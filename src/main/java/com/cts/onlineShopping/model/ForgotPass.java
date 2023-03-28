package com.cts.onlineShopping.model;

import lombok.Data;
import lombok.Generated;

@Generated
@Data
public class ForgotPass {
	
	private String LoginId;
	public ForgotPass(String loginId, boolean isUserAdmin) {
		super();
		LoginId = loginId;
		this.isUserAdmin = isUserAdmin;
	}
	public String getLoginId() {
		return LoginId;
	}
	public void setLoginId(String loginId) {
		LoginId = loginId;
	}
	public boolean isUserAdmin() {
		return isUserAdmin;
	}
	public void setUserAdmin(boolean isUserAdmin) {
		this.isUserAdmin = isUserAdmin;
	}
	private boolean isUserAdmin;
	
}
