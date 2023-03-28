package com.cts.onlineShopping.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cts.onlineShopping.filter.JwtFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.accept.ContentNegotiationStrategy;

@SuppressWarnings("deprecation")
@ContextConfiguration(classes = { SecurityConfig.class, AuthenticationManagerBuilder.class,
		AuthenticationConfiguration.class })
@ExtendWith(SpringExtension.class)
class SecurityConfigTest {

	@MockBean
	private AuthenticationTrustResolver authenticationTrustResolver;

	@MockBean
	private ContentNegotiationStrategy contentNegotiationStrategy;

	@MockBean
	private JwtFilter jwtFilter;

	@Autowired
	private SecurityConfig securityConfig;

	@MockBean
	private UserDetailsService userDetailsService;

	@Test
	void testPasswordEncoder() {
		assertTrue(securityConfig.passwordEncoder() instanceof NoOpPasswordEncoder);
	}

	@Test
	void testAuthenticationManagerBean() throws Exception {
		assertTrue(securityConfig.authenticationManagerBean() instanceof ProviderManager);
	}

}
