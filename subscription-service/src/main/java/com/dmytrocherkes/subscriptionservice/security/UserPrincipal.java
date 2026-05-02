package com.dmytrocherkes.subscriptionservice.security;

import java.io.Serializable;

public record UserPrincipal(Integer id, String email) implements Serializable {
}
