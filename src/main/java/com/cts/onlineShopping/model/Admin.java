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
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Admin")
public class Admin {
	
	@Id
	private String email;
	@Field
	private String fullName;
	@Field
	private String password;
	
	
}
