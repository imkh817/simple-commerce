package project.simple_commerce.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public void runRedisTest(){
        // 문자열을 다루는 도구를 꺼냄
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("spring-key", "hello-from-spring");

        String value = ops.get("spring-key");
        System.out.println("value = " + value);

        String terminalValue = ops.get("hello");
        System.out.println("terminalValue = " + terminalValue);
    }
}
