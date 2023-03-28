package com.cts.onlineShopping.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Generated
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCredentials {
	
	private String loginId;
	
	
	private String password;
	
	@JsonProperty("isUserAdmin")
	private boolean isUserAdmin;

}
