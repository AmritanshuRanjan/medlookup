import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './ResultsPage.css'; // Import custom CSS for styling

const ResultsPage = () => {
    const { symptom } = useParams();
    const [results, setResults] = useState([]);

    useEffect(() => {
        const fetchResults = async () => {
            const response = await fetch(`http://localhost:8080/search/${symptom}`);
            if (response.ok) {
                const data = await response.json();
                setResults(data);
            } else {
                console.error('Failed to fetch results');
            }
        };

        fetchResults();
    }, [symptom]);

    return (
        <div className="results-page">
            <h1 className="symptom-title">Results for: {symptom}</h1>
            {results.length > 0 ? (
                <div className="results-container">
                    {results.map((result, index) => (
                        <div key={index} className="result-box">
                            <h2 className="disease-header">{result.disease}</h2>
                            <table className="results-table">
                                <thead>
                                    <tr>
                                        <th>Medicine</th>
                                        <th>Dosage</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>{result.medicine}</td>
                                        <td>{result.dosage}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    ))}
                </div>
            ) : (
                <div className="no-results">No results found.</div>
            )}
        </div>
    );
};

export default ResultsPage;
