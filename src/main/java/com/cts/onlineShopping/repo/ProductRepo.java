package com.cts.onlineShopping.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.onlineShopping.model.Product;

@Repository
public interface ProductRepo extends MongoRepository<Product, String>{

	@Query("{'productName':{$regex: ?0}}")
	public List<Product> findByNameRegex(String searchTerm);
	
}
