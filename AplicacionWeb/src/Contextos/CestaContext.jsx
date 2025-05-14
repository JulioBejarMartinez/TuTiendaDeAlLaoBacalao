import React, { createContext, useState, useContext } from 'react';

// Crear el contexto
const CestaContext = createContext();

// Proveedor del contexto
export const CestaProvider = ({ children }) => {
  const [cesta, setCesta] = useState([]);

  // Añadir o restar un producto de la cesta
  const añadirACesta = (producto) => {
    setCesta((prevCesta) => {
      const productoExistente = prevCesta.find((item) => item.ID_Producto === producto.ID_Producto);

      if (productoExistente) {
        // Actualizar la cantidad del producto existente
        const nuevaCantidad = productoExistente.cantidad + (producto.cantidad || 1);

        if (nuevaCantidad <= 0) {
          // Eliminar el producto si la cantidad es 0 o menor
          return prevCesta.filter((item) => item.ID_Producto !== producto.ID_Producto);
        }

        // Actualizar la cantidad del producto
        return prevCesta.map((item) =>
          item.ID_Producto === producto.ID_Producto
            ? { ...item, cantidad: nuevaCantidad }
            : item
        );
      }

      // Añadir un nuevo producto a la cesta con cantidad inicial
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

  return (
    <CestaContext.Provider value={{ cesta, añadirACesta, eliminarDeCesta, vaciarCesta }}>
      {children}
    </CestaContext.Provider>
  );
};

// Hook para usar el contexto
export const useCesta = () => useContext(CestaContext);