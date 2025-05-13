import React, { createContext, useState, useContext } from 'react';

// Crear el contexto
const CestaContext = createContext();

// Proveedor del contexto
export const CestaProvider = ({ children }) => {
  const [cesta, setCesta] = useState([]);

  // Añadir un producto a la cesta
  const añadirACesta = (producto) => {
    setCesta((prevCesta) => {
      const productoExistente = prevCesta.find((item) => item.ID_Producto === producto.ID_Producto);
      if (productoExistente) {
        // Incrementar la cantidad si el producto ya está en la cesta
        return prevCesta.map((item) =>
          item.ID_Producto === producto.ID_Producto
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        );
      }
      // Añadir un nuevo producto a la cesta
      return [...prevCesta, { ...producto, cantidad: 1 }];
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