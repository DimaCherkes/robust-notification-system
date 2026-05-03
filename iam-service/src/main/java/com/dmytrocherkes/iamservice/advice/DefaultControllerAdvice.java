package com.dmytrocherkes.iamservice.advice;

import com.dmytrocherkes.iamservice.model.exception.CustomAccessDeniedException;
import com.dmytrocherkes.iamservice.model.exception.DataExistException;
import com.dmytrocherkes.iamservice.model.exception.InvalidPasswordException;
import com.dmytrocherkes.iamservice.model.exception.NotFoundException;
import com.dmytrocherkes.iamservice.model.response.DefaultApiResponse;
import com.dmytrocherkes.iamservice.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class DefaultControllerAdvice {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOGIN_PATH = "/api/v1/iam-service/auth/login";
    private static final String REGISTER_PATH = "/api/v1/iam-service/auth/register";

    private final JwtTokenProvider jwtTokenProvider;

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    protected ResponseEntity<? extends Serializable> handleNotFoundException(NotFoundException ex) {
        log.warn(ex.getMessage());

        return new ResponseEntity<>(
                DefaultApiResponse.createFailureResponse(ex.getMessage()),
                HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value())
        );
    }

    @ExceptionHandler(DataExistException.class)
    @ResponseBody
    protected ResponseEntity<? extends Serializable> handleDataExistsException(DataExistException ex) {
        log.warn(ex.getMessage());

        return new ResponseEntity<>(
                DefaultApiResponse.createFailureResponse(ex.getMessage()),
                HttpStatusCode.valueOf(HttpStatus.CONFLICT.value())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    protected ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String errorMessage = error.getDefaultMessage();
            errors.put("error", errorMessage);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseBody
    protected ResponseEntity<? extends Serializable> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        log.warn(ex.getMessage());

        String requestURI = request.getRequestURI();
        if (isAuthEndpoint(requestURI)) {
            Optional<String> expiredToken = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                    .map(header -> header.substring(BEARER_PREFIX.length()));

            if (expiredToken.isPresent()) {
                String refreshedToken = jwtTokenProvider.refreshToken(expiredToken.get());
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTHORIZATION_HEADER, BEARER_PREFIX + refreshedToken);
                return new ResponseEntity<>(headers, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(
                DefaultApiResponse.createFailureResponse(ex.getMessage()),
                HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value())
        );
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    @ResponseBody
    protected ResponseEntity<? extends Serializable> handleSignatureException(Exception ex) {
        log.warn(ex.getMessage());

        return new ResponseEntity<>(
                DefaultApiResponse.createFailureResponse(ex.getMessage()),
                HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value())
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleInvalidPasswordException(InvalidPasswordException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(CustomAccessDeniedException.class)
    @ResponseBody
    protected ResponseEntity<? extends Serializable> handleAccessDeniedException(CustomAccessDeniedException ex) {
        log.warn(ex.getMessage());

        return new ResponseEntity<>(
                DefaultApiResponse.createFailureResponse(ex.getMessage()),
                HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ResponseEntity<? extends Serializable> handleException(Exception ex) {
        log.error(ex.getMessage());

        return new ResponseEntity<>(
                DefaultApiResponse.createFailureResponse(ex.getMessage()),
                HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
    }

    private boolean isAuthEndpoint(String uri) {
        return uri.equals(LOGIN_PATH) || uri.equals(REGISTER_PATH);
    }
}
