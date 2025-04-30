import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Dashboard = () => {
  const [cliente, setCliente] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const obtenerDatosCliente = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      try {
        setLoading(true);
        const response = await axios.get('http://localhost:3000/usuario', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        setCliente(response.data);
        setError(null);
      } catch (error) {
        console.error('Error al obtener datos:', error);
        setError('No se pudieron cargar tus datos. Por favor, inténtalo de nuevo más tarde.');
        localStorage.removeItem('token');
        navigate('/login');
      } finally {
        setLoading(false);
      }
    };

    obtenerDatosCliente();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const toggleProfileMenu = () => {
    setShowProfileMenu(!showProfileMenu);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <p className="mt-3">Cargando tu información...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <div className="alert alert-danger" role="alert">
          <i className="bi bi-exclamation-triangle-fill me-2"></i> {error}
        </div>
        <button 
          className="btn btn-primary mt-3" 
          onClick={() => navigate('/login')}
        >
          Volver al inicio de sesión
        </button>
      </div>
    );
  }

  return (
    <div className="dashboard-wrapper">
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div className="container">
          <a className="navbar-brand" href="#!">
            <i className="bi bi-speedometer2 me-2"></i>
            Mi Portal
          </a>
          
          <div className="ms-auto d-flex align-items-center">
            <div className="dropdown">
              <button 
                className="btn btn-outline-light rounded-pill d-flex align-items-center" 
                onClick={toggleProfileMenu}
              >
                <div className="avatar me-2">
                  {cliente.Nombre.charAt(0).toUpperCase()}
                </div>
                <span className="d-none d-md-inline">{cliente.Nombre}</span>
                <i className="bi bi-chevron-down ms-2"></i>
              </button>
              
              {showProfileMenu && (
                <div className="dropdown-menu show dropdown-menu-end shadow">
                  <div className="dropdown-header">
                    <h6>{cliente.Nombre}</h6>
                    <small className="text-muted">{cliente.Email}</small>
                  </div>
                  <div className="dropdown-divider"></div>
                  <a className="dropdown-item" href="#!">
                    <i className="bi bi-person-circle me-2"></i>
                    Mi Perfil
                  </a>
                  <a className="dropdown-item" href="#!">
                    <i className="bi bi-gear me-2"></i>
                    Configuración
                  </a>
                  <div className="dropdown-divider"></div>
                  <button 
                    className="dropdown-item text-danger" 
                    onClick={handleLogout}
                  >
                    <i className="bi bi-box-arrow-right me-2"></i>
                    Cerrar sesión
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="container my-4">
        <div className="row">
          <div className="col-lg-4 mb-4">
            <div className="card border-0 shadow-sm">
              <div className="card-body text-center">
                <div className="avatar-large mx-auto mb-3">
                  {cliente.Nombre.charAt(0).toUpperCase()}
                </div>
                <h3 className="card-title">{cliente.Nombre}</h3>
                <p className="text-muted mb-3">{cliente.Email}</p>
                <div className="d-grid">
                  <button className="btn btn-outline-primary">
                    <i className="bi bi-pencil-square me-2"></i>
                    Editar perfil
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div className="col-lg-8">
            <div className="row">
              <div className="col-md-6 mb-4">
                <div className="card border-0 shadow-sm h-100">
                  <div className="card-body">
                    <div className="d-flex align-items-center mb-3">
                      <div className="icon-box bg-primary text-white">
                        <i className="bi bi-telephone"></i>
                      </div>
                      <h5 className="card-title ms-3 mb-0">Contacto</h5>
                    </div>
                    <p className="card-text">
                      <strong>Teléfono:</strong><br />
                      {cliente.Telefono || 'No registrado'}
                    </p>
                    <button className="btn btn-sm btn-light">
                      <i className="bi bi-pencil me-1"></i> Actualizar
                    </button>
                  </div>
                </div>
              </div>
              
              <div className="col-md-6 mb-4">
                <div className="card border-0 shadow-sm h-100">
                  <div className="card-body">
                    <div className="d-flex align-items-center mb-3">
                      <div className="icon-box bg-warning text-white">
                        <i className="bi bi-star"></i>
                      </div>
                      <h5 className="card-title ms-3 mb-0">Programa de fidelidad</h5>
                    </div>
                    <div className="text-center py-2">
                      <h2 className="display-4 fw-bold text-warning">{cliente.PuntosFidelidad}</h2>
                      <p className="text-muted">puntos acumulados</p>
                    </div>
                    <div className="d-grid">
                      <button className="btn btn-warning">
                        <i className="bi bi-gift me-2"></i>
                        Canjear puntos
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div className="col-12 mb-4">
                <div className="card border-0 shadow-sm">
                  <div className="card-header bg-transparent border-0">
                    <h5 className="mb-0">Actividad reciente</h5>
                  </div>
                  <div className="card-body">
                    <div className="activity-item py-2">
                      <div className="d-flex align-items-center">
                        <div className="icon-box bg-success text-white">
                          <i className="bi bi-check-circle"></i>
                        </div>
                        <div className="ms-3">
                          <h6 className="mb-1">Inicio de sesión exitoso</h6>
                          <small className="text-muted">Hoy, {new Date().toLocaleTimeString()}</small>
                        </div>
                      </div>
                    </div>
                    <hr />
                    <div className="activity-item py-2">
                      <div className="d-flex align-items-center">
                        <div className="icon-box bg-info text-white">
                          <i className="bi bi-plus-circle"></i>
                        </div>
                        <div className="ms-3">
                          <h6 className="mb-1">Puntos de fidelidad añadidos</h6>
                          <small className="text-muted">Hace 3 días</small>
                        </div>
                        <span className="ms-auto badge bg-success">+10 pts</span>
                      </div>
                    </div>
                  </div>
                  <div className="card-footer bg-transparent border-0 text-center">
                    <a href="#!" className="text-decoration-none">Ver todo el historial</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;