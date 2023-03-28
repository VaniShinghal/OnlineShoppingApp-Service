package com.cts.onlineShopping.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cts.onlineShopping.model.Admin;
import com.cts.onlineShopping.model.Customer;
import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = { UserDetailsService.class })
@ExtendWith(SpringExtension.class)
class UserDetailsServiceTest {
	@MockBean
	private AdminRepo adminRepo;

	@MockBean
	private CustomerRepo customerRepo;

	@Autowired
	private UserDetailsService userDetailsService;

	@Test
	void testLoadUserByUsername() throws UsernameNotFoundException {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "iloveyou")));
		when(customerRepo.findById((String) any()))
				.thenReturn(Optional.of(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42")));
		UserDetails actualLoadUserByUsernameResult = userDetailsService.loadUserByUsername("janedoe");
		assertTrue(actualLoadUserByUsernameResult.getAuthorities().isEmpty());
		assertTrue(actualLoadUserByUsernameResult.isEnabled());
		assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
		assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
		assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
		assertEquals("42", actualLoadUserByUsernameResult.getUsername());
		assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
		verify(adminRepo).findById((String) any());
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testLoadUserByUsername2() throws UsernameNotFoundException {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "iloveyou")));
		when(customerRepo.findById((String) any())).thenThrow(new UsernameNotFoundException("Msg"));
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("janedoe"));
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testLoadUserByUsername4() throws UsernameNotFoundException {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "iloveyou")));
		Customer customer = mock(Customer.class);
		when(customer.getLoginId()).thenThrow(new UsernameNotFoundException("Msg"));
		when(customer.getPassword()).thenThrow(new UsernameNotFoundException("Msg"));
		Optional<Customer> ofResult = Optional.of(customer);
		when(customerRepo.findById((String) any())).thenReturn(ofResult);
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("janedoe"));
		verify(adminRepo).findById((String) any());
		verify(customerRepo).findById((String) any());
		verify(customer).getLoginId();
	}

	@Test
	void testLoadUserByUsername5() throws UsernameNotFoundException {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "iloveyou")));
		when(customerRepo.findById((String) any())).thenReturn(Optional.empty());
		new UsernameNotFoundException("Msg");
		new UsernameNotFoundException("Msg");
		UserDetails actualLoadUserByUsernameResult = userDetailsService.loadUserByUsername("janedoe");
		assertTrue(actualLoadUserByUsernameResult.getAuthorities().isEmpty());
		assertTrue(actualLoadUserByUsernameResult.isEnabled());
		assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
		assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
		assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
		assertEquals("jane.doe@example.org", actualLoadUserByUsernameResult.getUsername());
		assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
		verify(adminRepo).findById((String) any());
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testLoadUserByUsername7() throws UsernameNotFoundException {
		Admin admin = mock(Admin.class);
		when(admin.getEmail()).thenThrow(new UsernameNotFoundException("Msg"));
		when(admin.getPassword()).thenThrow(new UsernameNotFoundException("Msg"));
		Optional<Admin> ofResult = Optional.of(admin);
		when(adminRepo.findById((String) any())).thenReturn(ofResult);
		when(customerRepo.findById((String) any())).thenReturn(Optional.empty());
		new UsernameNotFoundException("Msg");
		new UsernameNotFoundException("Msg");
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("janedoe"));
		verify(adminRepo).findById((String) any());
		verify(admin).getEmail();
		verify(customerRepo).findById((String) any());
	}
}
