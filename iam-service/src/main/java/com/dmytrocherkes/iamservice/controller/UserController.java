package com.dmytrocherkes.iamservice.controller;

import com.dmytrocherkes.iamservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.iamservice.model.dto.user.UserDTO;
import com.dmytrocherkes.iamservice.model.dto.user.UserSearchDTO;
import com.dmytrocherkes.iamservice.model.request.NewUserRequest;
import com.dmytrocherkes.iamservice.model.request.UpdateUserRequest;
import com.dmytrocherkes.iamservice.model.request.UserSearchRequest;
import com.dmytrocherkes.iamservice.model.response.DefaultApiResponse;
import com.dmytrocherkes.iamservice.model.response.PaginationResponse;
import com.dmytrocherkes.iamservice.service.UserServiceImpl;
import com.dmytrocherkes.iamservice.utils.ApiUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = UserController.ENDPOINT_BASE_PATH)
@Tag(name = "User Controller", description = "Endpoints for user management")
public class UserController {

    public static final String ENDPOINT_BASE_PATH = ApiPath.BASE_PATH + ApiPath.API_USERS_PATH;
    private final UserServiceImpl userService;

    @GetMapping(ApiPath.API_ID_PATH)
    public ResponseEntity<DefaultApiResponse<UserDTO>> getUserById(@PathVariable(name = "id") Integer userId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO user = userService.getById(userId);
        return ResponseEntity.ok(DefaultApiResponse.createSuccessfulResponse(user));
    }

    @GetMapping(ApiPath.API_USERNAME_PATH)
    public ResponseEntity<DefaultApiResponse<UserDTO>> getUserByUsername(@PathVariable String username) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO user = userService.getByUsername(username);
        return ResponseEntity.ok(DefaultApiResponse.createSuccessfulResponse(user));
    }

    @PostMapping(ApiPath.API_CREATE_PATH)
    public ResponseEntity<DefaultApiResponse<UserDTO>> createUser(
            @RequestBody @Valid NewUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO user = userService.createUser(request);
        return ResponseEntity.ok(DefaultApiResponse.createSuccessfulResponse(user));
    }

    @PutMapping(ApiPath.API_ID_PATH)
    public ResponseEntity<DefaultApiResponse<UserDTO>> updatePost(
            @PathVariable(name = "id") Integer userId,
            @RequestBody @Valid UpdateUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        UserDTO userDTO = userService.updateUser(userId, request);
        return ResponseEntity.ok(DefaultApiResponse.createSuccessfulResponse(userDTO));
    }

    @DeleteMapping(ApiPath.API_ID_PATH)
    public ResponseEntity<Void> deleteById(
            @PathVariable(name = "id") Integer userId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiPath.API_ALL_PATH)
    public ResponseEntity<DefaultApiResponse<PaginationResponse<UserSearchDTO>>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        PaginationResponse<UserSearchDTO> allUsers = userService.findAllUsers(pageable);
        DefaultApiResponse<PaginationResponse<UserSearchDTO>> response = DefaultApiResponse.createSuccessfulResponse(allUsers);
        return ResponseEntity.ok(response);
    }

    @PostMapping(ApiPath.API_SEARCH_PATH)
    public ResponseEntity<DefaultApiResponse<PaginationResponse<UserSearchDTO>>> searchUsers(
            @RequestBody @Valid UserSearchRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        PaginationResponse<UserSearchDTO> userSearchDTOPaginationResponse = userService.searchUsers(request, pageable);
        DefaultApiResponse<PaginationResponse<UserSearchDTO>> response = DefaultApiResponse.createSuccessfulResponse(userSearchDTOPaginationResponse);
        return ResponseEntity.ok(response);
    }
}

