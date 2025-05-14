import React from 'react';
import { useCesta } from '../Contextos/CestaContext';

const ModalCesta = ({ getImageUrl }) => {
  const {
    cesta,
    showCesta,
    toggleCesta,
    añadirACesta,
    eliminarDeCesta,
    vaciarCesta,
    calcularTotal,
    realizarPedido,
  } = useCesta();

  return (
    <>
      {showCesta && <div className="modal-backdrop fade show" style={{ zIndex: 1040 }}></div>}
      <div className={`modal fade ${showCesta ? 'show d-block' : ''}`} tabIndex="-1" style={{ zIndex: 1050 }}>
        <div className="modal-dialog modal-dialog-centered modal-lg">
          <div className="modal-content shadow-lg border-0">
            <div className="modal-header bg-primary text-white">
              <h5 className="modal-title">
                <i className="bi bi-cart-fill me-2"></i>
                Mi Cesta
              </h5>
              <button type="button" className="btn-close btn-close-white" onClick={toggleCesta}></button>
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
                                  onClick={() => añadirACesta({ ...item, cantidad: -1 })}
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
                            <td className="text-end">${parseFloat(item.PrecioProducto).toFixed(2)}</td>
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
                      <button className="btn btn-outline-danger" onClick={vaciarCesta}>
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
                  <p className="text-muted">Añade productos a tu cesta para realizar un pedido</p>
                  <button className="btn btn-primary mt-2" onClick={toggleCesta}>
                    <i className="bi bi-shop me-2"></i>
                    Seguir comprando
                  </button>
                </div>
              )}
            </div>
            {cesta.length > 0 && (
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={toggleCesta}>
                  Seguir comprando
                </button>
                <button type="button" className="btn btn-primary" onClick={realizarPedido}>
                  <i className="bi bi-check-circle me-2"></i>
                  Realizar Pedido
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default ModalCesta;