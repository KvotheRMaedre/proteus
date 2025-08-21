package tech.kvothe.proteus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.kvothe.proteus.exception.ProteusException;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ProteusException.class)
    public ProblemDetail handleProteusException(ProteusException proteusException){
        return proteusException.toProblemDetail();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        var fieldErrors = methodArgumentNotValidException.getFieldErrors()
                .stream()
                .map(fieldError -> new InvalidParam(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Your request parameters didn't validate.");
        problemDetail.setProperty("invalid-params", fieldErrors);

        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleMethodArgumentNotValidException(AuthenticationException authenticationException) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Incorrect credentials");
        problemDetail.setDetail(authenticationException.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException){
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Parse error");
        problemDetail.setDetail(httpMessageNotReadableException.getMessage());

        return problemDetail;
    }

    private record InvalidParam(String name, String reason){}

}
