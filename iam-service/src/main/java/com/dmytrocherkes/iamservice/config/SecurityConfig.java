package com.dmytrocherkes.iamservice.config;

import com.dmytrocherkes.iamservice.controller.ApiPath;
import com.dmytrocherkes.iamservice.security.filter.JwtRequestFilter;
import com.dmytrocherkes.iamservice.security.handler.AccessRestrictionHandler;
import com.dmytrocherkes.iamservice.service.impl.UserServiceImpl;
import com.dmytrocherkes.iamservice.service.model.IamServiceUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final AccessRestrictionHandler accessRestrictionHandler;

    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final AntPathRequestMatcher[] NOT_SECURED_URLS = new AntPathRequestMatcher[]{
            new AntPathRequestMatcher(ApiPath.BASE_PATH + ApiPath.API_AUTH_PATH + ApiPath.API_LOGIN_PATH, POST),
            new AntPathRequestMatcher(ApiPath.BASE_PATH + ApiPath.API_AUTH_PATH + ApiPath.API_REGISTER_PATH, POST),
            new AntPathRequestMatcher(ApiPath.BASE_PATH + ApiPath.API_AUTH_PATH + ApiPath.API_REFRESH_TOKEN_PATH, GET),

            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/webjars/**"),
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(NOT_SECURED_URLS).permitAll()

                        .requestMatchers(get("/posts/all")).hasAnyAuthority(adminAccessSecurityRoles())
                        .requestMatchers(get("/users/all")).hasAnyAuthority(adminAccessSecurityRoles())
                        .requestMatchers(post("/users/create")).hasAnyAuthority(adminAccessSecurityRoles())

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(accessRestrictionHandler)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserServiceImpl userService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private String[] adminAccessSecurityRoles() {
        return new String[]{
                IamServiceUserRole.SUPER_ADMIN.name(),
                IamServiceUserRole.ADMIN.name()
        };
    }

    private static AntPathRequestMatcher get(String pattern) {
        return new AntPathRequestMatcher(pattern, GET);
    }

    private static AntPathRequestMatcher post(String pattern) {
        return new AntPathRequestMatcher(pattern, POST);
    }

}
