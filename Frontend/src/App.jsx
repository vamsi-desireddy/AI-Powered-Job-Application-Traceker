import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Dashboard from './Components/Dashboard';
import JobList from './Components/JobList';
import JobApplicationForm from './Components/JobApplicationForm';
import CustomNavbar from './Components/Navbar';
import Footer from './Components/Footer';
import 'bootstrap/dist/css/bootstrap.min.css';

const App = () => {
  return (
    <Router>
      <div className="app-container d-flex flex-column min-vh-100">
        <CustomNavbar />
        <main className="flex-grow-1 container mt-4">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/applications" element={<JobList />} />
            <Route path="/add-application" element={<JobApplicationForm />} />
            <Route path="/job-list" element={<JobList />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </Router>
  );
};

export default App;
