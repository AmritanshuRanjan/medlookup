import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const EditMode = () => {
    const [symptoms, setSymptoms] = useState('');
    const [disease, setDisease] = useState('');
    const [medicine, setMedicine] = useState('');
    const [dosage, setDosage] = useState('');
    const navigate = useNavigate();

    const handleSave = async () => {
        const data = {
            symptoms,
            disease,
            medicine,
            dosage,
        };

        const response = await fetch('http://localhost:8080/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            alert('Data saved successfully!');
            navigate('/');
        } else {
            alert('Failed to save data.');
        }
    };

    return (
        <div>
            <h1>Edit Mode</h1>
            <input
                type="text"
                value={symptoms}
                onChange={(e) => setSymptoms(e.target.value)}
                placeholder="Enter Symptoms"
            />
            <input
                type="text"
                value={disease}
                onChange={(e) => setDisease(e.target.value)}
                placeholder="Enter Disease"
            />
            <input
                type="text"
                value={medicine}
                onChange={(e) => setMedicine(e.target.value)}
                placeholder="Enter Medicine"
            />
            <input
                type="text"
                value={dosage}
                onChange={(e) => setDosage(e.target.value)}
                placeholder="Enter Dosage"
            />
            <button onClick={handleSave}>Save</button>
        </div>
    );
};

export default EditMode;
