package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.config.PricingService;
import com.ecommerce.sbecom.controller.OrderStatusUpdateRequest;
import com.ecommerce.sbecom.dto.*;
import com.ecommerce.sbecom.exception.APIException;
import com.ecommerce.sbecom.exception.ResourceNotFoundException;
import com.ecommerce.sbecom.model.*;
import com.ecommerce.sbecom.payload.OrderResponse;
import com.ecommerce.sbecom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    private final OrderRepository orderRepository;

    private final PricingService pricingService;

    @Transactional
    @Override
    public OrderDto placeOrder(
            OrderRequestDto dto,
            UUID userId,
            String email,
            PaymentMethod paymentMethod) {
        // 1. Get Cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException("Cart not found for user"));
        if (cart.getCartItemList() == null || cart.getCartItemList().isEmpty()) {
            throw new APIException("Cart is empty");
        }
        // 2. Validate Address
        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> new APIException("Address not found"));


        // 4. Create Order
        Order order = Order.builder()
                .email(email)
                .userId(userId)
                .address(address)
                .orderDateTime(LocalDateTime.now())
                .status(dto.getPaymentMethod() == PaymentMethod.COD ? OrderStatus.CONFIRMED : OrderStatus.PENDING_PAYMENT)
                .totalAmount(0.0) // Initialize, we will sum it up
                .build();


        Payment payment = Payment.builder()
                .paymentMethod(dto.getPaymentMethod())
                .paymentStatus(dto.getPaymentMethod() == PaymentMethod.COD
                        ? PaymentStatus.COD_PENDING
                        : PaymentStatus.PENDING)
                .pgPaymentId(dto.getPgPaymentId())
                .pgStatus(dto.getPgStatus())
                .pgName(dto.getPgName())
                .pgResponseMessage(dto.getPgResponseMessage())
//                .amount(order.getTotalAmount())
                .order(order)
                .build();
        order.setPayment(payment);


// This list will store FINAL, IMMUTABLE order items
// Think: invoice line items
        List<OrderItem> orderItems = new ArrayList<>();
        // Order total must be CALCULATED, not trusted
// We will derive it from order items
        double orderTotal = 0;
        // Each CartItem represents "what user was planning to buy"
        for (CartItem cartItem : cart.getCartItemList()) {
            // Product is MASTER data (catalog item)
            // We only REFERENCE it, never modify its identity
            Product product = cartItem.getProduct();
            Integer quantity = product.getQuantity();
            // Warehouse check: do we actually have enough stock?
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new APIException("Product " + product.getProductName() + " has only " + quantity + " items left");
            }
            // NEVER read price directly from product
            // Price is a BUSINESS RULE, not a field
            Double price = pricingService.calculateLivePrice(product);
            // Line total = price × quantity − discount

            Double discount = cartItem.getDiscount() != null
                    ? cartItem.getDiscount()
                    : 0.0;
            double lineTotal = price * cartItem.getQuantity() - discount;
            // Create OrderItem = SNAPSHOT of CartItem
            OrderItem orderItem = OrderItem.builder()
                    // Link to parent order
                    .order(order)

                    // Reference product (do NOT copy product data)
                    .product(product)

                    // Freeze quantity
                    .quantity(cartItem.getQuantity())

                    // Freeze discount
                    .discount(discount)

                    // Freeze final price for this line
                    .orderedProductPrice(lineTotal)
                    .build();

            // Add line total to order total
            orderTotal += lineTotal;

            // Add order item to list
            orderItems.add(orderItem);

        }
        // Attach all order items to order
        order.setOrderItemList(orderItems);

// Set final totals
        order.setTotalAmount(orderTotal);
        payment.setAmount(orderTotal);


        // Single save call
