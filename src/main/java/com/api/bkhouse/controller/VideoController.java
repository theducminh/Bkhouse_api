/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.bkhouse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.api.bkhouse.payload.dto.VideoDTO;
import com.api.bkhouse.service.VideoService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 *
 * @author dieppv
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/api/v1/videos/add")
    public String addVideo(@RequestParam("title") String title,
            @RequestParam("file") MultipartFile file, Model model) throws IOException {
        String id = videoService.addVideo(title, file);
        return id;
    }

    @GetMapping("/api/no-auth/videos/{id}")
    public String getVideo(@PathVariable String id, Model model) throws Exception {
        VideoDTO video = videoService.getVideo(id);
        model.addAttribute("title", video.getTitle());
        model.addAttribute("url", "/videos/stream/" + id);
        return Base64.getEncoder().encodeToString(video.getStream().readAllBytes());
    }

    @GetMapping("/api/no-auth/videos/stream/{id}")
    public void streamVideo(@PathVariable String id, HttpServletResponse response) throws Exception {
        VideoDTO video = videoService.getVideo(id);
        FileCopyUtils.copy(video.getStream(), response.getOutputStream());
    }
}
