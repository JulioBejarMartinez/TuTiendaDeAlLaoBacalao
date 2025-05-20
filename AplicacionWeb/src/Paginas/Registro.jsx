import React, { useState } from 'react';
import axios from 'axios';
import bcrypt from 'bcryptjs';

const Registro = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    contrasena: '',
    confirmarContrasena: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    // Validar que las contraseñas coincidan
    if (formData.contrasena !== formData.confirmarContrasena) {
      setError('Las contraseñas no coinciden');
      setLoading(false);
      return;
    }

    try {
      // Generar hash de la contraseña
      const hashedPassword = await bcrypt.hash(formData.contrasena, 10);

      // Crear un nuevo objeto con la contraseña hasheada
      const dataToSend = {
        ...formData,
        contrasena: hashedPassword,
      };
      
      // Eliminar confirmarContrasena del objeto a enviar
      delete dataToSend.confirmarContrasena;

      // Enviar los datos al servidor
      const response = await axios.post('http://localhost:3000/registro', dataToSend);
      setSuccess('¡Registro exitoso! Redirigiendo al inicio de sesión...');
      
      // Redirigir después de un tiempo
      setTimeout(() => {
        window.location.href = '/login';
      }, 2000);
      
    } catch (error) {
      console.error('Error al registrar:', error);
      setError(error.response?.data?.message || 'Hubo un error al registrar el cliente');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container-fluid py-4 mt-3">
      {/* Logo de la empresa */}
      <div className="row justify-content-center mb-4">
        <div className="col-12 col-md-8 col-lg-6 text-center">
          <img 
            src="/TuTiendaDeAlLaoLogo.png" 
            alt="TaTiendaDeAlLao" 
            className="img-fluid" 
            style={{ maxHeight: '120px' }}
          />
        </div>
      </div>

      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-6">
          <div className="card border-0 shadow-lg rounded-3 overflow-hidden">
            {/* Header con gradiente */}
            <div className="card-header bg-primary bg-gradient text-white text-center p-4 border-0">
              <h2 className="mb-0 fw-bold">
                <i className="bi bi-person-plus-fill me-2"></i>
                Registro de Cuenta
              </h2>
              <p className="mb-0 mt-2 opacity-75">Crea tu cuenta para acceder a todos los beneficios</p>
            </div>
            
            <div className="card-body p-4 p-lg-5">
              {error && (
                <div className="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  {error}
                  <button type="button" className="btn-close" onClick={() => setError('')}></button>
                </div>
              )}
              
              {success && (
                <div className="alert alert-success alert-dismissible fade show mb-4" role="alert">
                  <i className="bi bi-check-circle-fill me-2"></i>
                  {success}
                  <button type="button" className="btn-close" onClick={() => setSuccess('')}></button>
                </div>
              )}
              
              <form onSubmit={handleSubmit} className="needs-validation">
                <div className="row">
                  <div className="col-md-6 mb-4">
                    <div className="form-floating">
                      <input
                        type="text"
                        id="nombre"
                        name="nombre"
                        className="form-control form-control-lg border-primary-subtle"
                        placeholder="Tu nombre"
                        value={formData.nombre}
                        onChange={handleChange}
                        required
                      />
                      <label htmlFor="nombre">
                        <i className="bi bi-person me-2"></i>Nombre
                      </label>
                    </div>
                  </div>
                  
                  <div className="col-md-6 mb-4">
                    <div className="form-floating">
                      <input
                        type="text"
                        id="apellido"
                        name="apellido"
                        className="form-control form-control-lg border-primary-subtle"
                        placeholder="Tu apellido"
                        value={formData.apellido}
                        onChange={handleChange}
                        required
                      />
                      <label htmlFor="apellido">
                        <i className="bi bi-person-fill me-2"></i>Apellido
                      </label>
                    </div>
                  </div>
                </div>
                
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
                    type="tel"
                    id="telefono"
                    name="telefono"
                    className="form-control form-control-lg border-primary-subtle"
                    placeholder="Tu teléfono"
                    value={formData.telefono}
                    onChange={handleChange}
                  />
                  <label htmlFor="telefono">
                    <i className="bi bi-telephone me-2"></i>Teléfono
                  </label>
                </div>
                
                <div className="row">
                  <div className="col-md-6 mb-4">
                    <div className="form-floating">
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
                  </div>
                  
                  <div className="col-md-6 mb-4">
                    <div className="form-floating">
                      <input
                        type="password"
                        id="confirmarContrasena"
                        name="confirmarContrasena"
                        className="form-control form-control-lg border-primary-subtle"
                        placeholder="Confirmar contraseña"
                        value={formData.confirmarContrasena}
                        onChange={handleChange}
                        required
                      />
                      <label htmlFor="confirmarContrasena">
                        <i className="bi bi-shield-lock me-2"></i>Confirmar
                      </label>
                    </div>
                  </div>
                </div>
                
                <div className="form-check mb-4">
                  <input className="form-check-input" type="checkbox" id="terminos" required />
                  <label className="form-check-label" htmlFor="terminos">
                    Acepto los <a href="/terminos" className="text-decoration-none">Términos y Condiciones</a> y la <a href="/privacidad" className="text-decoration-none">Política de Privacidad</a>
                  </label>
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
                        Procesando...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-check-circle me-2"></i>
                        Crear Cuenta
                      </>
                    )}
                  </button>
                </div>
              </form>
            </div>
            
            <div className="card-footer bg-light text-center p-4 border-0">
              <p className="mb-0 fs-6">
                ¿Ya tienes una cuenta?{' '}
                <a href="/login" className="text-primary fw-bold text-decoration-none">
                  Inicia sesión <i className="bi bi-arrow-right"></i>
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

export default Registro;