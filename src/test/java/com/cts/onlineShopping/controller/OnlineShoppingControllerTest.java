package com.cts.onlineShopping.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

import com.cts.onlineShopping.config.ProductProducer;
import com.cts.onlineShopping.model.Product;
import com.cts.onlineShopping.repo.AdminRepo;
import com.cts.onlineShopping.repo.CustomerRepo;
import com.cts.onlineShopping.service.OnlineShoppingService;
import com.cts.onlineShopping.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = { OnlineShoppingController.class })
@ExtendWith(SpringExtension.class)
class OnlineShoppingControllerTest {
	@MockBean
	private AdminRepo adminRepo;

	@MockBean
	private CustomerRepo customerRepo;

	@MockBean
	private JwtUtil jwtUtil;

	@Autowired
	private OnlineShoppingController onlineShoppingController;

	@MockBean
	private OnlineShoppingService onlineShoppingService;

	@MockBean
	private ProductProducer productProducer;

	/**
	 * Method under test:
	 * {@link OnlineShoppingController#addProduct(Product, String)}
	 */
	@Test
	void testAddProduct() throws Exception {
		MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
				.post("/api/v1.0/shopping/{productname}/add", "", "Uri Variables")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==").contentType(MediaType.APPLICATION_JSON);

		ObjectMapper objectMapper = new ObjectMapper();
		MockHttpServletRequestBuilder requestBuilder = contentTypeResult.content(objectMapper.writeValueAsString(
				new Product("Product Name", "Product Description", 10.0d, "Features", "Product Status", 1, 1)));
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(onlineShoppingController).build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	/**
	 * Method under test: {@link OnlineShoppingController#allProducts(String)}
	 */
	@Test
	void testAllProducts() throws Exception {
		when(onlineShoppingService.getAllProducts()).thenReturn(new ArrayList<>());
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1.0/shopping/all")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.content().string("[]"));
	}

	/**
	 * Method under test: {@link OnlineShoppingController#allProducts(String)}
	 */
	@Test
	void testAllProducts2() throws Exception {
		ArrayList<Product> productList = new ArrayList<>();
		productList.add(new Product("?", "?", 10.0d, "?", "?", 1, 1));
		when(onlineShoppingService.getAllProducts()).thenReturn(productList);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1.0/shopping/all")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.content().string(
						"[{\"productName\":\"?\",\"productDescription\":\"?\",\"price\":10.0,\"features\":\"?\",\"productStatus\":\"?\",\"noOfOrders"
								+ "\":1,\"noOfProducts\":1}]"));
	}

	@Test
	void testForgotPassword() throws Exception {
		when(onlineShoppingService.forgotPassword((String) any(), (String) any())).thenReturn("iloveyou");
		MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
				.get("/api/v1.0/shopping/{customername}/forgot", "Customername")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletRequestBuilder requestBuilder = contentTypeResult
				.content((new ObjectMapper()).writeValueAsString("foo"));
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("iloveyou"));
	}

	@Test
	void testForgotPassword2() throws Exception {
		when(onlineShoppingService.forgotPassword((String) any(), (String) any())).thenReturn("User Not Found");
		MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
				.get("/api/v1.0/shopping/{customername}/forgot", "Customername")
				.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletRequestBuilder requestBuilder = contentTypeResult
				.content((new ObjectMapper()).writeValueAsString("foo"));
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(onlineShoppingController).build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("User Not Found"));
	}

	@Test
	void testSearchProduct() throws Exception {
		when(onlineShoppingService.searchProduct((String) any())).thenReturn(new ArrayList<>());
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/api/v1.0/shopping/products/search/{productname}", "Productname");
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.content().string("[]"));
	}

	@Test
	void testSearchProduct2() throws Exception {
		ArrayList<Product> productList = new ArrayList<>();
		productList.add(new Product("?", "?", 10.0d, "?", "?", 1, 1));
		when(onlineShoppingService.searchProduct((String) any())).thenReturn(productList);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/api/v1.0/shopping/products/search/{productname}", "Productname");
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.content().string(
						"[{\"productName\":\"?\",\"productDescription\":\"?\",\"price\":10.0,\"features\":\"?\",\"productStatus\":\"?\",\"noOfOrders"
								+ "\":1,\"noOfProducts\":1}]"));
	}

	@Test
	void testUpdateProduct() throws Exception {
		when(onlineShoppingService.updateProduct((String) any(), (String) any(), (String) any()))
				.thenReturn("2020-03-01");
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/v1.0/shopping/{productname}/update/{status}", "Productname", "Status")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(onlineShoppingController).build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("2020-03-01"));
	}

	@Test
	void testUpdateProduct2() throws Exception {
		when(onlineShoppingService.updateProduct((String) any(), (String) any(), (String) any()))
				.thenReturn("Product Updated");
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/v1.0/shopping/{productname}/update/{status}", "Productname", "Status")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(onlineShoppingController).build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("Product Updated"));
	}

	@Test
	void testUpdateProduct3() throws Exception {
		when(onlineShoppingService.updateProduct((String) any(), (String) any(), (String) any()))
				.thenReturn("Product Not Found");
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/v1.0/shopping/{productname}/update/{status}", "Productname", "Status")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(onlineShoppingController).build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("Product Not Found"));
	}

	@Test
	void testDeleteProduct() throws Exception {
		when(onlineShoppingService.deleteProduct((String) any(), (String) any())).thenReturn(true);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete("/api/v1.0/shopping/{productname}/delete", "Productname")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("Product Deleted"));
	}

	@Test
	void testDeleteProduct2() throws Exception {
		when(onlineShoppingService.deleteProduct((String) any(), (String) any())).thenReturn(false);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.delete("/api/v1.0/shopping/{productname}/delete", "Productname")
				.header("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
		ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(onlineShoppingController).build()
				.perform(requestBuilder);
		actualPerformResult.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("Unauthorized Action for User"));
	}

	@Test
	void testPlaceOrder() throws Exception {
		when(onlineShoppingService.placeOrder((String) any(), anyInt())).thenReturn("Place Order");
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/v1.0/shopping/{productname}/{noOfOrder}", "Productname", 1);
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("Place Order"));
	}

	@Test
	void testPlaceOrder2() throws Exception {
		when(onlineShoppingService.placeOrder((String) any(), anyInt())).thenReturn("Product not found");
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/api/v1.0/shopping/{productname}/{noOfOrder}", "Productname", 1);
		MockMvcBuilders.standaloneSetup(onlineShoppingController).build().perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
				.andExpect(MockMvcResultMatchers.content().string("Product not found"));
	}
}
