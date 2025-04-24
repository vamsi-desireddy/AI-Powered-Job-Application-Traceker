import React from 'react';
import { Card, Button } from 'react-bootstrap';

const JobCard = ({ app }) => {
  return (
    <Card className="mb-3 shadow-sm">
      <Card.Body>
        <Card.Title>@ {app.company_name || '—'}</Card.Title>
        <Card.Subtitle className="mb-2 text-muted">
          Status: {app.status || '—'} | Applied on:{' '}
          {app.application_date
            ? new Date(app.application_date).toLocaleDateString()
            : '—'}
        </Card.Subtitle>
        <Card.Text>Role: {app.job_title || '—'}</Card.Text>
        <Button variant="primary" size="sm">View All</Button>
      </Card.Body>
    </Card>
  );
};

export default JobCard;
