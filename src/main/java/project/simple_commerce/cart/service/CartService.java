package project.simple_commerce.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.cart.dto.CartResponseDto;
import project.simple_commerce.cart.dto.CreateCartRequestDto;
import project.simple_commerce.cart.dto.CreateCartResponseDto;
import project.simple_commerce.cart.entity.Cart;
import project.simple_commerce.cart.repository.CartRepository;
import project.simple_commerce.cartItem.entity.CartItem;
import project.simple_commerce.cartItem.repository.CartItemRepository;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.item.exception.NotFoundItemException;
import project.simple_commerce.item.repository.ItemRepository;
import project.simple_commerce.member.entity.Member;
import project.simple_commerce.member.exception.NotFoundMemberException;
import project.simple_commerce.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public CreateCartResponseDto addCart(CreateCartRequestDto cartRequestDto) {

        // 사용자의 장바구니가 존재하는지 확인
        Cart cart = cartRepository.findByMemberId(cartRequestDto.memberId())
                .orElseGet(() -> {
                    // 존재하지 않으면 장바구니 새로 생성
                    Member member = memberRepository.findById(cartRequestDto.memberId())
                            .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다 ID: " + cartRequestDto.memberId()));
                    Cart createCart = Cart.createCart(member);
                    return cartRepository.save(createCart);
                });

        Item item = itemRepository.findById(cartRequestDto.itemId())
                .orElseThrow(() -> new NotFoundItemException("상품을 찾을 수 없습니다 ID: " + cartRequestDto.itemId()));

        // 장바구니에 상품이 등록되어 있는지 확인
        CartItem savedCartItem = cartItemRepository.findByCartAndItem(cart, item);

        // 등록되어있으면 수량 증가
        if (savedCartItem != null) {
            savedCartItem.addQuantity(cartRequestDto.quantity());
            return CreateCartResponseDto.of(cart, item, savedCartItem.getQuantity(), false);
        } else {
            // 등록되어있지 않으면 장바구니 상품 등록 후 저장
            CartItem cartItem = CartItem.createCartItem(cart, item, cartRequestDto.quantity());
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
            return CreateCartResponseDto.of(cart, item, cartItem.getQuantity(), true);
        }
    }

    public CartResponseDto searchCart(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .map(CartResponseDto::from)
                .orElseGet(() -> CartResponseDto.empty(memberId));
    }
}
