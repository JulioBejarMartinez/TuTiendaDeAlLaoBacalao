import { useState } from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './Paginas/Login'
import Registro from './Paginas/Registro'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import 'bootswatch/dist/brite/bootstrap.min.css';
import "bootstrap-icons/font/bootstrap-icons.css";
import Dashboard from './Paginas/Dashboard';
import Tienda from './Paginas/Tienda';
import './App.css'

function App() {

  return (
    <div className="full-background">
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/tienda" element={<Tienda />} />
      </Routes>
    </Router>
    </div>
  );
}

export default App

