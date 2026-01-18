package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.CartDto;
import com.ecommerce.sbecom.exception.APIException;
import com.ecommerce.sbecom.exception.ProductAlreadyInCartException;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

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

        return CartDto.builder().cartId(cart.getId()).totalPrice(cart.getTotalPrice()).build();
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

