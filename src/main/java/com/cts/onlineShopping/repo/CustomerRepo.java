package com.cts.onlineShopping.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cts.onlineShopping.model.Customer;

@Repository
public interface CustomerRepo extends MongoRepository<Customer, String>{

}
