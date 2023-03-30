package com.cts.onlineShopping.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//import com.cts.onlineShopping.config.ProductProducer;
import com.cts.onlineShopping.model.Admin;
import com.cts.onlineShopping.model.Customer;
import com.cts.onlineShopping.model.LoginCredentials;
import com.cts.onlineShopping.model.Product;
import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;
import com.cts.onlineShopping.repo.ProductRepo;
import com.cts.onlineShopping.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = { OnlineShoppingService.class })
@ExtendWith(SpringExtension.class)
class OnlineShoppingServiceTest {
	@MockBean
	private AdminRepo adminRepo;

	@MockBean
	private CustomerRepo customerRepo;

	@MockBean
	private JwtUtil jwtUtil;

	@Autowired
	private OnlineShoppingService onlineShoppingService;

	/*
	 * @MockBean private ProductProducer productProducer;
	 */

	@MockBean
	private ProductRepo productRepo;

	@Test
	void testRegister() {
		when(customerRepo.save((Customer) any()))
				.thenReturn(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42"));
		assertTrue(onlineShoppingService
				.register(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42")));
		verify(customerRepo).save((Customer) any());
	}

	@Test
	void testLogin() {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "iloveyou")));
		when(jwtUtil.generateToken((UserDetails) any())).thenReturn("ABC123");
		assertEquals("ABC123", onlineShoppingService.login(new LoginCredentials("42", "iloveyou", true)));
		verify(adminRepo).findById((String) any());
		verify(jwtUtil).generateToken((UserDetails) any());
	}

