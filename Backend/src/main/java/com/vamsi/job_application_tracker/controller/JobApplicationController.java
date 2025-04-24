package com.vamsi.job_application_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vamsi.job_application_tracker.model.JobApplication;
import com.vamsi.job_application_tracker.repository.JobApplicationRepository;
import com.vamsi.job_application_tracker.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class JobApplicationController {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private EmailService emailService;

    // Add a new job application
    @PostMapping("/add")
    public ResponseEntity<?> addApplication(@Valid @RequestBody JobApplication application, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        application.setApplicationDate(new Date());
        System.out.println("Received Job Application: " + application);
        JobApplication savedApplication = jobApplicationRepository.save(application);
        return ResponseEntity.ok(savedApplication);
    }

    // Get all job applications
    @GetMapping
    public ResponseEntity<List<JobApplication>> getAllApplications() {
        List<JobApplication> applications = jobApplicationRepository.findAll();
        return ResponseEntity.ok(applications);
    }

    // Get a job application by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getApplicationById(@PathVariable Long id) {
        Optional<JobApplication> application = jobApplicationRepository.findById(id);
        return application.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update the status of a job application
    @PutMapping("/update-status/{id}")
    public ResponseEntity<JobApplication> updateStatus(@PathVariable Long id, @RequestBody String status) {
        Optional<JobApplication> application = jobApplicationRepository.findById(id);
        if (application.isPresent()) {
            JobApplication existingApplication = application.get();
            existingApplication.setStatus(JobApplication.Status.valueOf(status.toUpperCase()));
            jobApplicationRepository.save(existingApplication);
            return ResponseEntity.ok(existingApplication);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a job application
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        if (jobApplicationRepository.existsById(id)) {
            jobApplicationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get job applications by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplication>> getApplicationsByStatus(@PathVariable String status) {
        try {
            List<JobApplication> applications = jobApplicationRepository.findByStatus(JobApplication.Status.valueOf(status.toUpperCase()));
            return ResponseEntity.ok(applications);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Fetch and save job applications from emails using live Gmail reader
    @PostMapping("/fetch-emails")
    public ResponseEntity<List<JobApplication>> fetchAndSaveJobApplications() {
        List<JobApplication> jobApplications = emailService.fetchUnreadEmails();
        return ResponseEntity.ok(jobApplications);
    }

    // ✅ NEW: Import AI-parsed applications from email_applications.json
    @PostMapping("/import-ai-data")
    public ResponseEntity<String> importAIExtractedData() {
        try {
            File jsonFile = new File("/Users/Desireddy/Desktop/AIJobTracker/AI/email_applications.json");

            ObjectMapper mapper = new ObjectMapper();
            List<JobApplication> applications = Arrays.asList(mapper.readValue(jsonFile, JobApplication[].class));

            jobApplicationRepository.saveAll(applications);

            return ResponseEntity.ok("✅ Successfully imported " + applications.size() + " applications from AI data.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Failed to import AI data: " + e.getMessage());
        }
    }
}
