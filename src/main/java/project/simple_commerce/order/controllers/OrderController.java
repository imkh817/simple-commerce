package project.simple_commerce.order.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.simple_commerce.order.dto.create.CreateOrderRequest;
import project.simple_commerce.order.service.OrderService;
import project.simple_commerce.order.dto.create.CreateOrderResponse;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest){
        return orderService.createOrder(createOrderRequest);
    }
}
