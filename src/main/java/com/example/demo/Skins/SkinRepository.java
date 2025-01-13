package com.example.demo.Skins;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;


public interface SkinRepository extends MongoRepository<Skin, String> {
    @Query("{'name' : { '$regex' : ?0 , $options: 'i' }}")
    Optional<Skin> findByName(String name);

    List<Skin> findAllByVendible(boolean vendible);
}