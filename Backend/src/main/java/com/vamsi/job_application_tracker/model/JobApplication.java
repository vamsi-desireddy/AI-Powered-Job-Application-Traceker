package com.vamsi.job_application_tracker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("company_name")
    @NotBlank(message = "Company name is required")
    private String companyName;

    @JsonProperty("role_applied_for")
    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @JsonProperty("date_applied")
    @NotNull(message = "Application date is required")
    @JsonFormat(pattern = "yyyy-MM-dd") // ✅ parse "2025-04-19" string into Date
    @Temporal(TemporalType.DATE)
    private Date applicationDate;

    @JsonProperty("status")
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Lob
    private String emailContent;

    @Lob
    private String notes;

    @JsonProperty("days_since_update")
    private int daysSinceUpdate;

    public enum Status {
        APPLIED,
        INTERVIEW_SCHEDULED,
        OFFER,
        REJECTED;

        @JsonCreator
        public static Status fromString(String value) {
            return Status.valueOf(value.toUpperCase().replace(" ", "_"));
        }
    }

    public JobApplication() {}

    public JobApplication(String companyName, String jobTitle, Date applicationDate, Status status, String emailContent, String notes) {
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.applicationDate = applicationDate;
        this.status = status;
        this.emailContent = emailContent;
        this.notes = notes;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getDaysSinceUpdate() {
        return daysSinceUpdate;
    }

    public void setDaysSinceUpdate(int daysSinceUpdate) {
        this.daysSinceUpdate = daysSinceUpdate;
    }
}
