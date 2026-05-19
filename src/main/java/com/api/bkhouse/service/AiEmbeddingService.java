package com.api.bkhouse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiEmbeddingService {

    // Trỏ thẳng vào model mà luồng Python ETL đang dùng
    @Value("${huggingface.api.url:https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2}")
    private String apiUrl;

    @Value("${huggingface.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Dùng API của Hugging Face để biến Text thành Vector (384 chiều - MiniLM)
     * Đảm bảo đồng bộ không gian vector với database do PySpark đẩy vào.
     */
    public float[] generateEmbedding(String text) {
        try {
            // 1. Tạo Header với Bearer Token của Hugging Face
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 2. Build Body JSON: Hugging Face API nhận format {"inputs": "nội dung text"}
            Map<String, Object> body = new HashMap<>();
            body.put("inputs", text);

            // 3. Đóng gói Request và gửi POST
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            // 4. Parse kết quả trả về
            // Hugging Face feature-extraction trả về mảng trực tiếp: [0.01, -0.02, 0.05, ...]
            JsonNode rootArray = objectMapper.readTree(response.getBody());

            // 5. Chuyển JsonNode thành float[]
            if (rootArray.isArray()) {
                float[] vector = new float[rootArray.size()];
                for (int i = 0; i < rootArray.size(); i++) {
                    vector[i] = (float) rootArray.get(i).asDouble();
                }
                return vector; // Trả về mảng 384 số thực
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gọi Hugging Face API: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Nếu lỗi trả về vector 384 chiều rỗng (để không làm crash hệ thống RAG)
        return new float[384]; 
    }
}