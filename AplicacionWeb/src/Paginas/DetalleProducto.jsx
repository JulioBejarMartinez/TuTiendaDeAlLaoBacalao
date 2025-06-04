import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useCesta } from '../Contextos/CestaContext'; // Importar el hook useCesta
import ModalCesta from '../Componentes/ModalCesta'; // Importar el componente ModalCesta

const DetalleProducto = () => {
  const { id } = useParams();
  const [producto, setProducto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [imagenError, setImagenError] = useState(false);
  const navigate = useNavigate();
  const API_URL = 'http://localhost:3000';

  const { añadirACesta, calcularCantidadItems, toggleCesta } = useCesta(); // Usar el contexto de la cesta

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
  const getImageUrl = () =>
    imagenError
      ? 'https://via.placeholder.com/400x400?text=Imagen+no+disponible'
      : `${API_URL}/productos/imagen/${id}?t=${new Date().getTime()}`;

  const handleComprar = () => {
    if (producto?.StockActual <= 0) {
      alert(`Lo sentimos, ${producto.Nombre} no está disponible en este momento.`);
      return;
    }

    añadirACesta(producto); // Añadir a la cesta
    toggleCesta(); // Abrir la cesta
  };

  if (loading) {
    return (
      <div className="loading-container d-flex flex-column justify-content-center align-items-center min-vh-75">
        <div className="spinner-border text-primary" style={{ width: '3rem', height: '3rem' }} role="status">
          <span className="visually-hidden">Cargando...</span>
        </div>
        <p className="mt-3 text-muted">Cargando producto...</p>
      </div>
    );
  }

  if (error) {
    return (
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
  }

  return (
    <div className="d-flex flex-column min-vh-100 full-background">
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
              <button
                className="btn btn-outline-light rounded-pill position-relative"
                onClick={toggleCesta}
              >
                <i className="bi bi-cart-fill"></i>
                {calcularCantidadItems() > 0 && (
                  <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                    {calcularCantidadItems()}
                  </span>
                )}
              </button>
            </div>
          </div>
        </nav>
      </header>

      {/* Modal de la cesta */}
      <ModalCesta getImageUrl={getImageUrl} />

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
                      <span
                        className={`badge ${
                          producto.StockActual > 0 ? 'bg-success' : 'bg-danger'
                        } bg-opacity-10 text-${producto.StockActual > 0 ? 'success' : 'danger'} fs-6`}
                      >
                        {producto.StockActual > 0 ? `${producto.StockActual} disponibles` : 'Sin stock'}
                      </span>
                    </div>

                    {/* Botón de compra */}
                    <button
                      className={`btn ${
                        producto.StockActual > 0 ? 'btn-primary' : 'btn-secondary'
                      } btn-lg w-100 py-3 mb-4`}
                      onClick={handleComprar}
                      disabled={producto.StockActual <= 0}
                    >
                      <i
                        className={`bi ${
                          producto.StockActual > 0 ? 'bi-cart-plus' : 'bi-exclamation-circle'
                        } me-2`}
                      ></i>
                      {producto.StockActual > 0 ? 'Comprar ahora' : 'No disponible'}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default DetalleProducto;