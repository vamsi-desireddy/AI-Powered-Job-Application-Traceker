import React, { useEffect, useState } from 'react';
import { fetchApplications } from '../services/apiService';
import axios from 'axios';
import { Container, Alert, Button, Table } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
  const [applications, setApplications] = useState([]);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    loadApplications();
  }, []);

  const loadApplications = async () => {
    try {
      const data = await fetchApplications();
      console.log("🔥 Fetched Applications from backend:", data);  // <-- ADD THIS LINE
      const latest = [...data].sort((a, b) => b.id - a.id).slice(0, 3);
      setApplications(latest);
    } catch (error) {
      console.error("❌ Error fetching applications:", error);
      setErrorMessage('Failed to load job applications.');
    }
  };
  

  const handleGmailSync = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/analyze-emails');
      const count = response.data.count;

      if (count > 0) {
        setSuccessMessage(`✅ Synced ${count} new job application${count > 1 ? 's' : ''} from Gmail.`);
      } else {
        setSuccessMessage('✅ Your applications are up to date. No new job-related emails found.');
      }

      setErrorMessage('');
      loadApplications(); // Refresh dashboard with latest 3

    } catch (error) {
      console.error('AI sync error:', error);
      setErrorMessage('❌ AI sync failed. See console for details.');
      setSuccessMessage('');
    }
  };

  return (
    <Container className="mt-4">
      <h2>📋 Job Application Dashboard</h2>

      {successMessage && (
        <Alert variant="success" className="white-space-pre">
          {successMessage}
        </Alert>
      )}
      {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}

      <Button variant="primary" className="mb-4" onClick={handleGmailSync}>
        📬 Sync From Gmail
      </Button>

      {applications.length === 0 ? (
        <p>No job applications found.</p>
      ) : (
        <>
          <Table striped bordered hover responsive>
            <thead>
              <tr>
              
                <th>Company</th>
                <th>Job Title</th>
                <th>Status</th>
                <th>Application Date</th>
              </tr>
            </thead>
            <tbody>
              {applications.map((app) => (
                <tr key={app.id}>
                <td>{app.company_name || '—'}</td>
                <td>{app.role_applied_for || '—'}</td>
                <td>{app.status || '—'}</td>
                <td>{app.date_applied ? new Date(app.date_applied).toLocaleDateString() : '—'}</td>
              </tr>
              ))}
            </tbody>
          </Table>

          <div className="text-center">
            <Button variant="secondary" onClick={() => navigate('/applications')}>
              🔍 View All Applications
            </Button>
          </div>
        </>
      )}
    </Container>
  );
};

export default Dashboard;
