package com.dmytrocherkes.iamservice.controller;

public interface ApiPath {

    String BASE_PATH = "/api/v1/iam-service";

    String API_ID_PATH = "/{id}";
    String API_USERNAME_PATH = "/by-username/{username}";
    String API_CREATE_PATH = "/create";
    String API_ALL_PATH = "/all";
    String API_SEARCH_PATH = "/search";
    String API_USERS_PATH = "/users";
    String API_AUTH_PATH = "/auth";
    String API_REGISTER_PATH = "/register";
    String API_LOGIN_PATH = "/login";
    String API_REFRESH_TOKEN_PATH = "/refresh/token";

}
