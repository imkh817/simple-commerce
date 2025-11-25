package project.simple_commerce.cart.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.simple_commerce.cart.dto.CreateCartRequestDto;
import project.simple_commerce.cart.dto.CreateCartResponseDto;
import project.simple_commerce.cart.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CreateCartResponseDto createCart(CreateCartRequestDto createCartRequestDto){
        return cartService.addCart(createCartRequestDto);
    }
}

