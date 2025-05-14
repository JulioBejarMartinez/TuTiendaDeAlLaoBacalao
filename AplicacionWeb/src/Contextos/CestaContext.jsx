import React, { createContext, useState, useContext } from 'react';

// Crear el contexto
const CestaContext = createContext();

// Proveedor del contexto
export const CestaProvider = ({ children }) => {
  const [cesta, setCesta] = useState([]);
  const [showCesta, setShowCesta] = useState(false); // Estado para mostrar/ocultar el modal de la cesta

  // Añadir o restar un producto de la cesta
  const añadirACesta = (producto) => {
    setCesta((prevCesta) => {
      const productoExistente = prevCesta.find((item) => item.ID_Producto === producto.ID_Producto);

      if (productoExistente) {
        const nuevaCantidad = productoExistente.cantidad + (producto.cantidad || 1);

        if (nuevaCantidad <= 0) {
          return prevCesta.filter((item) => item.ID_Producto !== producto.ID_Producto);
        }

        return prevCesta.map((item) =>
          item.ID_Producto === producto.ID_Producto
            ? { ...item, cantidad: nuevaCantidad }
            : item
        );
      }

      return [...prevCesta, { ...producto, cantidad: producto.cantidad || 1 }];
    });
  };

  // Eliminar un producto de la cesta
  const eliminarDeCesta = (idProducto) => {
    setCesta((prevCesta) => prevCesta.filter((item) => item.ID_Producto !== idProducto));
  };

  // Vaciar la cesta
  const vaciarCesta = () => {
    setCesta([]);
  };

  // Mostrar/ocultar el modal de la cesta
  const toggleCesta = () => {
    setShowCesta(!showCesta);
  };

  // Calcular el total de la cesta
  const calcularTotal = () => {
    return cesta.reduce((total, item) => total + item.PrecioProducto * item.cantidad, 0).toFixed(2);
  };

  // Calcular el número total de items en la cesta
  const calcularCantidadItems = () => {
    return cesta.reduce((total, item) => total + item.cantidad, 0);
  };

  // Realizar pedido
  const realizarPedido = () => {
    if (cesta.length === 0) {
      alert('La cesta está vacía');
      return;
    }

    alert('¡Pedido realizado con éxito!');
    vaciarCesta();
    setShowCesta(false);
  };

  return (
    <CestaContext.Provider
      value={{
        cesta,
        añadirACesta,
        eliminarDeCesta,
        vaciarCesta,
        showCesta,
        toggleCesta,
        calcularTotal,
        calcularCantidadItems,
        realizarPedido,
      }}
    >
      {children}
    </CestaContext.Provider>
  );
};

// Hook para usar el contexto
export const useCesta = () => useContext(CestaContext);