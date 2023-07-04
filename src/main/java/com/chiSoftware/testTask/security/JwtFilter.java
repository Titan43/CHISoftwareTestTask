package com.chiSoftware.testTask.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.chiSoftware.testTask.Constants.AUTH_PATH;
import static com.chiSoftware.testTask.Constants.REGISTER_PATH;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private final UserDetailsService userDetailsService;
    @Autowired
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String username;
        final String jwtToken;

        final String path = request.getServletPath();

        if(path.contains(AUTH_PATH) ||
                path.contains(REGISTER_PATH)){
            filterChain.doFilter(request, response);
            return;
        }

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token is missing");
            response.getWriter().flush();
            return;
        }
        jwtToken = authHeader.substring(7);
        try {
            username = jwtUtil.extractUsername(jwtToken);
        }
        catch (ExpiredJwtException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token is expired");
            response.getWriter().flush();
            return;
        }
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            boolean isTokenValid;
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                isTokenValid = jwtUtil.validateToken(jwtToken, userDetails);
                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            catch (UsernameNotFoundException e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Owner of this token does not exist");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}