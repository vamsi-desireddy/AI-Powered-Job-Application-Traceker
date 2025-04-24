import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/applications';
const ANALYZE_URL = 'http://localhost:8080/api/analyze-emails';

// 🔹 Get all job applications
export const fetchApplications = async () => {
  try {
    const response = await axios.get(API_BASE_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching applications:', error.response ? error.response.data : error.message);
    throw error;
  }
};

// 🔹 Add a new job application
export const addApplication = async (application) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/add`, application);
    return response.data;
  } catch (error) {
    console.error('Error adding application:', error.response ? error.response.data : error.message);
    throw error;
  }
};

// 🔹 Delete a job application by ID
export const deleteApplication = async (id) => {
  try {
    await axios.delete(`${API_BASE_URL}/delete/${id}`);
  } catch (error) {
    console.error(`Error deleting application with ID ${id}:`, error.response ? error.response.data : error.message);
    throw error;
  }
};

// 🔹 Trigger AI script to analyze unread emails
export const runAIAnalyzer = async () => {
  try {
    const response = await axios.get(ANALYZE_URL);
    return response.data;
  } catch (error) {
    console.error('Error running AI analyzer:', error.response ? error.response.data : error.message);
    throw error;
  }
};

// 🔹 Import job application data from email_applications.json
export const importAIData = async () => {
  try {
    const response = await axios.post(`${API_BASE_URL}/import-ai-data`);
    return response.data;
  } catch (error) {
    console.error('Error importing AI data:', error.response ? error.response.data : error.message);
    throw error;
  }
};
