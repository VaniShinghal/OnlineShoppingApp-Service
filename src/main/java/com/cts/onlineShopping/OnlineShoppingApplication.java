package com.cts.onlineShopping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;
import com.cts.onlineShopping.repo.ProductRepo;



@SpringBootApplication
@EnableWebMvc
public class OnlineShoppingApplication {

	@Autowired
	AdminRepo adminRepo;
	@Autowired
	CustomerRepo customerRepo;
	@Autowired
	ProductRepo productRepo;

	public static void main(String[] args) {
		System.out.println("Application running from main");
		SpringApplication.run(OnlineShoppingApplication.class, args);
	}
}
