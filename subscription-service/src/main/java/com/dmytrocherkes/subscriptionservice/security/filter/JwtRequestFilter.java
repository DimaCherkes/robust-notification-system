package com.dmytrocherkes.subscriptionservice.security.filter;

import com.dmytrocherkes.subscriptionservice.security.JwtTokenProvider;
import com.dmytrocherkes.subscriptionservice.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            if (jwtTokenProvider.validateToken(jwt)) {
                username = jwtTokenProvider.getUserEmail(jwt);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Integer userId = jwtTokenProvider.getUserId(jwt);
            List<String> roles = jwtTokenProvider.getRoles(jwt);
            
            UserPrincipal principal = new UserPrincipal(userId, username);
            
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal, null, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        chain.doFilter(request, response);
    }
}
