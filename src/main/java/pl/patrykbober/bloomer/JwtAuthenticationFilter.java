package pl.patrykbober.bloomer;

import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/token", "POST");

    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtDecoder jwtDecoder, UserDetailsService userDetailsService) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        var grantType = obtainGrantType(request);
        if ("password".equals(grantType)) {
            var username = obtainUsername(request);
            var password = obtainPassword(request);
            var authRequest = new UsernamePasswordAuthenticationToken(username, password);
            return this.getAuthenticationManager().authenticate(authRequest);
        } else if ("refresh_token".equals(grantType)) {
            var refreshToken = obtainRefreshToken(request);
            var token = jwtDecoder.decode(refreshToken);
            var user = userDetailsService.loadUserByUsername(token.getSubject());
            return new UsernamePasswordAuthenticationToken(token.getSubject(), null, user.getAuthorities());
        } else {
            throw new AuthenticationServiceException("Grant type not supported: " + grantType);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
        }

        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();
        this.logger.trace("Failed to process authentication request", failed);
        this.logger.trace("Cleared SecurityContextHolder");
        this.logger.trace("Handling authentication failure");

        this.logger.debug("Sending 401 Unauthorized error");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
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

}
