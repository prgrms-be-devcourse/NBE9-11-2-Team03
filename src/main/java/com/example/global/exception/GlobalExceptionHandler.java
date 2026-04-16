package com.example.global.exception;

import com.example.global.rsData.RsData;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //400 Bad Request (잘못된 요청값, 비즈니스상 잘못된 인자)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.example.global.rsData.RsData<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(RsData.fail(e.getMessage()));
    }

    // 404 Not Found (정상 요청이지만 대상 리소스를 찾을 수 없음)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Void>> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>("404", e.getMessage(), null));
    }

    // 404 Not Found (요청한 데이터를 찾을 수 없을 때 사용합니다.)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RsData<Void>> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RsData<>("404", e.getMessage(), null));
    }

    // 401 Unauthorized (로그인이 필요한데 회원 정보를 확인할 수 없을 때 사용합니다.)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<RsData<Void>> handleUnauthorizedException(UnauthorizedException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new RsData<>("401", e.getMessage(), null));
    }

    // 403 Forbidden (로그인은 했지만 해당 작업 권한이 없을 때 사용합니다.)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<RsData<Void>> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new RsData<>("403", e.getMessage(), null));
    }

    // 409 Conflict (이미 신고한 리뷰처럼 같은 요청이 중복될 때 사용합니다.)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<RsData<Void>> handleConflictException(ConflictException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RsData<>("409", e.getMessage(), null));
    }

    // 400 Bad Request (삭제된 리뷰 신고처럼 요청 조건이 맞지 않을 때 사용합니다.)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<RsData<Void>> handleBadRequestException(BadRequestException e) {
        return ResponseEntity
                .badRequest()
                .body(new RsData<>("400", e.getMessage(), null));
    }

    // 400 Bad Request (@Valid 검증 실패 시 사용합니다.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(RsData.fail(message));
    }

    // 400 Bad Request (@RequestParam, @PathVariable 등의 제약조건 검증 실패)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RsData<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity
                .badRequest()
                .body(RsData.fail(e.getMessage()));
    }

    //400 Bad Request (JSON 형식 오류, 잘못된 요청 본문)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RsData<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity
                .badRequest()
                .body(RsData.fail("잘못된 요청 본문입니다."));
    }

    // 400 Bad Request(필수 요청 파라미터 누락)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RsData<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        String message = "필수 요청 파라미터가 누락되었습니다. parameter=" + e.getParameterName();

        return ResponseEntity
                .badRequest()
                .body(RsData.fail(message));
    }

    //405 Method Not Allowed (지원하지 않는 HTTP 메서드 호출)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RsData<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new RsData<>("405", "지원하지 않는 HTTP 메서드입니다.", null));
    }

     //처리되지 않은 예외의 최종 방어 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RsData<>("500", "서버 오류가 발생했습니다.", null));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    //429 Too Many Requests (외부 API 호출 한도 초과)
    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<RsData<Void>> handleTooManyRequests(HttpClientErrorException.TooManyRequests e) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new RsData<>("429", "외부 API 호출 한도를 초과했습니다.", null));
    }

    //TODOS: 시큐리티 도입 후, 관련 예외처리
    /* 추후 시큐리티 도입 시 확장
    //401 Unauthorized (인증 실패)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RsData<Void>> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new RsData<>("401", "인증이 필요합니다.", null));
    }

    // 403 Forbidden (인증은 되었지만 권한 부족)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RsData<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new RsData<>("403", "접근 권한이 없습니다.", null));
    }

     */
}
