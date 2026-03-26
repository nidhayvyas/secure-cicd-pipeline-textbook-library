package com.se441.textbooklibrary.repository;

import com.se441.textbooklibrary.model.Textbook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextbookRepository extends MongoRepository<Textbook, String> {
    
    Optional<Textbook> findById(Integer id);
}
