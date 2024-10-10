import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './SearchPage.css'; // Import custom CSS for styling

const SearchPage = () => {
    const [symptom, setSymptom] = useState('');
    const navigate = useNavigate();

    const handleSearch = () => {
        navigate(`/results/${symptom}`);
    };

    return (
        <div className="search-page">
            <div className="edit-link" onClick={() => navigate('/edit')}>
                Edit Mode
            </div>
            <h1 className="medlookup-title">
                <span style={{ color: '#4285F4' }}>M</span>
                <span style={{ color: '#EA4335' }}>e</span>
                <span style={{ color: '#FBBC05' }}>d</span>
                <span style={{ color: '#34A853' }}>L</span>
                <span style={{ color: '#4285F4' }}>o</span>
                <span style={{ color: '#EA4335' }}>o</span>
                <span style={{ color: '#FBBC05' }}>k</span>
                <span style={{ color: '#34A853' }}>u</span>
                <span style={{ color: '#EA4335' }}>p</span>
            </h1>
            <div className="search-container">
                <input
                    className="search-input"
                    type="text"
                    value={symptom}
                    onChange={(e) => setSymptom(e.target.value)}
                    placeholder="Enter symptom"
                />
                <button className="search-button" onClick={handleSearch}>Search</button>
            </div>
        </div>
    );
};

export default SearchPage;
