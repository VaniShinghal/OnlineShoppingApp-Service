package com.cts.onlineShopping.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.onlineShopping.model.Admin;
import com.cts.onlineShopping.model.Customer;
import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService{

	@Autowired
	CustomerRepo custRepo;
	
	@Autowired
	AdminRepo adminRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<Customer> cust = custRepo.findById(username);
		Optional<Admin> admin = adminRepo.findById(username);
		if(cust.isPresent()) {
			return new User(cust.get().getLoginId(), cust.get().getPassword(), new ArrayList<>());
		}else if(admin.isPresent()) {
			return new User(admin.get().getEmail(), admin.get().getPassword(), new ArrayList<>());
		}
		
		return new User("", "", new ArrayList<>());
	}
	
	
	
}
