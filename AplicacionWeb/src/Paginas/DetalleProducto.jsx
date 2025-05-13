import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const DetalleProducto = () => {
  const { id } = useParams(); // Obtener el ID del producto desde la URL
  const [producto, setProducto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imagenError, setImagenError] = useState(false);
  const navigate = useNavigate();
  const API_URL = 'http://localhost:3000';

  useEffect(() => {
    const fetchProducto = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`${API_URL}/productos/${id}`);
        setProducto(response.data);
        setError(null);
      } catch (err) {
        console.error('Error al cargar el producto:', err);
        setError('No se pudo cargar el producto. Por favor, inténtalo de nuevo más tarde.');
      } finally {
        setLoading(false);
      }
    };

    fetchProducto();
  }, [id]);

  const handleImageError = () => {
    setImagenError(true);
  };

  const getImageUrl = () => {
    if (imagenError) {
      return 'https://via.placeholder.com/400x400?text=Imagen+no+disponible'; // Placeholder si hay error
    }
    return `${API_URL}/productos/imagen/${id}?t=${new Date().getTime()}`;
  };

  if (loading) {
    return (
      <div className="loading-container d-flex flex-column justify-content-center align-items-center" style={{ minHeight: '70vh' }}>
        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Cargando...</span>
        </div>
        <p className="mt-3">Cargando producto...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container d-flex flex-column justify-content-center align-items-center" style={{ minHeight: '70vh' }}>
        <div className="alert alert-danger" role="alert">
          <i className="bi bi-exclamation-triangle-fill me-2"></i> {error}
        </div>
        <button 
          className="btn btn-primary mt-3" 
          onClick={() => navigate('/tienda')}
        >
          Volver a la Tienda
        </button>
      </div>
    );
  }

  return (
    <div className="detalle-producto-wrapper min-vh-100 d-flex flex-column bg-light">
      {/* Logo y Header */}
      <div className="container-fluid py-4 bg-white text-center border-bottom">
        <img 
          src="/TuTiendaDeAlLaoLogo.png" 
          alt="TuTiendaDeAlLao" 
          className="img-fluid" 
          style={{ maxHeight: '120px' }}
        />
      </div>

      {/* Main Content */}
      <div className="container my-4 flex-grow-1">
        <div className="row g-4">
          {/* Imagen del producto */}
          <div className="col-lg-6">
            <div className="card border-0 shadow-lg rounded-3 overflow-hidden">
              <img 
                src={getImageUrl()} 
                alt={producto.Nombre} 
                className="img-fluid w-100"
                onError={handleImageError}
              />
            </div>
          </div>

          {/* Detalles del producto */}
          <div className="col-lg-6">
            <div className="card border-0 shadow-lg rounded-3 overflow-hidden">
              <div className="card-body p-4">
                <h1 className="card-title fw-bold text-primary">{producto.Nombre}</h1>
                <p className="card-text text-muted">{producto.Descripcion}</p>
                <p className="card-text fw-bold text-primary fs-4">
                  Precio: ${parseFloat(producto.PrecioProducto).toFixed(2)}
                </p>
                <p className="card-text">
                  <span className={`badge ${producto.StockActual > 0 ? 'bg-success' : 'bg-danger'} rounded-pill`}>
                    {producto.StockActual > 0 ? `Stock disponible: ${producto.StockActual}` : 'Sin stock'}
                  </span>
                </p>
                <p className="card-text">
                  <strong>Tipo:</strong> {producto.Tipo}
                </p>
                <p className="card-text">
                  <strong>Stock mínimo:</strong> {producto.StockMinimo}
                </p>
                <p className="card-text">
                  <strong>ID del proveedor:</strong> {producto.ID_Proveedor}
                </p>
                <button 
                  className={`btn ${producto.StockActual > 0 ? 'btn-primary' : 'btn-secondary'} btn-lg w-100 mt-3 py-2 rounded-pill`} 
                  onClick={() => alert(`Has comprado: ${producto.Nombre}`)}
                  disabled={producto.StockActual <= 0}
                >
                  {producto.StockActual > 0 ? (
                    <><i className="bi bi-cart-plus me-2"></i>Comprar ahora</>
                  ) : (
                    <><i className="bi bi-exclamation-circle me-2"></i>No disponible</>
                  )}
                </button>
                <button 
                  className="btn btn-outline-primary btn-lg w-100 mt-3 py-2 rounded-pill" 
                  onClick={() => navigate('/tienda')}
                >
                  <i className="bi bi-arrow-left me-2"></i>Volver a la Tienda
                </button>
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

export default DetalleProducto;