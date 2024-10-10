import React, { useState, useEffect } from 'react';
import './EditMode.css'; // Import custom CSS for table styling

const EditMode = () => {
    const [tableData, setTableData] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch('http://localhost:8080/getData');
            if (response.ok) {
                const data = await response.json();
                setTableData(data);
            } else {
                console.error('Failed to load data');
            }
        };

        fetchData();
    }, []);

    const handleInputChange = (index, field, value) => {
        const updatedData = [...tableData];
        updatedData[index][field] = value;
        setTableData(updatedData);
    };

    const addRow = () => {
        setTableData([...tableData, { symptoms: '', disease: '', medicine: '', dosage: '' }]);
    };

    const handleSave = async () => {
        const response = await fetch('http://localhost:8080/saveAll', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(tableData),
        });

        if (response.ok) {
            alert('Data saved successfully!');
        } else {
            alert('Failed to save data.');
        }
    };

    return (
        <div className="edit-mode-page">
            <h1>Edit Mode</h1>
            <table className="editable-table">
                <thead>
                    <tr>
                        <th>Symptoms</th>
                        <th>Disease</th>
                        <th>Medicine</th>
                        <th>Dosage</th>
                        <th>Add/Remove</th>
                    </tr>
                </thead>
                <tbody>
                    {tableData.map((row, index) => (
                        <tr key={index}>
                            <td>
                                <input
                                    type="text"
                                    value={row.symptoms}
                                    onChange={(e) => handleInputChange(index, 'symptoms', e.target.value)}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    value={row.disease}
                                    onChange={(e) => handleInputChange(index, 'disease', e.target.value)}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    value={row.medicine}
                                    onChange={(e) => handleInputChange(index, 'medicine', e.target.value)}
                                />
                            </td>
                            <td>
                                <input
                                    type="text"
                                    value={row.dosage}
                                    onChange={(e) => handleInputChange(index, 'dosage', e.target.value)}
                                />
                            </td>
                            <td>
                                <button onClick={() => setTableData(tableData.filter((_, i) => i !== index))}>-</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <button onClick={addRow} className="add-row-button">+</button>
            <button onClick={handleSave} className="save-button">Save</button>
        </div>
    );
};

export default EditMode;
