package com.cozentus.oes.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.cozentus.oes.config.CustomAuthDetails;
import com.cozentus.oes.entities.UserInfo;
import com.cozentus.oes.repositories.UserInfoRepository;
import com.cozentus.oes.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserInfoRepository userInfoRepository;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(JwtService jwtservice, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver, UserInfoRepository userInfoRepository) {
        this.jwtService = jwtservice;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userInfoRepository = userInfoRepository; // Assuming userDetailsService is an instance of UserInfoRepository
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: {}", authHeader);
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        try{
            final String jwtToken = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwtToken);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (username != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    
                    UserInfo userInfo = userInfoRepository.findByEmailAndEnabledTrue(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    WebAuthenticationDetails webDetails = new WebAuthenticationDetailsSource().buildDetails(request);
                    CustomAuthDetails customDetails = new CustomAuthDetails(webDetails, userInfo.getId());
                    authenticationToken.setDetails(customDetails);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request,response);
        }
        catch(Exception e){
            logger.error(e.getMessage());
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }
}
