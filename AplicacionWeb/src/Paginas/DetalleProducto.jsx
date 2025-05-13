import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const DetalleProducto = () => {
  const { id } = useParams();
  const [producto, setProducto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imagenError, setImagenError] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const navigate = useNavigate();
  const API_URL = 'http://localhost:3000';

  useEffect(() => {
    const fetchProducto = async () => {
      try {
        const response = await axios.get(`${API_URL}/productos/${id}`);
        setProducto(response.data);
      } catch (err) {
        console.error('Error al cargar el producto:', err);
        setError('No se pudo cargar el producto. Por favor, inténtalo de nuevo más tarde.');
      } finally {
        setLoading(false);
      }
    };

    fetchProducto();
  }, [id]);

  const handleImageError = () => setImagenError(true);
  const getImageUrl = () => imagenError 
    ? 'https://via.placeholder.com/400x400?text=Imagen+no+disponible'
    : `${API_URL}/productos/imagen/${id}?t=${new Date().getTime()}`;

  const handleComprar = () => {
    if (producto?.StockActual <= 0) {
      alert(`Lo sentimos, ${producto.Nombre} no está disponible en este momento.`);
      return;
    }
    alert(`Has comprado: ${producto.Nombre}`);
  };

  if (loading) return (
    <div className="loading-container d-flex flex-column justify-content-center align-items-center min-vh-75">
      <div className="spinner-border text-primary" style={{ width: '3rem', height: '3rem' }} role="status">
        <span className="visually-hidden">Cargando...</span>
      </div>
      <p className="mt-3 text-muted">Cargando producto...</p>
    </div>
  );

  if (error) return (
    <div className="error-container d-flex flex-column justify-content-center align-items-center min-vh-75">
      <div className="alert alert-danger d-flex align-items-center" role="alert">
        <i className="bi bi-exclamation-triangle-fill me-3 fs-4"></i>
        <div>{error}</div>
      </div>
      <button className="btn btn-outline-primary mt-4 px-4 py-2" onClick={() => navigate('/tienda')}>
        Volver a la Tienda
      </button>
    </div>
  );

  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="container py-3 text-center">
          <img 
            src="/TuTiendaDeAlLaoLogo.png" 
            alt="TuTiendaDeAlLao" 
            className="brand-logo img-fluid"
            style={{ maxHeight: '100px' }}
          />
        </div>
        
        <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow">
          <div className="container">
            <a className="navbar-brand fw-bold" href="#!">
              <i className="bi bi-shop me-2"></i>
              Tu Tienda de Al Lao
            </a>
            
            <div className="ms-auto">
              <div className="dropdown">
                <button 
                  className="btn btn-outline-light rounded-pill px-3 py-1 d-flex align-items-center"
                  onClick={() => setShowProfileMenu(!showProfileMenu)}
                >
                  <div className="user-avatar bg-white text-primary rounded-circle me-2">
                    <i className="bi bi-person fs-5"></i>
                  </div>
                  <span className="d-none d-md-inline">Usuario</span>
                  <i className="bi bi-chevron-down ms-2"></i>
                </button>
                
                {showProfileMenu && (
                  <div className="dropdown-menu dropdown-menu-end shadow show">
                    <button className="dropdown-item" onClick={() => navigate('/dashboard')}>
                      <i className="bi bi-speedometer2 me-3"></i>
                      Dashboard
                    </button>
                    <button 
                      className="dropdown-item text-danger"
                      onClick={() => navigate('/login')}
                    >
                      <i className="bi bi-box-arrow-right me-3"></i>
                      Cerrar sesión
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </nav>
      </header>

      {/* Contenido Principal */}
      <main className="flex-grow-1 py-5">
        <div className="container">
          <div className="mb-4">
            <button 
              className="btn btn-outline-primary rounded-pill px-4"
              onClick={() => navigate('/tienda')}
            >
              <i className="bi bi-arrow-left me-2"></i>
              Volver
            </button>
          </div>

          <div className="row g-5">
            {/* Sección de Imagen */}
            <div className="col-lg-6">
              <div className="card shadow-sm border-0 overflow-hidden">
                <div className="ratio ratio-1x1 bg-light">
                  <img 
                    src={getImageUrl()} 
                    alt={producto.Nombre}
                    className="img-fluid object-fit-cover"
                    onError={handleImageError}
                  />
                  {producto.StockActual <= 0 && (
                    <div className="position-absolute top-50 start-50 translate-middle">
                      <span className="badge bg-danger fs-5 px-4 py-2">AGOTADO</span>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Sección de Detalles */}
            <div className="col-lg-6">
              <div className="d-flex flex-column h-100">
                <div className="card shadow-sm border-0 h-100">
                  <div className="card-body p-4">
                    {/* Header del producto */}
                    <div className="d-flex justify-content-between align-items-start mb-4">
                      <h1 className="h2 fw-bold text-primary mb-0">{producto.Nombre}</h1>
                      <span className="badge bg-primary bg-opacity-10 text-primary border border-primary">
                        {producto.Tipo}
                      </span>
                    </div>

                    <p className="lead text-muted mb-4">{producto.Descripcion}</p>

                    {/* Precio y Stock */}
                    <div className="d-flex justify-content-between align-items-center mb-5">
                      <div className="h3 fw-bold text-primary">
                        ${parseFloat(producto.PrecioProducto).toFixed(2)}
                      </div>
                      <span className={`badge ${producto.StockActual > 0 ? 'bg-success' : 'bg-danger'} bg-opacity-10 text-${producto.StockActual > 0 ? 'success' : 'danger'} fs-6`}>
                        {producto.StockActual > 0 ? `${producto.StockActual} disponibles` : 'Sin stock'}
                      </span>
                    </div>

                    {/* Botón de compra */}
                    <button 
                      className={`btn ${producto.StockActual > 0 ? 'btn-primary' : 'btn-secondary'} btn-lg w-100 py-3 mb-4`}
                      onClick={handleComprar}
                      disabled={producto.StockActual <= 0}
                    >
                      <i className={`bi ${producto.StockActual > 0 ? 'bi-cart-plus' : 'bi-exclamation-circle'} me-2`}></i>
                      {producto.StockActual > 0 ? 'Comprar ahora' : 'No disponible'}
                    </button>

                    {/* Detalles adicionales */}
                    <div className="card border-0 bg-light">
                      <div className="card-body p-3">
                        <h5 className="fw-bold mb-3">Información del producto</h5>
                        <dl className="row mb-0">
                          <dt className="col-sm-4 text-muted">Categoría</dt>
                          <dd className="col-sm-8 fw-medium">{producto.Tipo}</dd>
                          
                          <dt className="col-sm-4 text-muted">ID Producto</dt>
                          <dd className="col-sm-8 fw-medium">{producto.ID_Producto}</dd>
                        </dl>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-primary text-white mt-auto">
        <div className="container py-4">
          <div className="text-center small">
            <p className="mb-0">
              © {new Date().getFullYear()} Tu Tienda de Al Lao
              <span className="mx-2">·</span>
              <a href="/privacidad" className="link-light text-decoration-none">Privacidad</a>
              <span className="mx-2">·</span>
              <a href="/terminos" className="link-light text-decoration-none">Términos</a>
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default DetalleProducto;