import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    contrasena: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Enviar credenciales al servidor para validación
      const response = await axios.post('http://localhost:3000/login', formData);

      if (response.data.success) {
        // Mostramos animación de éxito antes de redirigir
        setTimeout(() => {
          navigate('/dashboard');
        }, 1000);
      } else {
        setError('Credenciales incorrectas. Inténtalo de nuevo.');
      }
    } catch (err) {
      console.error('Error al iniciar sesión:', err);
      setError('Hubo un problema al iniciar sesión. Inténtalo más tarde.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-fluid py-6 mt-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-6">
          <div className="card border-0 shadow-lg rounded-3 overflow-hidden">
            {/* Header con gradiente */}
            <div className="card-header bg-primary bg-gradient text-white text-center p-4 border-0">
              <h2 className="mb-0 fw-bold">
                <i className="bi bi-shield-lock me-2"></i>
                Acceso Seguro
              </h2>
              <p className="mb-0 mt-2 opacity-75">Ingresa tus credenciales para continuar</p>
            </div>
            
            <div className="card-body p-4 p-lg-5">
              {error && (
                <div className="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  {error}
                  <button type="button" className="btn-close" onClick={() => setError('')}></button>
                </div>
              )}
              
              <form onSubmit={handleSubmit} className="needs-validation">
                <div className="form-floating mb-4">
                  <input
                    type="email"
                    id="email"
                    name="email"
                    className="form-control form-control-lg border-primary-subtle"
                    placeholder="nombre@ejemplo.com"
                    value={formData.email}
                    onChange={handleChange}
                    required
                  />
                  <label htmlFor="email">
                    <i className="bi bi-envelope me-2"></i>Correo Electrónico
                  </label>
                </div>
                
                <div className="form-floating mb-4">
                  <input
                    type="password"
                    id="contrasena"
                    name="contrasena"
                    className="form-control form-control-lg border-primary-subtle"
                    placeholder="Contraseña"
                    value={formData.contrasena}
                    onChange={handleChange}
                    required
                  />
                  <label htmlFor="contrasena">
                    <i className="bi bi-key me-2"></i>Contraseña
                  </label>
                </div>
                
                <div className="d-flex justify-content-between align-items-center mb-4">
                  <div className="form-check">
                    <input className="form-check-input" type="checkbox" id="recordarme" />
                    <label className="form-check-label" htmlFor="recordarme">
                      Recordar mis datos
                    </label>
                  </div>
                  <a href="/recuperar" className="text-decoration-none">¿Olvidaste tu contraseña?</a>
                </div>
                
                <div className="d-grid gap-2 mb-4">
                  <button 
                    type="submit" 
                    className="btn btn-primary btn-lg py-3 fw-bold shadow-sm" 
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Verificando...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-box-arrow-in-right me-2"></i>
                        Iniciar Sesión
                      </>
                    )}
                  </button>
                </div>
              
                <div className="text-center mt-4">
                  <div className="d-flex align-items-center justify-content-center mb-3">
                    <hr className="flex-grow-1" />
                    <span className="mx-3 text-muted">O continúa con</span>
                    <hr className="flex-grow-1" />
                  </div>
                  
                  <div className="d-flex justify-content-center gap-4 mb-2">
                    <button className="btn btn-outline-primary rounded-circle p-3" title="Continuar con Google">
                      <i className="bi bi-google fs-5"></i>
                    </button>
                    <button className="btn btn-outline-primary rounded-circle p-3" title="Continuar con Facebook">
                      <i className="bi bi-facebook fs-5"></i>
                    </button>
                    <button className="btn btn-outline-primary rounded-circle p-3" title="Continuar con Apple">
                      <i className="bi bi-apple fs-5"></i>
                    </button>
                  </div>
                </div>
              </form>
            </div>
            
            <div className="card-footer bg-light text-center p-4 border-0">
              <p className="mb-0 fs-6">
                ¿No tienes una cuenta?{' '}
                <a href="/registro" className="text-primary fw-bold text-decoration-none">
                  Regístrate ahora <i className="bi bi-arrow-right"></i>
                </a>
              </p>
            </div>
          </div>
          
          <div className="text-center mt-4 text-muted small">
            <p>© 2025 Tu Empresa · <a href="/privacidad" className="text-decoration-none">Privacidad</a> · <a href="/terminos" className="text-decoration-none">Términos</a></p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;