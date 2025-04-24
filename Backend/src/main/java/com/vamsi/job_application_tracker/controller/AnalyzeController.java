package com.vamsi.job_application_tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173"})
public class AnalyzeController {

    @GetMapping("/analyze-emails")
    public ResponseEntity<Map<String, Object>> analyzeEmails() {
        try {
            // Path to your AI script
            ProcessBuilder pb = new ProcessBuilder("python3", "AIanalysis.py");
            pb.directory(new File("/Users/Desireddy/Desktop/AIJobTracker/AI"));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            int appCount = 0;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                if (line.contains("✅ Extracted:")) {
                    appCount++;
                }
            }

            int exitCode = process.waitFor();

            Map<String, Object> response = new HashMap<>();
            response.put("count", appCount);
            response.put("log", output.toString());

            if (exitCode == 0) {
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "AI script failed with non-zero exit code.");
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
