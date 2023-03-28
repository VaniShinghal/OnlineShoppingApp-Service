package com.cts.onlineShopping.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.cts.onlineShopping.config.ProductProducer;
import com.cts.onlineShopping.model.Admin;
import com.cts.onlineShopping.model.Customer;
import com.cts.onlineShopping.model.LoginCredentials;
import com.cts.onlineShopping.model.Product;
import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;
import com.cts.onlineShopping.repo.ProductRepo;
import com.cts.onlineShopping.util.JwtUtil;

@Service
public class OnlineShoppingService {
	
	@Autowired
	AdminRepo adminRepo;
	
	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	ProductProducer producer;
	
	@Autowired
	JwtUtil jwtUtil;
	
	public boolean register(Customer customer) {
		customerRepo.save(customer);
		return true;
	}
	
	public String login(LoginCredentials loginCreds) {
		String response = "Invalid Credentials";
		System.out.println(loginCreds);
		if(loginCreds.isUserAdmin()) {
			Optional<Admin> admin = adminRepo.findById(loginCreds.getLoginId());
			System.out.println(admin);
			if(admin.isPresent()) {
				if(admin.get().getPassword().equals(loginCreds.getPassword())) {
					response = jwtUtil.generateToken(new User(loginCreds.getLoginId(), loginCreds.getPassword(), new ArrayList<>()));
				}else {
					response = "Wrong Password";
				}
			}else {
				response = "User Not Found";
			}
		}else {
			Optional<Customer> customer = customerRepo.findById(loginCreds.getLoginId());
			if(customer.isPresent()) {
				if(customer.get().getPassword().equals(loginCreds.getPassword())) {
					response = jwtUtil.generateToken(new User(loginCreds.getLoginId(), loginCreds.getPassword(), new ArrayList<>()));
				}else {
					response = "Wrong Password";
				}
			}else {
				response = "User Not Found";
			}
		}
		return response;
	}
	
	public String forgotPassword(String custLoginId, String newPassword) {
		
		Optional<Customer> cust = customerRepo.findById(custLoginId); 
		if(cust.isPresent()) {
			Customer customer = cust.get();
			customer.setPassword(newPassword);
			customerRepo.save(customer);
			return "Password Changed";
		}
		
		return "User Not Found";
	}
	
	
	public List<Product> getAllProducts(){
		return productRepo.findAll();
	}
	
	public List<Product> searchProduct(String productName){
		return productRepo.findByNameRegex(".*"+productName+".*");
	}
	
	public String addProduct(Product product, String authToken) {
		if(!isUserAdmin(authToken)) {
			return "Unauthorised Action for user";
		}
		if(product!=null) {
			productRepo.save(product);
			producer.send("product_activities", "Added Product " + product);
			return "Product Added";
		}
		return "Invalid Product";
	}
	
	public String updateProduct(String productName, String status, String authToken) {
		if(!isUserAdmin(authToken)) {
			return "Unauthorised Action for user";
		}
		Optional<Product> prod = productRepo.findById(productName);
		if(prod.isPresent()) {
			Product product = prod.get();
			product.setProductStatus(status);
			productRepo.save(product);
			producer.send("product_activities", "Updated Product " + product);
			return "Product Updated";
		}
		return "Product Not Found";
	}
	
	public boolean deleteProduct(String productName, String authToken) {
		if(!isUserAdmin(authToken)) {
			return false;
		}
		productRepo.deleteById(productName);
		producer.send("product_activities", "Deleted Product " + productName);
		return true;
	}
	
	public String placeOrder(String productName, int noOfOrder) {
		String response = "";
		Optional<Product> product = productRepo.findById(productName);
		if(product.isPresent()) {
			int noOfProduct = product.get().getNoOfProducts();
			Product updatedProduct = product.get();
			if(updatedProduct.getProductStatus().equals("OUT OF STOCK")) {
				return "OUT OF STOCK";
			}
			if(noOfProduct==0) {
				response="OUT OF STOCK";
			}else {
				if(noOfProduct<noOfOrder) {
//					updatedProduct.setNoOfProducts(0);
					response = "OUT OF STOCK";
				}else {
					int remainingProduct = noOfProduct-noOfOrder;
					updatedProduct.setNoOfProducts(remainingProduct);
					if(remainingProduct<=0) {
						updatedProduct.setProductStatus("OUT OF STOCK");
					}
					updatedProduct.setNoOfOrders(updatedProduct.getNoOfOrders()+noOfOrder);
					productRepo.save(updatedProduct);
					response = "Order Placed. Number of orders: " + noOfOrder;
				}
				
			}
			producer.send("product_activities", response + " " + updatedProduct.toString());
		}else {
			response = "Product Not Found";
		}
		
		return response;
	}
	
	public boolean isUserAdmin(String authToken) {
		String username = jwtUtil.extractUsername(authToken);
		if(adminRepo.findById(username).isPresent()) {
			return true;
		}
		return false;
	}
	
}
