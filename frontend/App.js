import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import EditApp from './EditApp'; // Assuming your edit component is in EditApp.js

const HomePage = ({ onSearch }) => {
  const [symptom, setSymptom] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch(symptom);
  };

  return (
    <div>
      <h1>MedLookup</h1>
      <form onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Enter symptom..."
          value={symptom}
          onChange={(e) => setSymptom(e.target.value)}
        />
        <button type="submit">Search</button>
      </form>
      <Link to="/edit">Edit Mode</Link>
    </div>
  );
};

const ResultsPage = ({ results }) => (
  <div>
    <h2>Results</h2>
    {results && results.map((result, index) => (
      <div key={index}>
        <p>{result}</p>
      </div>
    ))}
  </div>
);

const App = () => {
  const [results, setResults] = useState([]);

  const handleSearch = async (symptom) => {
    try {
      const response = await fetch(`http://localhost:8080/search/${symptom}`);
      const data = await response.text(); // Adjust based on your response format
      setResults([data]); // Update based on actual data structure
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage onSearch={handleSearch} />} />
        <Route path="/results" element={<ResultsPage results={results} />} />
        <Route path="/edit" element={<EditApp />} />
      </Routes>
    </Router>
  );
};

export default App;
