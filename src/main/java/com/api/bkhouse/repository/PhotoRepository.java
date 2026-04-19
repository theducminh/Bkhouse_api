/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.bkhouse.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.api.bkhouse.entity.Photo;

/**
 *
 * @author ducnm
 */
public interface PhotoRepository extends MongoRepository<Photo, ObjectId>{
    

}
