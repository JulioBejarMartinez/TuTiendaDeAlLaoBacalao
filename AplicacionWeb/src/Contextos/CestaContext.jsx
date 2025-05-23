import React, { createContext, useState, useContext } from 'react';
import axios from 'axios';

// Crear el contexto
const CestaContext = createContext();

// Proveedor del contexto
export const CestaProvider = ({ children }) => {
  const [cesta, setCesta] = useState([]);
  const [showCesta, setShowCesta] = useState(false); // Estado para mostrar/ocultar el modal de la cesta

  // Añadir o restar un producto de la cesta
  const añadirACesta = (producto) => {
    // Validar que producto es un objeto válido y no un evento
    if (!producto || typeof producto !== 'object' || producto.constructor.name === 'SyntheticEvent' || producto.target) {
      console.error('Error: Se pasó un evento en lugar de un producto a añadirACesta');
      return;
    }

    // Solo los campos necesarios para el pedido
    const productoParaCesta = {
      ID_Producto: producto.ID_Producto,
      Nombre: producto.Nombre,
      PrecioProducto: parseFloat(producto.PrecioProducto), // Asegurar que es un número
      cantidad: parseInt(producto.cantidad) || 1, // Asegurar que es un número entero
    };

    // Validar que los campos requeridos estén presentes
    if (!productoParaCesta.ID_Producto || !productoParaCesta.Nombre || !productoParaCesta.PrecioProducto) {
      console.error('Error: Faltan campos requeridos en el producto', productoParaCesta);
      return;
    }

    setCesta((prevCesta) => {
      const productoExistente = prevCesta.find((item) => item.ID_Producto === productoParaCesta.ID_Producto);

      if (productoExistente) {
        const nuevaCantidad = productoExistente.cantidad + (productoParaCesta.cantidad || 1);

        if (nuevaCantidad <= 0) {
          return prevCesta.filter((item) => item.ID_Producto !== productoParaCesta.ID_Producto);
        }

        return prevCesta.map((item) =>
          item.ID_Producto === productoParaCesta.ID_Producto
            ? { ...item, cantidad: nuevaCantidad }
            : item
        );
      }

      return [...prevCesta, productoParaCesta];
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
    return cesta.reduce((total, item) => total + (parseFloat(item.PrecioProducto) * parseInt(item.cantidad)), 0).toFixed(2);
  };

  // Calcular el número total de items en la cesta
  const calcularCantidadItems = () => {
    return cesta.reduce((total, item) => total + parseInt(item.cantidad), 0);
  };

  // Función para obtener el ID del cliente desde el token
  const obtenerClienteId = async () => {
    try {
      const token = localStorage.getItem('token'); // Asumiendo que guardas el token en localStorage
      if (!token) {
        throw new Error('No hay token de autenticación');
      }

      const response = await axios.get('http://localhost:3000/usuario', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      return response.data.ID_Cliente;
    } catch (error) {
      console.error('Error al obtener el ID del cliente:', error);
      throw new Error('No se pudo obtener la información del cliente');
    }
  };

  const realizarPedido = async () => {
    if (cesta.length === 0) {
      alert('La cesta está vacía');
      return;
    }

    try {
      // Obtener el ID del cliente desde el token
      const clienteId = await obtenerClienteId();
      
      if (!clienteId) {
        alert('Error: No se pudo identificar al cliente. Por favor, inicia sesión nuevamente.');
        return;
      }

      // Preparar los datos del pedido de forma limpia
      const datosLimpios = {
        clienteId: parseInt(clienteId),
        productos: cesta.map(item => ({
          ID_Producto: parseInt(item.ID_Producto),
          cantidad: parseInt(item.cantidad)
        })),
        total: parseFloat(calcularTotal())
      };

      console.log('Datos del pedido a enviar:', datosLimpios);

      const response = await axios.post('http://localhost:3000/realizar-pedido', datosLimpios, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Respuesta del servidor:', response.data);
      alert('¡Pedido realizado con éxito!');
      vaciarCesta();
      setShowCesta(false);
    } catch (error) {
      console.error('Error completo:', error);
      console.error('Datos de la respuesta:', error.response?.data);
      
      if (error.message === 'No se pudo obtener la información del cliente') {
        alert('Error: No se pudo identificar al cliente. Por favor, inicia sesión nuevamente.');
      } else {
        alert(`Error al realizar el pedido: ${error.response?.data?.error || error.message}`);
      }
    }
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