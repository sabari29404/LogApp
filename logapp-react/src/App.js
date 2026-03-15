import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage    from './pages/LoginPage';
import SuccessPage  from './pages/SuccessPage';
import CompaniesPage from './pages/CompaniesPage';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/"          element={<LoginPage />} />
        <Route path="/success"   element={<SuccessPage />} />
        <Route path="/companies" element={<CompaniesPage />} />
        <Route path="*"          element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
