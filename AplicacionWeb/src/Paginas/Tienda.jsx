import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useCesta } from '../Contextos/CestaContext'; // Importar el hook useCesta

const Tienda = () => {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [categorias, setCategorias] = useState([]);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState('Todas');
  const [imagenError, setImagenError] = useState({});
  const [showCesta, setShowCesta] = useState(false); // Estado para mostrar/ocultar la cesta
  const navigate = useNavigate();
  const API_URL = 'http://localhost:3000';
  
  // Usar el contexto de la cesta
  const { cesta, añadirACesta, eliminarDeCesta, vaciarCesta } = useCesta();

  // Calcular el total de la cesta
  const calcularTotal = () => {
    return cesta.reduce((total, item) => total + (item.PrecioProducto * item.cantidad), 0).toFixed(2);
  };

  // Calcular el número total de items en la cesta
  const calcularCantidadItems = () => {
    return cesta.reduce((total, item) => total + item.cantidad, 0);
  };

  useEffect(() => {
    const fetchProductos = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`${API_URL}/productos`);
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
      return 'https://via.placeholder.com/200x200?text=Imagen+no+disponible';
    }
    return `${API_URL}/productos/imagen/${producto.ID_Producto}?t=${new Date().getTime()}`;
  };

  const handleComprar = (producto) => {
    // Verificar stock antes de añadir a la cesta
    if (producto.StockActual <= 0) {
      alert(`Lo sentimos, ${producto.Nombre} no está disponible en este momento.`);
      return;
    }

    // Añadir a la cesta
    añadirACesta(producto);
    
    // Mostrar mensaje de confirmación
    const toast = document.createElement('div');
    toast.className = 'toast-notification bg-success text-white p-3 rounded-3 shadow';
    toast.style.position = 'fixed';
    toast.style.bottom = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '1050';
    toast.innerHTML = `<div class="d-flex align-items-center">
      <i class="bi bi-check-circle-fill me-2 fs-4"></i>
      <span>¡${producto.Nombre} añadido a la cesta!</span>
    </div>`;
    
    document.body.appendChild(toast);
    
    // Eliminar el toast después de 3 segundos
    setTimeout(() => {
      document.body.removeChild(toast);
    }, 3000);
  };

  const toggleProfileMenu = () => {
    setShowProfileMenu(!showProfileMenu);
  };

  const toggleCesta = () => {
    setShowCesta(!showCesta);
  };

  const filtrarProductos = () => {
    if (categoriaSeleccionada === 'Todas') {
      return productos;
    }
    return productos.filter(producto => producto.Tipo === categoriaSeleccionada);
  };

  const realizarPedido = () => {
    if (cesta.length === 0) {
      alert('La cesta está vacía');
      return;
    }
    
    // Aquí implementarías la lógica para procesar el pedido
    alert('¡Pedido realizado con éxito!');
    vaciarCesta();
    setShowCesta(false);
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
    <div className="tienda-wrapper min-vh-100 d-flex flex-column bg-light">
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
            <i className="bi bi-shop me-2"></i>
            Tu Tienda de Al Lao
          </a>
          <div className="ms-auto d-flex align-items-center">
            {/* Botón de la cesta */}
            <button 
              className="btn btn-outline-light rounded-pill me-2 position-relative" 
              onClick={toggleCesta}
            >
              <i className="bi bi-cart-fill"></i>
              {calcularCantidadItems() > 0 && (
                <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                  {calcularCantidadItems()}
                </span>
              )}
            </button>
            
            {/* Menú de usuario */}
            <div className="dropdown">
              <button 
                className="btn btn-outline-light rounded-pill d-flex align-items-center px-3" 
                onClick={toggleProfileMenu}
              >
                <div className="avatar me-2 bg-white text-primary rounded-circle d-flex align-items-center justify-content-center" style={{ width: '32px', height: '32px' }}>
                  U
                </div>
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

      {/* Modal de la cesta */}
      {showCesta && (
        <div className="modal-backdrop fade show" style={{ zIndex: 1040 }}></div>
      )}
      
      <div className={`modal fade ${showCesta ? 'show d-block' : ''}`} tabIndex="-1" style={{ zIndex: 1050 }}>
        <div className="modal-dialog modal-dialog-centered modal-lg">
          <div className="modal-content shadow-lg border-0">
            <div className="modal-header bg-primary text-white">
              <h5 className="modal-title">
                <i className="bi bi-cart-fill me-2"></i>
                Mi Cesta
              </h5>
              <button 
                type="button" 
                className="btn-close btn-close-white" 
                onClick={toggleCesta}
              ></button>
            </div>
            <div className="modal-body">
              {cesta.length > 0 ? (
                <>
                  <div className="table-responsive">
                    <table className="table table-hover align-middle">
                      <thead>
                        <tr>
                          <th scope="col" width="80">Imagen</th>
                          <th scope="col">Producto</th>
                          <th scope="col" className="text-center">Cantidad</th>
                          <th scope="col" className="text-end">Precio</th>
                          <th scope="col" className="text-end">Subtotal</th>
                          <th scope="col" width="50"></th>
                        </tr>
                      </thead>
                      <tbody>
                        {cesta.map((item) => (
                          <tr key={item.ID_Producto}>
                            <td>
                              <img 
                                src={getImageUrl(item)} 
                                alt={item.Nombre} 
                                className="img-fluid rounded" 
                                style={{ width: '60px', height: '60px', objectFit: 'cover' }}
                              />
                            </td>
                            <td>
                              <div className="fw-bold">{item.Nombre}</div>
                              <div className="small text-muted">{item.Tipo}</div>
                            </td>
                            <td className="text-center">
                              <div className="d-flex align-items-center justify-content-center">
                                <button 
                                  className="btn btn-sm btn-outline-primary" 
                                  onClick={() => añadirACesta({...item, cantidad: -1})}
                                  disabled={item.cantidad <= 1}
                                >
                                  <i className="bi bi-dash"></i>
                                </button>
                                <span className="mx-2 fw-bold">{item.cantidad}</span>
                                <button 
                                  className="btn btn-sm btn-outline-primary" 
                                  onClick={() => añadirACesta({ ...item, cantidad: 1 })}
                                  disabled={item.cantidad >= item.StockActual}
                                >
                                  <i className="bi bi-plus"></i>
                                </button>
                              </div>
                            </td>
                            <td className="text-end">
                              ${parseFloat(item.PrecioProducto).toFixed(2)}
                            </td>
                            <td className="text-end fw-bold">
                              ${(item.PrecioProducto * item.cantidad).toFixed(2)}
                            </td>
                            <td>
                              <button 
                                className="btn btn-sm btn-outline-danger" 
                                onClick={() => eliminarDeCesta(item.ID_Producto)}
                              >
                                <i className="bi bi-trash"></i>
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  <div className="d-flex justify-content-between align-items-center mt-4 p-3 bg-light rounded-3">
                    <div>
                      <button 
                        className="btn btn-outline-danger" 
                        onClick={vaciarCesta}
                      >
                        <i className="bi bi-trash me-2"></i>
                        Vaciar Cesta
                      </button>
                    </div>
                    <div className="text-end">
                      <div className="fs-5">
                        Total: <span className="fw-bold text-primary">${calcularTotal()}</span>
                      </div>
                      <div className="text-muted small">IVA incluido</div>
                    </div>
                  </div>
                </>
              ) : (
                <div className="text-center py-5">
                  <i className="bi bi-cart-x display-1 text-muted"></i>
                  <h3 className="mt-3">Tu cesta está vacía</h3>
                  <p className="text-muted">
                    Añade productos a tu cesta para realizar un pedido
                  </p>
                  <button 
                    className="btn btn-primary mt-2" 
                    onClick={toggleCesta}
                  >
                    <i className="bi bi-shop me-2"></i>
                    Seguir comprando
                  </button>
                </div>
              )}
            </div>
            {cesta.length > 0 && (
              <div className="modal-footer">
                <button 
                  type="button" 
                  className="btn btn-secondary" 
                  onClick={toggleCesta}
                >
                  Seguir comprando
                </button>
                <button 
                  type="button" 
                  className="btn btn-primary" 
                  onClick={realizarPedido}
                >
                  <i className="bi bi-check-circle me-2"></i>
                  Realizar Pedido
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="container my-4 flex-grow-1">
        <h1 className="text-center mb-4 fw-bold text-primary">Nuestros Productos</h1>
        
        {/* Filtro por categorías */}
        <div className="row mb-4">
          <div className="col-12">
            <div className="d-flex flex-wrap justify-content-center gap-2">
              {categorias.map((categoria) => (
                <button
                  key={categoria}
                  className={`btn btn-lg ${
                    categoriaSeleccionada === categoria 
                    ? 'btn-primary' 
                    : 'btn-outline-primary border-2'
                  } rounded-pill px-4`}
                  onClick={() => setCategoriaSeleccionada(categoria)}
                >
                  {categoria}
                </button>
              ))}
            </div>
          </div>
        </div>
        
        {/* Productos */}
        <div className="row g-4">
        {filtrarProductos().length > 0 ? (
          filtrarProductos().map((producto) => (
            <div className="col-sm-6 col-md-4 col-lg-3" key={producto.ID_Producto}>
              <div className="card h-100 border-0 shadow-lg rounded-3 overflow-hidden hover-shadow transition-all">
                <div
                  className="product-image-container position-relative"
                  style={{ height: '250px', cursor: 'pointer' }}
                  onClick={() => navigate(`/producto/${producto.ID_Producto}`)}
                >
                  <img
                    src={getImageUrl(producto)}
                    alt={producto.Nombre}
                    className="img-fluid h-100 w-100 object-fit-cover"
                    onError={() => handleImageError(producto.ID_Producto)}
                  />
                  {producto.StockActual <= 0 && (
                    <div className="agotado-overlay position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-dark bg-opacity-50">
                      <span className="badge bg-danger fs-5 p-2 rounded-pill">Agotado</span>
                    </div>
                  )}
                </div>
                <div className="card-body d-flex flex-column p-4">
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <h5
                      className="card-title mb-0 fs-5 fw-bold text-primary"
                      style={{ cursor: 'pointer' }}
                      onClick={() => navigate(`/producto/${producto.ID_Producto}`)}
                    >
                      {producto.Nombre}
                    </h5>
                    <span className="badge bg-primary bg-opacity-10 text-primary border border-primary rounded-pill">
                      {producto.Tipo}
                    </span>
                  </div>
                  <p className="card-text text-muted flex-grow-1 mb-3">{producto.Descripcion}</p>
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <p className="card-text fw-bold text-primary mb-0 fs-5">
                      ${parseFloat(producto.PrecioProducto).toFixed(2)}
                    </p>
                    <span
                      className={`badge ${
                        producto.StockActual > 0
                          ? 'bg-success bg-opacity-10 text-success'
                          : 'bg-danger bg-opacity-10 text-danger'
                      } rounded-pill`}
                    >
                      {producto.StockActual > 0 ? `Stock: ${producto.StockActual}` : 'Sin stock'}
                    </span>
                  </div>
                  <div className="d-flex gap-2">
                    <button
                      className={`btn ${
                        producto.StockActual > 0 ? 'btn-outline-primary' : 'btn-outline-secondary'
                      } flex-grow-1 py-2 rounded-pill`}
                      onClick={() => navigate(`/producto/${producto.ID_Producto}`)}
                    >
                      <i className="bi bi-eye me-2"></i>Ver detalle
                    </button>
                    <button
                      className={`btn ${
                        producto.StockActual > 0 ? 'btn-primary' : 'btn-secondary'
                      } flex-grow-1 py-2 rounded-pill`}
                      onClick={() => handleComprar(producto)}
                      disabled={producto.StockActual <= 0}
                    >
                      {producto.StockActual > 0 ? (
                        <>
                          <i className="bi bi-cart-plus me-2"></i>Añadir
                        </>
                      ) : (
                        <>
                          <i className="bi bi-exclamation-circle me-2"></i>No disponible
                        </>
                      )}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="col-12 text-center py-5">
            <i className="bi bi-emoji-frown display-1 text-muted"></i>
            <h3 className="mt-3 text-primary">No hay productos disponibles en esta categoría</h3>
            <button
              className="btn btn-primary btn-lg mt-3 px-4 py-2 rounded-pill"
              onClick={() => setCategoriaSeleccionada('Todas')}
            >
              <i className="bi bi-arrow-left me-2"></i>Ver todos los productos
            </button>
          </div>
        )}
      </div>
    </div>

      {/* Floater para mostrar cantidad de productos en la cesta (visible en móvil) */}
      {calcularCantidadItems() > 0 && (
        <div 
          className="position-fixed d-md-block d-lg-none bottom-0 end-0 mb-4 me-4"
          style={{ zIndex: 1030 }}
        >
          <button 
            className="btn btn-primary btn-lg rounded-circle shadow-lg d-flex align-items-center justify-content-center position-relative"
            style={{ width: '60px', height: '60px' }}
            onClick={toggleCesta}
          >
            <i className="bi bi-cart-fill fs-4"></i>
            <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
              {calcularCantidadItems()}
            </span>
          </button>
        </div>
      )}
      
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

export default Tienda;