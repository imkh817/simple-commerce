package project.simple_commerce.cart.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import project.simple_commerce.cart.dto.CartItemDto;
import project.simple_commerce.cart.dto.CartResponseDto;
import project.simple_commerce.cart.entity.Cart;
import project.simple_commerce.cart.repository.CartRepository;
import project.simple_commerce.cartItem.entity.CartItem;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Test
    @DisplayName(value = "장바구니 조회 (장바구니 생성 전)")
    void searchNonCreateCart(){
        // given
        Long memberId = 1L;

        // when
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.empty());

        // then
        CartResponseDto cartResponseDto = cartService.searchCart(memberId);

        Assertions.assertThat(cartResponseDto.memberId()).isEqualTo(memberId);
        Assertions.assertThat(cartResponseDto.cartItems()).isEmpty();
    }
    @Test
    @DisplayName(value = "장바구니 조회 (장바구니 생성 후, 상품이 없는 경우)")
    void searchCartNonItem(){
        // given
        Long memberId = 1L;

        CartItemDto cartItemDto1 = new CartItemDto(1L, "mac", 1000, 5000, 5);
        CartItemDto cartItemDto2 = new CartItemDto(2L, "mac2", 2000, 4000, 2);
        List<CartItemDto> cartItemDtos = List.of(cartItemDto1, cartItemDto2);

        CartResponseDto response = new CartResponseDto(memberId, cartItemDtos);
        Member member = new Member("조건희","123");
        ReflectionTestUtils.setField(member,"id", memberId);

        Cart cart = Cart.createCart(member);
        Item item = new Item("mac", 1000, 3);
        Item item2 = new Item("note", 2000, 5);
        CartItem cartItem1 = CartItem.createCartItem(cart, item, 2);
        CartItem cartItem2 = CartItem.createCartItem(cart, item, 3);



        // when
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        // then
        CartResponseDto cartResponseDto = cartService.searchCart(memberId);

        Assertions.assertThat(cartResponseDto.memberId()).isEqualTo(memberId);
        Assertions.assertThat(cartResponseDto.cartItems()).isEmpty();
    }

    @Test
    @DisplayName(value = "장바구니 조회 (장바구니 생성 후, 상품이 있는 경우)")
    void searchCartWithItem(){
        // given
        Long memberId = 1L;

        Member member = new Member("조건희","123");
        ReflectionTestUtils.setField(member,"id", memberId);

        // 장바구니 생성
        Cart cart = Cart.createCart(member);

        // 상품 생성
        Item item = new Item("mac", 1000, 3);
        Item item2 = new Item("note", 2000, 5);

        // 장바구니 상품 생성
        CartItem cartItem1 = CartItem.createCartItem(cart, item, 2);
        CartItem cartItem2 = CartItem.createCartItem(cart, item2, 3);
        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);


        // when
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        // then
        CartResponseDto cartResponseDto = cartService.searchCart(memberId);

        Assertions.assertThat(cartResponseDto.memberId()).isEqualTo(memberId);
        Assertions.assertThat(cartResponseDto.cartItems().size()).isEqualTo(2);
    }

}