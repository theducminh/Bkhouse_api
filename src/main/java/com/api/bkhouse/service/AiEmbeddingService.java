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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiEmbeddingService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Hàm gọi Gemini API để biến Text thành Vector (768 chiều)
     */
    public float[] generateEmbedding(String text) {
        try {
            // 1. Tạo URL hoàn chỉnh kèm API Key
            String fullUrl = apiUrl + apiKey;

            // 2. Tạo Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. Build Body JSON theo đúng chuẩn của Gemini API
            // Format: { "content": { "parts": [{ "text": "..." }] } }
            Map<String, Object> body = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            
            Map<String, String> textPart = new HashMap<>();
            textPart.put("text", text);
            parts.add(textPart);
            
            content.put("parts", parts);
            body.put("content", content);

            // 4. Đóng gói Request và gửi POST
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, request, String.class);

            // 5. Parse kết quả trả về từ dạng String JSON
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode valuesNode = root.path("embedding").path("values");

            // 6. Chuyển mảng JsonNode thành float[]
            if (valuesNode.isArray()) {
                float[] vector = new float[valuesNode.size()];
                for (int i = 0; i < valuesNode.size(); i++) {
                    vector[i] = (float) valuesNode.get(i).asDouble();
                }
                return vector; // Trả về mảng 768 số thực
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi gọi Gemini API: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Nếu lỗi trả về vector mặc định (hoặc em có thể throw Exception)
        return new float[768]; 
    }
}
