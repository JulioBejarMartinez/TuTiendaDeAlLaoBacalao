import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const Dashboard = () => {
  const [cliente, setCliente] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [formData, setFormData] = useState({ Nombre: '', Contrasena: '' });
  const [successMessage, setSuccessMessage] = useState('');
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
        setFormData({ Nombre: response.data.Nombre, Contrasena: '' });
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

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');

    try {
      const response = await axios.put('http://localhost:3000/usuario', formData, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setSuccessMessage(response.data.message);
      setError(null);
    } catch (error) {
      console.error('Error al actualizar datos:', error);
      setError('No se pudieron actualizar tus datos. Por favor, inténtalo de nuevo más tarde.');
    }
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
                onClick={() => setShowProfileMenu(!showProfileMenu)}
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
              </div>
            </div>
          </div>
          
          <div className="col-lg-8">
            <div className="card border-0 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Actualizar información</h5>
                {successMessage && (
                  <div className="alert alert-success" role="alert">
                    {successMessage}
                  </div>
                )}
                {error && (
                  <div className="alert alert-danger" role="alert">
                    {error}
                  </div>
                )}
                <form onSubmit={handleFormSubmit}>
                  <div className="mb-3">
                    <label htmlFor="Nombre" className="form-label">Nombre</label>
                    <input 
                      type="text" 
                      className="form-control" 
                      id="Nombre" 
                      name="Nombre" 
                      value={formData.Nombre} 
                      onChange={handleInputChange} 
                    />
                  </div>
                  <div className="mb-3">
                    <label htmlFor="Contrasena" className="form-label">Nueva contraseña</label>
                    <input 
                      type="password" 
                      className="form-control" 
                      id="Contrasena" 
                      name="Contrasena" 
                      value={formData.Contrasena} 
                      onChange={handleInputChange} 
                    />
                  </div>
                  <button type="submit" className="btn btn-primary">Guardar cambios</button>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;