// This will persist:
// - Order
// - Payment (cascade)
// - OrderItems (cascade)
        Order savedOrder = orderRepository.save(order);
        // Now that order is confirmed, update inventory
        for (CartItem cartItem : cart.getCartItemList()) {
            Product product = cartItem.getProduct();
            // Reduce stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }
        cart.getCartItemList().clear();
        cartRepository.save(cart);
// Think: "Invoice header"
        OrderDto orderDto = OrderDto.builder()
                .orderId(savedOrder.getId())           // Public reference for user
                .email(savedOrder.getEmail())           // Who placed the order
                .orderDate(savedOrder.getOrderDateTime()) // When order was placed
                .totalAmount(savedOrder.getTotalAmount()) // Final frozen amount
                .OrderStatus(savedOrder.getStatus().name()) // Current order state
                .addressId(savedOrder.getAddress().getId()) // Where it will be shipped
                .build();
// Think: "Invoice line items"
        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        for (OrderItem orderItem : savedOrder.getOrderItemList()) {

            OrderItemDto orderItemDto = OrderItemDto.builder()
                    .orderItemId(orderItem.getId())  // Line item ID
                    .quantity(orderItem.getQuantity()) // Frozen quantity
                    .discount(orderItem.getDiscount()) // Discount applied
                    .orderedProductPrice(orderItem.getOrderedProductPrice()) // Line total
                    .productDto(
                            ProductDto.builder()
                                    .productId(orderItem.getProduct().getId().toString())
                                    .productName(orderItem.getProduct().getProductName())
                                    .price(orderItem.getProduct().getPrice())
                                    .build()
                    )
                    .build();

            orderItemDtos.add(orderItemDto);
        }
        orderDto.setOrderItems(orderItemDtos);
// payment was already created earlier
        PaymentDto paymentDto = PaymentDto.builder()
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .pgPaymentId(payment.getPgPaymentId())
                .pgName(payment.getPgName())
                .pgStatus(payment.getPgStatus())
                .build();

        orderDto.setPaymentDto(paymentDto);


        return orderDto;
    }

    @Transactional
    @Override
    public void updateOrder(UUID orderId, OrderRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new APIException("Order not found"));

        // Update order status
        order.setStatus(OrderStatus.CONFIRMED);

        // Update payment details
        Payment payment = order.getPayment();
        payment.setPgPaymentId(dto.getPgPaymentId());
        payment.setPgStatus(dto.getPgStatus());
        payment.setPgName(dto.getPgName());
        payment.setPgResponseMessage(dto.getPgResponseMessage());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);

        orderRepository.save(order);
    }

    @Transactional
    @Override
    public List<OrderDto> getAllOrder(UUID userId) {
        List<Order> orders = orderRepository.getOrderByUserId(userId);

        return orders.stream().map(order -> {

            // Map Order Items
            List<OrderItemDto> orderItemDtos = order.getOrderItemList().stream()
                    .map(item -> OrderItemDto.builder()
                            .orderItemId(item.getId())
                            .quantity(item.getQuantity())
                            .discount(item.getDiscount())
                            .orderedProductPrice(item.getOrderedProductPrice())
                            .productDto(ProductDto.builder()
                                    .productId(item.getProduct().getId().toString())
                                    .productName(item.getProduct().getProductName())
                                    .price(item.getProduct().getPrice())
                                    .build())
                            .build()
                    ).toList();

            // Map Payment
            PaymentDto paymentDto = PaymentDto.builder()
                    .paymentMethod(order.getPayment().getPaymentMethod())
                    .paymentStatus(order.getPayment().getPaymentStatus())
                    .amount(order.getPayment().getAmount())
                    .pgPaymentId(order.getPayment().getPgPaymentId())
                    .pgName(order.getPayment().getPgName())
                    .pgStatus(order.getPayment().getPgStatus())
                    .build();

            // Map Order
            return OrderDto.builder()
                    .orderId(order.getId())
                    .email(order.getEmail())
                    .orderDate(order.getOrderDateTime())
                    .totalAmount(order.getTotalAmount())
                    .OrderStatus(order.getStatus().name())
                    .addressId(order.getAddress().getId())
                    .orderItems(orderItemDtos)
                    .paymentDto(paymentDto)
                    .build();

        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getAllOrderAdmin(
            UUID id,
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sortByAndOrder);
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<Order> listOrder = orderPage.getContent();

        List<OrderDto> list = listOrder.stream().map(order -> OrderDto.builder()
                .orderId(order.getId())
                .email(order.getEmail())
                .orderDate(order.getOrderDateTime())
                .totalAmount(order.getTotalAmount())
                .OrderStatus(order.getStatus().name())
                .addressId(order.getAddress().getId())
                .orderItems(order.getOrderItemList().stream().map(item -> OrderItemDto.builder()
                        .orderItemId(item.getId())
                        .quantity(item.getQuantity())
                        .discount(item.getDiscount())
                        .orderedProductPrice(item.getOrderedProductPrice())
                        .productDto(ProductDto.builder()
                                .productId(item.getProduct().getId().toString())
                                .productName(item.getProduct().getProductName())
                                .price(item.getProduct().getPrice())
                                .build())
                        .build()
                ).toList())
                .paymentDto(PaymentDto.builder()
                        .paymentMethod(order.getPayment().getPaymentMethod())
                        .paymentStatus(order.getPayment().getPaymentStatus())
                        .amount(order.getPayment().getAmount())
                        .pgPaymentId(order.getPayment().getPgPaymentId())
                        .pgName(order.getPayment().getPgName())
                        .pgStatus(order.getPayment().getPgStatus())
                        .build())
                .orderId(order.getId())
                .build()

        ).toList();


        return OrderResponse.builder()
                .orderDto(list)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .lastPage(orderPage.isLast())
                .build();

    }

    @Transactional
    @Override
    public void updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(request.getOrderStatus());
    }


}
