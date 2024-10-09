import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SearchPage from './SearchPage';
import ResultsPage from './ResultsPage';
import EditMode from './EditMode';

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<SearchPage />} />
                <Route path="/results/:symptom" element={<ResultsPage />} />
                <Route path="/edit" element={<EditMode />} />
            </Routes>
        </Router>
    );
};

export default App;
