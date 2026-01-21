package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.config.PricingService;
import com.ecommerce.sbecom.dto.CartDto;
import com.ecommerce.sbecom.dto.ProductDto;
import com.ecommerce.sbecom.exception.APIException;
import com.ecommerce.sbecom.exception.ResourceNotFoundException;
import com.ecommerce.sbecom.model.Cart;
import com.ecommerce.sbecom.model.CartItem;
import com.ecommerce.sbecom.model.Product;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.repository.CartItemRepository;
import com.ecommerce.sbecom.repository.CartRepository;
import com.ecommerce.sbecom.repository.ProductRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final PricingService pricingService;

    @Override
    @Transactional
    public CartDto addProductToCart(UUID productId, Integer quantity, Authentication authentication) {
        //finding the existing cart or creating the new cart for user
        Cart cart = getOrCreateCart(authentication.getName());

        //finding the product if doesn't eixts then throw error
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Not Found With id : " + productId));
        // 2. Check karo product pehle se cart mein hai ya nahi
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getId(), productId);
        // 3. Final Quantity calculate karo
        int finalQuantity = (cartItem != null) ? (cartItem.getQuantity() + quantity) : quantity;
        // 4. Common Validation: Ek hi jagah stock check
        if (product.getQuantity() < finalQuantity) {
            throw new APIException("Stock insufficient for " + product.getProductName());
        }
        // 5. CartItem ko Update ya Create karo
        if (cartItem != null) {
            cartItem.setQuantity(finalQuantity);
            cartItem.setSellingPrice(product.getPrice() * finalQuantity);
        } else {
            cartItem = CartItem.builder()
                    .product(product).cart(cart)
                    .quantity(quantity).sellingPrice(product.getPrice() * quantity)
                    .build();
        }
        cartItemRepository.save(cartItem);
        // 6. Cart ka Total Price Update karo
        Double currentTotal = (cart.getTotalPrice() != null) ? cart.getTotalPrice() : 0.0;
        cart.setTotalPrice(currentTotal + (product.getPrice() * quantity));
        cartRepository.save(cart);
        List<ProductDto> list = cart.getCartItemList().stream()
                .map(item -> ProductDto.builder()
                        .productId(item.getProduct().getId().toString())
                        .productName(item.getProduct().getProductName())
                        .price(item.getProduct().getPrice())
                        .specialPrice(item.getProduct().getSpecialPrice())
                        .quantity(item.getQuantity())
                        .build()).toList();
        return CartDto.builder()
                .cartId(cart.getId()).
                totalPrice(cart.getTotalPrice())
                .quantity(cart.getTotalItems())

                .productDtoList(list)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartDto> getAllCarts() {

        List<Cart> all = cartRepository.findAll();
        return all.stream().map(cart -> CartDto.builder().cartId(cart.getId())
                .price(cart.getTotalPrice())
                .quantity(cart.getTotalItems())

                .productDtoList(cart.getCartItemList().stream().map(item -> ProductDto.builder()
                                .productId(item.getProduct().getId().toString())
                                .productName(item.getProduct().getProductName())
                                .price(item.getProduct().getPrice())
                                .specialPrice(item.getProduct().getSpecialPrice())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build()).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CartDto getCart(UUID id, String email) {
        Cart cart = cartRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));


        List<ProductDto> list = cart.getCartItemList().stream().map(item -> {
            Double livePrice = pricingService.calculateLivePrice(item.getProduct());
            return ProductDto.builder()
                    .productId(item.getProduct().getId().toString())
                    .productName(item.getProduct().getProductName())
                    .price(item.getSellingPrice()) // Purani price (snapshot)
                    .price(livePrice)
                    .price(livePrice)        // Nayi price (live)
                    .quantity(item.getQuantity())
                    .build();
        }).toList();

        return CartDto.builder()
                .cartId(cart.getId())
                .price(cart.getTotalPrice())
                .quantity(cart.getTotalItems())
                .productDtoList(list)
                .build();

    }

    @Transactional
    @Override
    public CartDto updateProudctQuantityInCart(UUID userId, UUID productId, String action) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user :" + userId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getId(), productId);
        Integer currentQty = cartItem.getQuantity();
        Integer newQty;
        // 3️⃣ Decide new quantity
        if ("INCREASE".equalsIgnoreCase(action)) {
            newQty = currentQty + 1;
        } else if ("DECREASE".equalsIgnoreCase(action)) {
            newQty = currentQty - 1;
        } else {
            throw new APIException("Invalid action");
        }
        // 4️⃣ Stock validation
        if (newQty > cartItem.getProduct().getQuantity()) {
            throw new APIException("Insufficient stock");
        }
        // 5️⃣ If quantity = 0 → remove item
        if (newQty == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(newQty);
            cartItem.setSellingPrice(cartItem.getProduct().getPrice() * newQty);
            cartItemRepository.save(cartItem);
        }
        double total = cart.getCartItemList().stream().mapToDouble(CartItem::getSellingPrice).sum();
        cart.setTotalPrice(total);
        Cart updatedCart = cartRepository.save(cart);
        return CartDto.builder()
                .cartId(updatedCart.getId())
                .quantity(updatedCart.getTotalItems())
                .price(updatedCart.getTotalPrice())
                .productDtoList(updatedCart.getCartItemList().stream().map(item -> ProductDto.builder()
                                .productId(item.getProduct().getId().toString())
                                .productName(item.getProduct().getProductName())
                                .price(item.getProduct().getPrice())
                                .specialPrice(item.getProduct().getSpecialPrice())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build();
    }

    @Transactional
    @Override
    public CartDto deleteProductFromCart(UUID userId, UUID productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user :" + userId));

        CartItem cartItem = cartItemRepository
                .findOptionalByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found in cart")
                );

        // 🔥 orphanRemoval does the delete
        cart.getCartItemList().remove(cartItem);

        double total = cart.getCartItemList().stream()
                .mapToDouble(CartItem::getSellingPrice)
                .sum();

        cart.setTotalPrice(total);
        cart.setTotalItems(cart.getCartItemList().size());
        cartRepository.save(cart);

        return CartDto.builder()
                .cartId(cart.getId())
                .price(cart.getTotalPrice())
                .quantity(cart.getTotalItems())
                .productDtoList(cart.getCartItemList().stream()
                        .map(item -> ProductDto.builder()
                                .productId(item.getProduct().getId().toString())
                                .productName(item.getProduct().getProductName())
                                .price(item.getProduct().getPrice())
                                .specialPrice(item.getProduct().getSpecialPrice())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build();

    }


    private Cart getOrCreateCart(String email) {
        // 1. Check database
        Cart cart = cartRepository.findCartByUserEmail(email);

        // 2. If not found, create new
        if (cart == null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            cart = Cart.builder()
                    .user(user)
                    .totalPrice(0.0)
                    .isActive(true)
                    .build();

            // 3. Save to DB
            return cartRepository.save(cart);
        }

        // 4. Return found cart

        return cart;
    }

}

