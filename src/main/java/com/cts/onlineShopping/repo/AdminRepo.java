package com.cts.onlineShopping.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cts.onlineShopping.model.Admin;

@Repository
public interface AdminRepo extends MongoRepository<Admin, String>{

}
