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
    <div className="dashboard-wrapper min-vh-100 d-flex flex-column full-background">
      {/* Logo y Header */}
      <div className="container-fluid py-4 bg-white text-center border-bottom">
        <img 
          src="/TuTiendaDeAlLaoLogo.png" 
          alt="TuTiendaDeAlLao" 
          className="img-fluid" 
          style={{ maxHeight: '120px' }}
        />
      </div>
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary bg-gradient shadow-sm">
        <div className="container">
          <a className="navbar-brand fw-bold" href="#!">
            <i className="bi bi-speedometer2 me-2"></i>
            Mi Portal
          </a>
          
          <div className="ms-auto d-flex align-items-center">
            <div className="dropdown">
              <button 
                className="btn btn-outline-light rounded-pill d-flex align-items-center px-3" 
                onClick={() => setShowProfileMenu(!showProfileMenu)}
              >
                <div className="avatar me-2 bg-white text-primary rounded-circle d-flex align-items-center justify-content-center" style={{ width: '32px', height: '32px' }}>
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
      <div className="container my-4 flex-grow-1">
        <h1 className="text-center mb-4 fw-bold text-primary">Mi Perfil</h1>
        <div className="row g-4">
          {/* Perfil del usuario */}
          <div className="col-lg-4">
            <div className="card h-100 border-0 shadow-lg rounded-3 overflow-hidden">
              <div className="card-body text-center p-4">
                <div className="avatar-large mx-auto mb-3 bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style={{ width: '100px', height: '100px', fontSize: '2rem' }}>
                  {cliente.Nombre.charAt(0).toUpperCase()}
                </div>
                <h5 className="card-title mb-0 fs-5 fw-bold text-primary">{cliente.Nombre}</h5>
                <p className="card-text text-muted">{cliente.Email}</p>
              </div>
            </div>
          </div>

          {/* Formulario de actualización */}
          <div className="col-lg-8">
            <div className="card h-100 border-0 shadow-lg rounded-3 overflow-hidden">
              <div className="card-body p-4">
                <h5 className="card-title mb-4 fw-bold text-primary">Actualizar información</h5>
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
                  <button type="submit" className="btn btn-primary btn-lg w-100 py-2 rounded-pill">Guardar cambios</button>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-primary bg-gradient text-white py-4 mt-auto">
        <div className="container">
          <div className="row">
            <div className="col text-center">
              <p className="mb-0 fw-light">
                © {new Date().getFullYear()} Tu Tienda de Al Lao
                <span className="mx-2">·</span>
                <a href="/privacidad" className="text-white text-decoration-none">Privacidad</a>
                <span className="mx-2">·</span>
                <a href="/terminos" className="text-white text-decoration-none">Términos</a>
              </p>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Dashboard;