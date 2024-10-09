import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const SearchPage = () => {
    const [symptom, setSymptom] = useState('');
    const navigate = useNavigate();

    const handleSearch = () => {
        navigate(`/results/${symptom}`);
    };

    return (
        <div>
            <h1>Search for Symptoms</h1>
            <input
                type="text"
                value={symptom}
                onChange={(e) => setSymptom(e.target.value)}
                placeholder="Enter symptom"
            />
            <button onClick={handleSearch}>Search</button>
            <button onClick={() => navigate('/edit')}>Edit Mode</button>
        </div>
    );
};

export default SearchPage;