	@Test
	void testLogin2() {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "Invalid Credentials")));
		when(jwtUtil.generateToken((UserDetails) any())).thenReturn("ABC123");
		assertEquals("Wrong Password", onlineShoppingService.login(new LoginCredentials("42", "iloveyou", true)));
		verify(adminRepo).findById((String) any());
	}

	@Test
	void testLogin3() {
		when(adminRepo.findById((String) any())).thenReturn(Optional.empty());
		when(jwtUtil.generateToken((UserDetails) any())).thenReturn("ABC123");
		assertEquals("User Not Found", onlineShoppingService.login(new LoginCredentials("42", "iloveyou", true)));
		verify(adminRepo).findById((String) any());
	}

	@Test
	void testLogin5() {
		Admin admin = mock(Admin.class);
		when(admin.getPassword()).thenReturn("iloveyou");
		Optional<Admin> ofResult = Optional.of(admin);
		when(adminRepo.findById((String) any())).thenReturn(ofResult);
		when(customerRepo.findById((String) any()))
				.thenReturn(Optional.of(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42")));
		when(jwtUtil.generateToken((UserDetails) any())).thenReturn("ABC123");
		assertEquals("ABC123", onlineShoppingService.login(new LoginCredentials("42", "iloveyou", false)));
		verify(customerRepo).findById((String) any());
		verify(jwtUtil).generateToken((UserDetails) any());
	}

	@Test
	void testLogin6() {
		Admin admin = mock(Admin.class);
		when(admin.getPassword()).thenReturn("iloveyou");
		Optional<Admin> ofResult = Optional.of(admin);
		when(adminRepo.findById((String) any())).thenReturn(ofResult);
		when(customerRepo.findById((String) any())).thenReturn(
				Optional.of(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "Invalid Credentials", "42")));
		when(jwtUtil.generateToken((UserDetails) any())).thenReturn("ABC123");
		assertEquals("Wrong Password", onlineShoppingService.login(new LoginCredentials("42", "iloveyou", false)));
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testLogin7() {
		Admin admin = mock(Admin.class);
		when(admin.getPassword()).thenReturn("iloveyou");
		Optional<Admin> ofResult = Optional.of(admin);
		when(adminRepo.findById((String) any())).thenReturn(ofResult);
		when(customerRepo.findById((String) any())).thenReturn(Optional.empty());
		when(jwtUtil.generateToken((UserDetails) any())).thenReturn("ABC123");
		assertEquals("User Not Found", onlineShoppingService.login(new LoginCredentials("42", "iloveyou", false)));
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testForgotPassword() {
		when(customerRepo.save((Customer) any()))
				.thenReturn(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42"));
		when(customerRepo.findById((String) any()))
				.thenReturn(Optional.of(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42")));
		assertEquals("Password Changed", onlineShoppingService.forgotPassword("42", "iloveyou"));
		verify(customerRepo).save((Customer) any());
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testForgotPassword2() {
		when(customerRepo.save((Customer) any()))
				.thenReturn(new Customer("42", "jane.doe@example.org", "Jane", "Doe", "iloveyou", "42"));
		when(customerRepo.findById((String) any())).thenReturn(Optional.empty());
		assertEquals("User Not Found", onlineShoppingService.forgotPassword("42", "iloveyou"));
		verify(customerRepo).findById((String) any());
	}

	@Test
	void testGetAllProducts() {
		ArrayList<Product> productList = new ArrayList<>();
		when(productRepo.findAll()).thenReturn(productList);
		List<Product> actualAllProducts = onlineShoppingService.getAllProducts();
		assertSame(productList, actualAllProducts);
		assertTrue(actualAllProducts.isEmpty());
		verify(productRepo).findAll();
	}

	@Test
	void testSearchProduct() {
		ArrayList<Product> productList = new ArrayList<>();
		when(productRepo.findByNameRegex((String) any())).thenReturn(productList);
		List<Product> actualSearchProductResult = onlineShoppingService.searchProduct("Product Name");
		assertSame(productList, actualSearchProductResult);
		assertTrue(actualSearchProductResult.isEmpty());
		verify(productRepo).findByNameRegex((String) any());
	}

	/*
	 * @Test void testAddProduct() { when(adminRepo.findById((String) any()))
	 * .thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe",
	 * "iloveyou"))); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); assertEquals("Product Added",
	 * onlineShoppingService.addProduct( new Product("Product Name",
	 * "Product Description", 10.0d, "Features", "Product Status", 1, 1),
	 * "ABC123")); verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any());
	 * verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).save((Product) any()); }
	 * 
	 * @Test void testAddProduct3() { when(adminRepo.findById((String)
	 * any())).thenReturn(Optional.empty()); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); assertEquals("Unauthorised Action for user",
	 * onlineShoppingService.addProduct( new Product("Product Name",
	 * "Product Description", 10.0d, "Features", "Product Status", 1, 1),
	 * "ABC123")); verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any()); }
	 * 
	 * @Test void testAddProduct4() { when(adminRepo.findById((String) any()))
	 * .thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe",
	 * "iloveyou"))); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); assertEquals("Invalid Product",
	 * onlineShoppingService.addProduct(null, "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any()); }
	 * 
	 * @Test void testAddProduct5() { when(adminRepo.findById((String) any()))
	 * .thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe",
	 * "iloveyou"))); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); assertEquals("Product Added",
	 * onlineShoppingService.addProduct(mock(Product.class), "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any());
	 * verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).save((Product) any()); }
	 * 
	 * @Test void testUpdateProduct() { when(adminRepo.findById((String) any()))
	 * .thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe",
	 * "iloveyou"))); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); when(productRepo.findById((String)
	 * any())).thenReturn(Optional .of(new Product("Product Name",
	 * "Product Description", 10.0d, "Features", "Product Status", 1, 1)));
	 * assertEquals("Product Updated",
	 * onlineShoppingService.updateProduct("Product Name", "Status", "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any());
	 * verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).save((Product) any());
	 * verify(productRepo).findById((String) any()); }
	 * 
	 * @Test void testUpdateProduct2() { when(adminRepo.findById((String)
	 * any())).thenReturn(Optional.empty()); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); when(productRepo.findById((String)
	 * any())).thenReturn(Optional .of(new Product("Product Name",
	 * "Product Description", 10.0d, "Features", "Product Status", 1, 1)));
	 * assertEquals("Unauthorised Action for user",
	 * onlineShoppingService.updateProduct("Product Name", "Status", "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any()); }
	 * 
	 * @Test void testUpdateProduct3() { when(adminRepo.findById((String) any()))
	 * .thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe",
	 * "iloveyou"))); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3)))); Product product =
	 * mock(Product.class); doNothing().when(product).setProductStatus((String)
	 * any()); Optional<Product> ofResult = Optional.of(product);
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); when(productRepo.findById((String)
	 * any())).thenReturn(ofResult); assertEquals("Product Updated",
	 * onlineShoppingService.updateProduct("Product Name", "Status", "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any());
	 * verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).save((Product) any());
	 * verify(productRepo).findById((String) any());
	 * verify(product).setProductStatus((String) any()); }
	 */

	/*
	 * @Test void testDeleteProduct() { when(adminRepo.findById((String) any()))
	 * .thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe",
	 * "iloveyou"))); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * doNothing().when(productRepo).deleteById((String) any());
	 * assertTrue(onlineShoppingService.deleteProduct("Product Name", "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any());
	 * verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).deleteById((String) any()); }
	 * 
	 * @Test void testDeleteProduct2() { when(adminRepo.findById((String)
	 * any())).thenReturn(Optional.empty()); when(jwtUtil.extractUsername((String)
	 * any())).thenReturn("janedoe"); ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * doNothing().when(productRepo).deleteById((String) any());
	 * assertFalse(onlineShoppingService.deleteProduct("Product Name", "ABC123"));
	 * verify(adminRepo).findById((String) any());
	 * verify(jwtUtil).extractUsername((String) any()); }
	 * 
	 * @Test void testPlaceOrder() { ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3))));
	 * when(productRepo.save((Product) any())).thenReturn( new
	 * Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); when(productRepo.findById((String)
	 * any())).thenReturn(Optional .of(new Product("Product Name",
	 * "Product Description", 10.0d, "Features", "Product Status", 1, 1)));
	 * assertEquals("OUT OF STOCK", onlineShoppingService.placeOrder("Product Name",
	 * 1)); verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).findById((String) any()); }
	 * 
	 * @Test void testPlaceOrder2() { ProducerRecord<Integer, String> producerRecord
	 * = new ProducerRecord<>("Topic", "42");
	 * 
	 * when(productProducer.send((String) any(), (String) any())) .thenReturn(new
	 * AsyncResult<>(new SendResult<>(producerRecord, new RecordMetadata(new
	 * TopicPartition("Topic", 1), 1L, 1, 10L, 3, 3)))); Product product =
	 * mock(Product.class); when(product.getNoOfOrders()).thenReturn(1);
	 * doNothing().when(product).setNoOfOrders(anyInt()); Optional<Product> ofResult
	 * = Optional.of(product); when(productRepo.save((Product) any())).thenReturn(
	 * new Product("Product Name", "Product Description", 10.0d, "Features",
	 * "Product Status", 1, 1)); when(productRepo.findById((String)
	 * any())).thenReturn(ofResult); assertEquals("OUT OF STOCK",
	 * onlineShoppingService.placeOrder("Product Name", 1));
	 * verify(productProducer).send((String) any(), (String) any());
	 * verify(productRepo).findById((String) any()); }
	 */
	@Test
	void testIsUserAdmin() {
		when(adminRepo.findById((String) any()))
				.thenReturn(Optional.of(new Admin("jane.doe@example.org", "Dr Jane Doe", "iloveyou")));
		when(jwtUtil.extractUsername((String) any())).thenReturn("janedoe");
		assertTrue(onlineShoppingService.isUserAdmin("ABC123"));
		verify(adminRepo).findById((String) any());
		verify(jwtUtil).extractUsername((String) any());
	}

	@Test
	void testIsUserAdmin2() {
		when(adminRepo.findById((String) any())).thenReturn(Optional.empty());
		when(jwtUtil.extractUsername((String) any())).thenReturn("janedoe");
		assertFalse(onlineShoppingService.isUserAdmin("ABC123"));
		verify(adminRepo).findById((String) any());
		verify(jwtUtil).extractUsername((String) any());
	}
}
