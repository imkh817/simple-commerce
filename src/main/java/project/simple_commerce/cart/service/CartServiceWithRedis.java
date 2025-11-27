package project.simple_commerce.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.cart.dto.CreateCartRequestDto;
import project.simple_commerce.cart.dto.CreateCartResponseDto;
import project.simple_commerce.cart.repository.CartRepository;
import project.simple_commerce.cartItem.repository.CartItemRepository;
import project.simple_commerce.item.repository.ItemRepository;
import project.simple_commerce.member.repository.MemberRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceWithRedis {

    private final ItemRepository itemRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @Transactional
    public void addCart(CreateCartRequestDto cartRequestDto){
        String key = getCartKey(cartRequestDto.memberId());
        String hashKey = String.valueOf(cartRequestDto.itemId());

        Integer currentQuantity = (Integer) redisTemplate.opsForHash().get(key, hashKey);
        int newQuantity = (currentQuantity == null ? 0 : currentQuantity) + cartRequestDto.quantity();

        redisTemplate.opsForHash().put(key, hashKey, newQuantity);
        redisTemplate.expire(key, Duration.ofDays(30));

    }

    private String getCartKey(Long memberId){
        return "cart:" + memberId;
    }

}
