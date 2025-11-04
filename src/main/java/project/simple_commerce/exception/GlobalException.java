package project.simple_commerce.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        return ErrorResponse.builder()
                .code(e.getStatusCode().value())
                .message(e.getBindingResult().getFieldError().getDefaultMessage())
                .build();
    }
}
