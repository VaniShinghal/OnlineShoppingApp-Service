package com.cts.onlineShopping.model;

import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
@Generated
@Getter
@Setter
@Data
@Document(collection = "Product")
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	
	@Id
	private String productName;
	
	@Field
	private String productDescription;
	@Field
	private double price;
	@Field
	private String features;
	@Field
	private String productStatus;
	@Field
	private int noOfOrders;
	@Field
	private int noOfProducts;

}
