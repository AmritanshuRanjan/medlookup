import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

const ResultsPage = () => {
    const { symptom } = useParams();
    const [results, setResults] = useState([]);

    useEffect(() => {
        const fetchResults = async () => {
            const response = await fetch(`http://localhost:8080/search/${symptom}`);
            if (response.ok) {
                const data = await response.json(); // Expecting JSON response
                setResults(data);
            } else {
                console.error('Failed to fetch results');
            }
        };

        fetchResults();
    }, [symptom]);

    return (
        <div>
            <h1>Results for: {symptom}</h1>
            <table>
                <thead>
                    <tr>
                        <th>Disease</th>
                        <th>Medicine</th>
                        <th>Dosage</th>
                    </tr>
                </thead>
                <tbody>
                    {results.length > 0 ? (
                        results.map((result, index) => (
                            <tr key={index}>
                                <td>{result.disease}</td>
                                <td>{result.medicine}</td>
                                <td>{result.dosage}</td>
                            </tr>
                        ))
                    ) : (
                        <tr><td colSpan="3">No results found.</td></tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default ResultsPage;
