package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.dto.OrderDto;
import com.ecommerce.sbecom.dto.OrderRequestDto;
import com.ecommerce.sbecom.dto.ProductResponse;
import com.ecommerce.sbecom.exception.APIException;
import com.ecommerce.sbecom.model.*;
import com.ecommerce.sbecom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    @Transactional
    @Override
    public OrderDto placeOrder(
            OrderRequestDto orderRequestDto,
            UUID userId,
            String email,
            PaymentMethod paymentMethod) {
        //getting the user cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException("Cart not found for user"));
        if (cart.getCartItemList() == null || cart.getCartItemList().isEmpty()) {
            throw new APIException("Cart is empty");
        }

        Address address = addressRepository.findById(orderRequestDto.getAddressId())
                .orElseThrow(() -> new APIException("Address not found"));
        // 3. Determine order status based on payment method
        OrderStatus orderStatus = (paymentMethod == PaymentMethod.COD)
                ? OrderStatus.CONFIRMED
                : OrderStatus.PENDING_PAYMENT;

        // 4. Create Order
        Order order = Order.builder()
                .email(email)
                .address(address)
                .orderDateTime(LocalDateTime.now())
                .status(orderStatus)
                .totalAmount(cart.getTotalPrice()!=null ? cart.getTotalPrice():0.0)
                .build();
        // 5. Create Payment
        // 5. Create Payment
        PaymentStatus paymentStatus = (paymentMethod == PaymentMethod.COD)
                ? PaymentStatus.COD_PENDING
                : PaymentStatus.PENDING;
        Payment payment = Payment.builder()
                .paymentMethod(orderRequestDto.getPaymentMethod())
//                       .amount(orderRequestDto.gett)
                .paymentStatus(orderRequestDto.getPaymentStatus())
                .pgPaymentId(orderRequestDto.getPgPaymentId())
                .pgStatus(orderRequestDto.getPgStatus())
                .pgName(orderRequestDto.getPgName())
                .pgResponseMessage(orderRequestDto.getPgResponseMessage())
                .order(order)
                .build();
        Payment save = paymentRepository.save(payment);
        order.setPayment(save);
        Order savedOrder = orderRepository.save(order);
        List<CartItem> cartItemList = cart.getCartItemList();
        List<OrderItem> orderItems=new ArrayList<>();
        for(CartItem cartItem:cartItemList){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrderedProductPrice(cartItem.getProduct().getPrice()*cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProduct().getPrice()*cartItem.getQuantity()-cartItem.getDiscount());
            orderItem.setOrder(savedOrder);
          orderItems.add(orderItem);
        }

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItemList().forEach(item->{
            Integer quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
            //clear cart
            cartRepository.deleteByUserId(userId);
        });


        //create a new order with payment info
        //get items from cart and add it to order items
        //decrease the product quantity
        //clear the cart
        //save the order
        //send back order summary

        return null;
    }
}
