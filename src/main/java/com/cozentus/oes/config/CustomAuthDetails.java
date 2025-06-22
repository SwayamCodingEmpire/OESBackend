package com.cozentus.oes.config;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public record CustomAuthDetails(WebAuthenticationDetails webDetails, Integer userId) {}

