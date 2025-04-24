import React, { useState } from 'react';
import { addApplication } from '../services/apiService';
import { Form, Button, Container } from 'react-bootstrap';

const JobApplicationForm = () => {
  const [formData, setFormData] = useState({
    companyName: '',
    jobTitle: '',
    applicationDate: '',
    status: '',
    emailContent: '',
    notes: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await addApplication(formData);
      alert('Job application added successfully.');
      setFormData({
        companyName: '',
        jobTitle: '',
        applicationDate: '',
        status: '',
        emailContent: '',
        notes: ''
      });
    } catch (error) {
      console.error('Failed to add application.');
    }
  };

  return (
    <Container className="mt-4">
      <h2>Add Job Application</h2>
      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="formCompany">
          <Form.Label>Company Name</Form.Label>
          <Form.Control
            type="text"
            name="companyName"
            value={formData.companyName}
            onChange={handleChange}
            placeholder="Enter company name"
          />
        </Form.Group>
        <Form.Group controlId="formJobTitle" className="mt-3">
          <Form.Label>Job Title</Form.Label>
          <Form.Control
            type="text"
            name="jobTitle"
            value={formData.jobTitle}
            onChange={handleChange}
            placeholder="Enter job title"
          />
        </Form.Group>
        <Form.Group controlId="formDate" className="mt-3">
          <Form.Label>Application Date</Form.Label>
          <Form.Control
            type="date"
            name="applicationDate"
            value={formData.applicationDate}
            onChange={handleChange}
          />
        </Form.Group>
        <Form.Group controlId="formStatus" className="mt-3">
          <Form.Label>Status</Form.Label>
          <Form.Select name="status" value={formData.status} onChange={handleChange}>
            <option value="">Select Status</option>
            <option value="APPLIED">Applied</option>
            <option value="INTERVIEW_SCHEDULED">Interview Scheduled</option>
            <option value="OFFER">Offer</option>
            <option value="REJECTED">Rejected</option>
          </Form.Select>
        </Form.Group>
        <Form.Group controlId="formEmailContent" className="mt-3">
          <Form.Label>Email Content</Form.Label>
          <Form.Control
            as="textarea"
            name="emailContent"
            rows={3}
            value={formData.emailContent}
            onChange={handleChange}
          />
        </Form.Group>
        <Form.Group controlId="formNotes" className="mt-3">
          <Form.Label>Notes</Form.Label>
          <Form.Control
            as="textarea"
            name="notes"
            rows={3}
            value={formData.notes}
            onChange={handleChange}
          />
        </Form.Group>
        <Button variant="primary" type="submit" className="mt-4">
          Submit
        </Button>
      </Form>
    </Container>
  );
};

export default JobApplicationForm;
