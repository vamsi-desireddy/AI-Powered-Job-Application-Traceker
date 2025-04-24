package com.vamsi.job_application_tracker.service;

import org.springframework.stereotype.Service;
import okhttp3.*;
import java.io.IOException;
import java.util.logging.Logger;
import org.json.JSONObject;

@Service
public class NLPService {
    private static final String HUGGING_FACE_API_URL = "https://api-inference.huggingface.co/models/LlamaFactoryAI/cv-job-description-matching";
    private static final Logger logger = Logger.getLogger(NLPService.class.getName());

    // API Token retrieved from environment variables
    private final String apiToken = System.getenv("HUGGING_FACE_API_TOKEN");

    public String analyzeJobDescription(String jobDescription) {
        if (apiToken == null || apiToken.isEmpty()) {
            logger.severe("Hugging Face API token is not set.");
            return "Error: API token is missing.";
        }

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("inputs", jobDescription);

        Request request = new Request.Builder()
                .url(HUGGING_FACE_API_URL)
                .addHeader("Authorization", "Bearer " + apiToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonPayload.toString(), MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.severe("Failed to get a successful response from Hugging Face API. Status: " + response.code());
                return "Error: Failed to analyze job description.";
            }
            logger.info("Job description analyzed successfully.");
            return response.body().string();
        } catch (IOException e) {
            logger.severe("IOException occurred while calling Hugging Face API: " + e.getMessage());
            return "Error: Unable to process the job description.";
        }
    }
}
