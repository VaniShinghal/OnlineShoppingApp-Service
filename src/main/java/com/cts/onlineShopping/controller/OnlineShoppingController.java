package com.cts.onlineShopping.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.onlineShopping.config.ProductProducer;
import com.cts.onlineShopping.model.Customer;
import com.cts.onlineShopping.model.LoginCredentials;
import com.cts.onlineShopping.model.Product;
import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;
import com.cts.onlineShopping.service.OnlineShoppingService;
import com.cts.onlineShopping.util.JwtUtil;


@RestController
@RequestMapping("/api/v1.0/shopping")
@CrossOrigin(origins = "*")
public class OnlineShoppingController {
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	AdminRepo adminRepo;
	
	@Autowired
	OnlineShoppingService service;
	
	@Autowired
	ProductProducer producer;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody Customer customer){
		if(service.register(customer)) {
			return new ResponseEntity<String>("Registration Successful",HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("Registration Failed", HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginCredentials loginCreds){
		producer.send("log", "new Login: " + loginCreds.toString());
		System.out.println("Login Started");
		String token = service.login(loginCreds);
		if(token.equals("Invalid Credentials") || token.equals("Wrong Password") || token.equals("User Not Found")) {
			return new ResponseEntity<String>(token, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<String>(token, HttpStatus.OK);
	}
	
	@PostMapping("/{customername}/forgot")
	public ResponseEntity<String> forgotPassword(@RequestBody String newPassword, @PathVariable(value = "customername") String loginId  ){
		String response = service.forgotPassword(loginId, newPassword);
		if(response.equals("User Not Found")) {
			return new ResponseEntity<String>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<String>(response,HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<Product>> allProducts(@RequestHeader("Authorization") String token){
		System.out.println("in controller token: " + token);
		List<Product> products = service.getAllProducts();
		return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
	}
	
	/*
	 * @GetMapping("/products/search/{productname}") public
	 * ResponseEntity<List<Product>> searchProduct(@PathVariable(value =
	 * "productname") String productName){ List<Product> products =
	 * service.searchProduct(productName); return new
	 * ResponseEntity<List<Product>>(products, HttpStatus.OK); }
	 */
	
	@PostMapping("/{productname}/add")
	public ResponseEntity<String> addProduct(@RequestBody Product product, @RequestHeader("Authorization") String authToken){
//		String username = jwtUtil.extractUsername(authToken.substring(7));
//		if(adminRepo.findById(username).isEmpty()) {
//			return new ResponseEntity<String>("Permission Denied", HttpStatus.FORBIDDEN);
//		}
		String response = service.addProduct(product,authToken.substring(7));
		if(response.equals("Product Added")) {
			return new ResponseEntity<String>(response, HttpStatus.CREATED);
		}else if(response.equals("Product Invalid")) {
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		}
			return new ResponseEntity<String>(response, HttpStatus.FORBIDDEN);
	}
	
	@PutMapping("/{productname}/update/{status}")
	public ResponseEntity<String> updateProduct(@PathVariable(value="productname") String productName, @PathVariable("status") String productStatus, @RequestHeader("Authorization") String authToken){
		System.out.println("From update Product Controller "+productName+" "+productStatus+" "+authToken);
		String status = service.updateProduct(productName, productStatus, authToken.substring(7));
		ResponseEntity<String> response = null;
		if(status.equals("Product Updated")) {
			response = new ResponseEntity<String>("Product Updated", HttpStatus.OK);
		}else if(status.equals("Product Not Found")) {
			response = new ResponseEntity<String>("Product Not Found", HttpStatus.NOT_MODIFIED);
		}
		System.out.println(response);
		return response;
	}
	
	@DeleteMapping("/{productname}/delete")
	public ResponseEntity<String> deleteProduct(@PathVariable(value = "productname") String productName, @RequestHeader("Authorization") String authToken){
		boolean status = service.deleteProduct(productName, authToken.substring(7));
		if(status) {
			return new ResponseEntity<String>("Product Deleted", HttpStatus.OK);
		}
		return new ResponseEntity<String>("Unauthorized Action for User", HttpStatus.FORBIDDEN);
	}
	
	@PutMapping("/{productname}/{noOfOrder}")
	public ResponseEntity<String> placeOrder(@PathVariable(value="productname") String productName, @PathVariable("noOfOrder") int noOfOrder ){
		System.out.println(productName);
		String response = service.placeOrder(productName, noOfOrder);
		ResponseEntity<String> res = null;
		if(response.equals("Product not found")) {
			res = new ResponseEntity<String>(response, HttpStatus.NOT_FOUND);
		}
		res = new ResponseEntity<String>(response, HttpStatus.OK);
		return res;
	}
	
}
