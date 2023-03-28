package com.cts.onlineShopping.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cts.onlineShopping.filter.JwtFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	private UserDetailsService myUserDetailsService;
	
	@Autowired
	public void ConfigureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManager();
	}
	
//	@Override
//	public void configure(WebSecurity websecurity) {
//		websecurity.ignoring().antMatchers("/swagger-resources/", "/webjars/") .antMatchers(HttpMethod.OPTIONS, "/**");
//	}
	
	@Override
	protected void configure(HttpSecurity httpsecurity) throws Exception {
		httpsecurity.csrf().disable().authorizeRequests().antMatchers("/actuator","/api/v1.0/shopping/register","/api/v1.0/shopping/login","/api/v1.0/shopping/{customername}/forgot","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html", "/actuator/**")
			.permitAll().anyRequest().authenticated().and().sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		httpsecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		httpsecurity.cors();
	}
	
	
//	@Bean
//	CorsConfigurationSource corsConfigurationSource() {
//	    UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
//	    final String headers =  "Authorization, Access-Control-Allow-Origin, Access-Control-Request-Method, Access-Control-Allow-Headers, "+
//	                            "Origin, Accept, X-Requested-With, Content-Type, " + 
//	                            "Access-Control-Request-Method, Custom-Filter-Header";
////	    
//	    CorsConfiguration config = new CorsConfiguration();
//	    
//	    config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS","HEAD","PATCH")); // Required for PUT method
//	    config.addExposedHeader(headers);
//	    config.addAllowedHeader(headers);
////	    config.setAllowCredentials(true);
//	    config.applyPermitDefaultValues();
////	    config.addAllowedOriginPattern("*");
//	    
////	    
////	    config.addAllowedHeader("Access-Control-Allow-Origin");
////	    config.addExposedHeader("Access-Control-Allow-Origin");
//	    source.registerCorsConfiguration("/**", config);
//	    
//	    return source;
//	}
	
	
}
