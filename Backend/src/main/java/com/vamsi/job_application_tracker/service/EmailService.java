package com.vamsi.job_application_tracker.service;

import com.vamsi.job_application_tracker.model.JobApplication;
import com.vamsi.job_application_tracker.model.JobApplication.Status;
import com.vamsi.job_application_tracker.repository.JobApplicationRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.Folder;
import jakarta.mail.Multipart;
import jakarta.mail.BodyPart;




@Service
public class EmailService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    private static final String EMAIL = System.getenv("EMAIL_USERNAME");
    private static final String PASSWORD = System.getenv("EMAIL_PASSWORD");
    private static final String HOST = "imap.gmail.com";

    // Fetch and process job-related emails
    public List<JobApplication> fetchUnreadEmails() {
        List<JobApplication> updatedApplications = new ArrayList<>();
    
        try {
            // Setup properties for IMAP connection
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.host", HOST);
            properties.put("mail.imaps.port", "993");
    
            // Create the session and connect to the mail store
            Session session = Session.getInstance(properties);
            Store store = session.getStore();
            store.connect(HOST, EMAIL, PASSWORD);
    
            // Open the inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
    
            // Fetch unread messages (only UNSEEN messages)
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            System.out.println("Number of Unread Emails Fetched: " + messages.length);
    
            for (Message message : messages) {
                String subject = message.getSubject();
                String content = extractEmailBody(message);
    
                System.out.println("Email Subject: " + subject);
                System.out.println("Email Content: " + content);
    
                // For now, we're just printing and ignoring classification
                JobApplication jobApp = new JobApplication();
                jobApp.setCompanyName(subject);  // For testing, use the subject as the company name
                jobApp.setJobTitle(subject);  // Use the subject for the job title as well (for testing)
                jobApp.setStatus(Status.APPLIED);  // Default status for testing
                updatedApplications.add(jobApp);
    
                message.setFlag(Flags.Flag.SEEN, true);  // Mark the email as read (seen)
            }
    
            // Close inbox and store
            inbox.close(false);
            store.close();
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        System.out.println("Updated Applications: " + updatedApplications);
        return updatedApplications;
    }
    
    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findAll();
    }

    private String extractEmailBody(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            Document doc = Jsoup.parse(message.getContent().toString());
            return doc.text();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return (String) bodyPart.getContent();
                } else if (bodyPart.isMimeType("text/html")) {
                    Document doc = Jsoup.parse(bodyPart.getContent().toString());
                    return doc.text();
                }
            }
        }
        return "No content extracted";
    }

    public List<JobApplication> getApplicationsByStatus(Status status) {
        return jobApplicationRepository.findByStatus(status);
    }

    public List<JobApplication> getPaginatedApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobApplicationRepository.findAll(pageable).getContent();
    }

    private List<JobApplication> updateApplicationStatusFromEmail(String emailContent, Status newStatus) {
        List<JobApplication> updatedApplications = new ArrayList<>();
        String companyName = extractCompanyName(emailContent);
        System.out.println("Extracted Company: " + companyName);

        List<JobApplication> applications = jobApplicationRepository.findByCompanyNameIgnoreCase(companyName);
        if (applications.isEmpty()) {
            System.out.println("No applications found for company: " + companyName);
        }

        for (JobApplication application : applications) {
            application.setStatus(newStatus);
            jobApplicationRepository.save(application);
            updatedApplications.add(application);
        }

        return updatedApplications;
    }

    private String extractCompanyName(String emailContent) {
        String regex = "(?i)(?:Company:\\s*([A-Za-z0-9\\s]+))|(?:Job offer from\\s+([A-Za-z0-9\\s]+))|(?:Join us at\\s+([A-Za-z0-9\\s]+))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailContent);

        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    return matcher.group(i).trim();
                }
            }
        }
        return "Unknown Company";
    }
}
