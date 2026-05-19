/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.bkhouse.service;

import com.api.bkhouse.payload.dto.VideoDTO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 * @author ducnm
 */
@Service
public class VideoService {
    private final GridFsTemplate gridFsTemplate;

    private final GridFsOperations operations;

    public VideoService(GridFsTemplate gridFsTemplate, GridFsOperations operations) {
        this.gridFsTemplate = gridFsTemplate;
        this.operations = operations;
    }

    public String addVideo(String title, MultipartFile file) throws IOException { 
        DBObject metaData = new BasicDBObject(); 
        metaData.put("type", "video"); 
        metaData.put("title", title); 
        ObjectId id = gridFsTemplate.store(
          file.getInputStream(), file.getName(), file.getContentType(), metaData); 
        return id.toString(); 
    }

    public VideoDTO getVideo(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id))); 
        
        //  CHỐNG SẬP SERVER: Check null nếu video bị xóa hoặc sai ID
        if (file == null) {
            return null; 
        }
        
        VideoDTO video = new VideoDTO();
        if (file.getMetadata() != null && file.getMetadata().containsKey("title")) {
            video.setTitle(file.getMetadata().get("title").toString()); 
        } else {
            video.setTitle("Unknown Title");
        }
        
        video.setStream(operations.getResource(file).getInputStream());
        return video; 
    }

    public void deleteVideo(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }
}