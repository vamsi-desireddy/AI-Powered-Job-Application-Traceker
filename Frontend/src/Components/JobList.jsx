import React, { useEffect, useState } from 'react';
import { fetchApplications, deleteApplication } from '../services/apiService';
import { Table, Container, Button } from 'react-bootstrap';

const JobList = () => {
  const [applications, setApplications] = useState([]);

  useEffect(() => {
    const getApplications = async () => {
      try {
        const data = await fetchApplications();
        setApplications(data);
        console.log("📦 Applications from backend:", data); // optional debug
      } catch (error) {
        console.error('Failed to fetch applications.');
      }
    };

    getApplications();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this application?')) {
      try {
        await deleteApplication(id);
        setApplications(applications.filter((app) => app.id !== id));
      } catch (error) {
        console.error('Failed to delete application.');
      }
    }
  };

  return (
    <Container className="mt-4">
      <h2>Job Applications</h2>
      <Table striped bordered hover responsive>
        <thead>
          <tr>
            <th>Company</th>
            <th>Job Title</th>
            <th>Status</th>
            <th>Application Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {applications.map((app) => (
            <tr key={app.id}>
              <td>{app.company_name || '—'}</td>
              <td>{app.role_applied_for || '—'}</td>
              <td>{app.status || '—'}</td>
              <td>
                {app.date_applied
                  ? new Date(app.date_applied).toLocaleDateString()
                  : '—'}
              </td>
              <td>
                <Button
                  variant="danger"
                  onClick={() => handleDelete(app.id)}
                  size="sm"
                >
                  Delete
                </Button>
              </td>
            </tr>
          ))} 
        </tbody>
      </Table>
    </Container>
  );
};

export default JobList;
