package com.dmytrocherkes.subscriptionservice.service;

import com.dmytrocherkes.subscriptionservice.config.FeignConfig;
import com.dmytrocherkes.subscriptionservice.model.dto.UserDTO;
import com.dmytrocherkes.subscriptionservice.model.response.IamApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "iam-service", 
    url = "${app.services.iam-service.url}",
    configuration = FeignConfig.class
)
public interface IamClient {

    @GetMapping("/api/v1/iam-service/users/{id}")
    IamApiResponse<UserDTO> getUserById(@PathVariable Integer id);

    @GetMapping("/api/v1/iam-service/users/by-username/{username}")
    IamApiResponse<UserDTO> getUserByUsername(@PathVariable String username);

}
