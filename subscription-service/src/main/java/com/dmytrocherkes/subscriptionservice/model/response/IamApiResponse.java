package com.dmytrocherkes.subscriptionservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IamApiResponse<T> {
    private String message;
    private T body;
    private boolean success;
}
