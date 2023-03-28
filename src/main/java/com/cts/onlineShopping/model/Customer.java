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
@Document(collection = "Customer")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	
	@Id
	private String loginId;
	
	@Field
	private String email;
	@Field
	private String firstName;
	@Field
	private String lastName;
	@Field
	private String password;
	@Field
	private String contactNumber;

}
