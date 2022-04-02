package pl.patrykbober.bloomer.common.security;

import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RequestMatcher authenticationRequestMatcher = new AntPathRequestMatcher("/token", "POST");
    private final AuthenticationEntryPoint authenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();
    private final AuthenticationFailureHandler authenticationFailureHandler = (request, response, exception) -> {
        if (exception instanceof AuthenticationServiceException) {
            throw exception;
        } else {
            this.authenticationEntryPoint.commence(request, response, exception);
        }
    };

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!authenticationRequestMatcher.matches(request)) {
            chain.doFilter(request, response);
            return;
        }

        var grantType = obtainGrantType(request);
        if ("password".equals(grantType)) {
            var username = obtainUsername(request);
            var password = obtainPassword(request);
            if (username.isEmpty() || password.isEmpty()) {
                sendBadRequest(response, "Credentials cannot be empty");
                return;
            }
            var authRequest = new UsernamePasswordAuthenticationToken(username, password);
            try {
                var authenticationResult = authenticationManager.authenticate(authRequest);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationResult);
                SecurityContextHolder.setContext(context);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authenticationResult));
                }

                chain.doFilter(request, response);
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                this.logger.trace("Failed to process authentication request", e);
                this.authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            }
        } else if ("refresh_token".equals(grantType)) {
            var refreshToken = obtainRefreshToken(request);
            if (refreshToken.isEmpty()) {
                sendBadRequest(response, "Refresh token cannot be empty");
                return;
            }
            var authRequest = new BearerTokenAuthenticationToken(refreshToken);
            try {
                var authenticationResult = authenticationManager.authenticate(authRequest);
                var user = userDetailsService.loadUserByUsername(authenticationResult.getName());
                authenticationResult = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationResult);
                SecurityContextHolder.setContext(context);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authenticationResult));
                }

                chain.doFilter(request, response);
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                this.logger.trace("Failed to process authentication request", e);
                this.authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            }
        } else {
            sendBadRequest(response, "Grant type not supported: " + grantType);
        }
    }

    private String obtainGrantType(HttpServletRequest request) {
        var grantTypeParameter = "grant_type";
        var grantType = request.getParameter(grantTypeParameter);
        grantType = grantType != null ? grantType : "";
        grantType = grantType.trim();
        return grantType;
    }

    private String obtainUsername(HttpServletRequest request) {
        var usernameParameter = "username";
        var username = request.getParameter(usernameParameter);
        username = username != null ? username : "";
        username = username.trim();
        return username;
    }

    private String obtainPassword(HttpServletRequest request) {
        var passwordParameter = "password";
        var password = request.getParameter(passwordParameter);
        password = password != null ? password : "";
        password = password.trim();
        return password;
    }

    private String obtainRefreshToken(HttpServletRequest request) {
        var refreshTokenParameter = "refresh_token";
        var refreshToken = request.getParameter(refreshTokenParameter);
        refreshToken = refreshToken != null ? refreshToken : "";
        refreshToken = refreshToken.trim();
        return refreshToken;
    }

    private void sendBadRequest(HttpServletResponse response, String msg) throws IOException {
        this.logger.trace("Failed to process authentication request: " + msg);
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
    }
}
