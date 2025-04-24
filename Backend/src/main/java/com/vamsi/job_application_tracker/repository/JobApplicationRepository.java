package com.vamsi.job_application_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vamsi.job_application_tracker.model.JobApplication;
import com.vamsi.job_application_tracker.model.JobApplication.Status;

import java.util.Date;
import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // Find applications by status
    List<JobApplication> findByStatus(Status status);

    // Find applications by company name (case-insensitive)
    List<JobApplication> findByCompanyNameIgnoreCase(String companyName);

    // Find applications within a specific date range
    List<JobApplication> findByApplicationDateBetween(Date startDate, Date endDate);

    // Find applications by status and company name
    List<JobApplication> findByStatusAndCompanyNameIgnoreCase(Status status, String companyName);
}
