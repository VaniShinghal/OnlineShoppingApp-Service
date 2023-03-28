package com.cts.onlineShopping.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cts.onlineShopping.service.UserDetailsService;
import com.cts.onlineShopping.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
@Generated
@Component
public class JwtFilter extends OncePerRequestFilter{
	
	@Autowired
	private UserDetailsService myUserDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		final String authorizationHeader = request.getHeader("Authorization");
		System.out.println("From filter " + authorizationHeader);
		String username = null;
		String jwt = null;
		
		try {
		if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			System.out.println("Inside filter first if");
			jwt = authorizationHeader.substring(7);
			username = jwtUtil.extractUsername(jwt);
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(username);
			
			if(jwtUtil.validateToken(jwt, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		
		filterChain.doFilter(request, response);
		}catch(ExpiredJwtException e) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.getWriter().print("Session Expired, Login Again");
		}
	}
	
	

}
