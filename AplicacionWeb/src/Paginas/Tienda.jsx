import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Tienda = () => {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [categorias, setCategorias] = useState([]);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState('Todas');
  const [imagenError, setImagenError] = useState({});
  const navigate = useNavigate();
  const API_URL = 'http://localhost:3000';

  useEffect(() => {
    const fetchProductos = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`${API_URL}/productos`); // Cambiado a /productos
        setProductos(response.data);

        // Extraer categorías únicas (tipos) de productos
        const tiposUnicos = [...new Set(response.data.map(producto => producto.Tipo))];
        setCategorias(['Todas', ...tiposUnicos]);

        setError(null);
      } catch (err) {
        console.error('Error al cargar productos:', err);
        setError('No se pudieron cargar los productos. Por favor, inténtalo de nuevo más tarde.');
      } finally {
        setLoading(false);
      }
    };

    fetchProductos();
  }, []);

  const handleImageError = (id) => {
    setImagenError(prev => ({
      ...prev,
      [id]: true
    }));
  };

  const getImageUrl = (producto) => {
    if (imagenError[producto.ID_Producto]) {
      return 'https://via.placeholder.com/200x200?text=Imagen+no+disponible'; // Placeholder si hay error
    }
    // Usar el endpoint para obtener la imagen del producto
    // Añadimos timestamp para evitar caché del navegador
    return `${API_URL}/productos/imagen/${producto.ID_Producto}?t=${new Date().getTime()}`;
  };

  const handleComprar = (producto) => {
    // Verificar stock antes de comprar
    if (producto.StockActual <= 0) {
      alert(`Lo sentimos, ${producto.Nombre} no está disponible en este momento.`);
      return;
    }

    alert(`Has comprado: ${producto.Nombre}`);
    // Aquí implementarías la lógica para procesar la compra
  };

  const toggleProfileMenu = () => {
    setShowProfileMenu(!showProfileMenu);
  };

  const filtrarProductos = () => {
    if (categoriaSeleccionada === 'Todas') {
      return productos;
    }
    return productos.filter(producto => producto.Tipo === categoriaSeleccionada);
  };

  if (loading) {
    return (
      <div className="loading-container d-flex flex-column justify-content-center align-items-center" style={{ minHeight: '70vh' }}>
        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Cargando...</span>
        </div>
        <p className="mt-3">Cargando productos...</p>
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
          onClick={() => navigate('/dashboard')}
        >
          Volver al Dashboard
        </button>
      </div>
    );
  }

  return (
    <div className="tienda-wrapper min-vh-100 d-flex flex-column" style={{ backgroundColor: '#f0f2f5' }}>
      {/* Logo de la empresa */}
    <div className="container-fluid py-3 bg-light text-center" style={{ backgroundColor: '#f8f9fa' }}>
      <img 
        src="/TuTiendaDeAlLaoLogo.png" 
        alt="TuTiendaDeAlLao" 
        className="img-fluid" 
        style={{ maxHeight: '120px' }}
      />
    </div>
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div className="container">
          <a className="navbar-brand" href="#!">
            <i className="bi bi-shop me-2"></i>
            Tienda
          </a>
          <div className="ms-auto d-flex align-items-center">
            <div className="dropdown">
              <button 
                className="btn btn-outline-light rounded-pill d-flex align-items-center" 
                onClick={toggleProfileMenu}
              >
                <div className="avatar me-2">U</div>
                <span className="d-none d-md-inline">Usuario</span>
                <i className="bi bi-chevron-down ms-2"></i>
              </button>
              {showProfileMenu && (
                <div className="dropdown-menu show dropdown-menu-end shadow">
                  <a className="dropdown-item" href="#!" onClick={() => navigate('/dashboard')}>
                    <i className="bi bi-speedometer2 me-2"></i>
                    Ir al Dashboard
                  </a>
                  <div className="dropdown-divider"></div>
                  <button 
                    className="dropdown-item text-danger" 
                    onClick={() => navigate('/login')}
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
        <h1 className="text-center mb-4">Tienda</h1>
        
        {/* Filtro por categorías */}
        <div className="row mb-4">
          <div className="col-lg-6 mx-auto">
            <div className="d-flex justify-content-center">
              <div className="btn-group" role="group" aria-label="Filtrar por categoría">
                {categorias.map((categoria) => (
                  <button
                    key={categoria}
                    type="button"
                    className={`btn ${categoriaSeleccionada === categoria ? 'btn-primary' : 'btn-outline-primary'}`}
                    onClick={() => setCategoriaSeleccionada(categoria)}
                  >
                    {categoria}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
        
        {/* Productos */}
        <div className="row">
          {filtrarProductos().length > 0 ? (
            filtrarProductos().map((producto) => (
              <div className="col-sm-6 col-md-4 col-lg-3 mb-4" key={producto.ID_Producto}>
                <div className="card h-100 border-0 shadow-sm hover-elevation transition-all">
                  <div className="product-image-container" style={{ height: '200px', position: 'relative', overflow: 'hidden' }}>
                    <img 
                      src={getImageUrl(producto)} 
                      alt={producto.Nombre} 
                      className="card-img-top"
                      onError={() => handleImageError(producto.ID_Producto)}
                      style={{ 
                        width: '100%', 
                        height: '100%', 
                        objectFit: 'cover',
                        transition: 'transform 0.3s ease'
                      }}
                    />
                    {producto.StockActual <= 0 && (
                      <div className="agotado-overlay position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-dark bg-opacity-50">
                        <span className="badge bg-danger fs-5 p-2">Agotado</span>
                      </div>
                    )}
                  </div>
                  <div className="card-body d-flex flex-column">
                    <div className="d-flex justify-content-between align-items-start mb-1">
                      <h5 className="card-title mb-0">{producto.Nombre}</h5>
                      <span className="badge bg-secondary">{producto.Tipo}</span>
                    </div>
                    <p className="card-text text-muted flex-grow-1 mb-2">{producto.Descripcion}</p>
                    <div className="d-flex justify-content-between align-items-center">
                      <p className="card-text fw-bold text-primary mb-0 fs-5">
                        ${parseFloat(producto.PrecioProducto).toFixed(2)}
                      </p>
                      <span className="stock-info text-muted small">
                        {producto.StockActual > 0 ? `Stock: ${producto.StockActual}` : 'Sin stock'}
                      </span>
                    </div>
                    <button 
                      className={`btn ${producto.StockActual > 0 ? 'btn-primary' : 'btn-secondary'} btn-sm w-100 mt-3`} 
                      onClick={() => handleComprar(producto)}
                      disabled={producto.StockActual <= 0}
                    >
                      {producto.StockActual > 0 ? (
                        <><i className="bi bi-cart-plus me-2"></i>Comprar</>
                      ) : (
                        <><i className="bi bi-exclamation-circle me-2"></i>Agotado</>
                      )}
                    </button>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="col-12 text-center py-5">
              <i className="bi bi-emoji-frown display-1 text-muted"></i>
              <h3 className="mt-3">No hay productos disponibles en esta categoría</h3>
              <button 
                className="btn btn-primary mt-3"
                onClick={() => setCategoriaSeleccionada('Todas')}
              >
                Ver todos los productos
              </button>
            </div>
          )}
        </div>
      </div>
      
      {/* Footer */}
      <footer className="bg-light py-4 mt-auto">
        <div className="container">
          <div className="row">
            <div className="col text-center">
              <p className="mb-0">© {new Date().getFullYear()} Tu Tienda de Allao - Todos los derechos reservados</p>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Tienda;