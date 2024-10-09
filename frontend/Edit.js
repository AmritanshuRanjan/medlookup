import React, { useState } from 'react';

const EditApp = () => {
  const [disease, setDisease] = useState('');
  const [symptoms, setSymptoms] = useState('');
  const [medicine, setMedicine] = useState('');
  const [dosage, setDosage] = useState('');

  const handleSave = () => {
    // Implement your save logic here, e.g., send data to your backend
    console.log({ disease, symptoms, medicine, dosage });
  };

  return (
    <div>
      <h2>Edit Mode</h2>
      <form onSubmit={(e) => { e.preventDefault(); handleSave(); }}>
        <input
          type="text"
          placeholder="Disease"
          value={disease}
          onChange={(e) => setDisease(e.target.value)}
        />
        <input
          type="text"
          placeholder="Symptoms"
          value={symptoms}
          onChange={(e) => setSymptoms(e.target.value)}
        />
        <input
          type="text"
          placeholder="Medicine"
          value={medicine}
          onChange={(e) => setMedicine(e.target.value)}
        />
        <input
          type="text"
          placeholder="Dosage"
          value={dosage}
          onChange={(e) => setDosage(e.target.value)}
        />
        <button type="submit">Save</button>
      </form>
    </div>
  );
};

export default EditApp;
