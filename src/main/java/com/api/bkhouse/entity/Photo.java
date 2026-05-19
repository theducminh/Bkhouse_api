/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.bkhouse.entity;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id; //


/**
 *
 * @author ducnm
 */
@Document(collection = "photos")
public class Photo {
    @Id
    private ObjectId id;
    
    private String title;
        
    private Binary image;
    public Photo() {
    }

    public Photo(String title) {
        this.title = title;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Binary getImage() {
        return image;
    }

    public void setImage(Binary image) {
        this.image = image;
    }
}
