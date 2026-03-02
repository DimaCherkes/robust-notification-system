package com.dmytrocherkes.iamservice.controller;

import com.dmytrocherkes.iamservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.iamservice.model.constants.ApiMessage;
import com.dmytrocherkes.iamservice.model.dto.user.UserProfileDTO;
import com.dmytrocherkes.iamservice.model.request.LoginRequest;
import com.dmytrocherkes.iamservice.model.request.RegistrationUserRequest;
import com.dmytrocherkes.iamservice.model.response.DefaultApiResponse;
import com.dmytrocherkes.iamservice.service.impl.AuthServiceImpl;
import com.dmytrocherkes.iamservice.utils.ApiUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = AuthController.ENDPOINT_BASE_PATH)
public class AuthController {

    public static final String ENDPOINT_BASE_PATH = ApiPath.BASE_PATH + ApiPath.API_AUTH_PATH;
    private final AuthServiceImpl authService;

    @PostMapping(ApiPath.API_LOGIN_PATH)
    public ResponseEntity<DefaultApiResponse<UserProfileDTO>> login(
            @RequestBody @Valid LoginRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserProfileDTO userProfileDTO = authService.login(request);
        DefaultApiResponse<UserProfileDTO> result = DefaultApiResponse.createSuccessfulResponse(ApiMessage.USER_LOGIN_SUCCESSFUL.getMessage(), userProfileDTO);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getBody().getToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, authorizationCookie.toString());

        return ResponseEntity.ok().headers(headers).body(result);
    }

    @GetMapping(ApiPath.API_REFRESH_TOKEN_PATH)
    public ResponseEntity<DefaultApiResponse<UserProfileDTO>> refreshToken(
            @RequestParam(name = "token") String refreshToken,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserProfileDTO userProfileDTO = authService.refreshAccessToken(refreshToken);
        DefaultApiResponse<UserProfileDTO> result = DefaultApiResponse.createSuccessfulResponse(ApiMessage.USER_CREATED_OR_UPDATED.getMessage(), userProfileDTO);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(userProfileDTO.getToken());
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok(result);
    }

    @PostMapping(ApiPath.API_REGISTER_PATH)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationUserRequest request,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserProfileDTO userProfileDTO = authService.registerUser(request);
        DefaultApiResponse<UserProfileDTO> result = DefaultApiResponse.createSuccessfulResponse(ApiMessage.USER_CREATED_OR_UPDATED.getMessage(), userProfileDTO);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(userProfileDTO.getToken());
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok(result);
    }

}
