/**
 * API REST con Express y MySQL
 * Proporciona endpoints para manipular tablas de base de datos
 */

import express from 'express';
import mysql from 'mysql';
import cors from 'cors';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import axios from 'axios';
import http from 'http';
import { Server } from 'socket.io';
import path from 'path';
import multer from 'multer';


// Configuración de la aplicación
const app = express();
const port = 3000;

// Configuración de multer para guardar imágenes en la carpeta "imagenes"
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'imagenes'); // Carpeta donde se guardarán las imágenes
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, uniqueSuffix + path.extname(file.originalname)); // Nombre único para cada archivo
  }
});

const upload = multer({ storage: storage });


// Middleware
app.use(express.json());
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

/**
 * Configuración de la conexión a MySQL
 * @type {mysql.Connection}
 */
const db = mysql.createConnection({
  host: 'dam2.colexio-karbo.com',
  port: 3333,
  user: "dam2",
  password: "Ka3b0134679",
  database: "proyecto_lumarsan_jbejar"
});

// Establecer conexión a la base de datos
db.connect((err) => {
  if (err) {
    console.error('Error al conectar a la base de datos:', err);
    return;
  }
  console.log('Conexión a la base de datos establecida correctamente');
});

/**
 * Rutas de la API
 */

// Ruta principal - healthcheck
app.get('/', (req, res) => {
  res.send('API funcionando correctamente');
});


/**************************************************************************
 *                          ENDPOINTS GENÉRICOS                          *
 * Estos endpoints permiten realizar operaciones genéricas sobre tablas  *
 * de la base de datos. Son útiles para manejar múltiples tablas con un  *
 * conjunto común de operaciones como obtener registros, filtrar datos,  *
 * e insertar nuevos registros.                                          *
 *                                                                       *
 * NOTA: Estos endpoints deben usarse con precaución, ya que permiten    *
 * acceso dinámico a las tablas. Asegúrate de validar adecuadamente los  *
 * datos de entrada para evitar problemas de seguridad como inyecciones  *
 * SQL o accesos no autorizados.                                         *
 **************************************************************************/


/**
 * Obtiene todos los registros de una tabla específica
 * 
 * @route GET /tabla/:nombre
 * @param {string} nombre - Nombre de la tabla a consultar
 * @returns {Array} - Todos los registros de la tabla
 */
app.get('/tabla/:nombre', (req, res) => {
  const nombreTabla = req.params.nombre;
  const tablaEscapada = mysql.escapeId(nombreTabla);
  
  const query = `SELECT * FROM ${tablaEscapada}`;
  
  db.query(query, (err, results) => {
    if (err) {
      console.error('Error al ejecutar consulta SELECT:', err);
      return res.status(500).json({ 
        error: 'Error al leer los registros de la tabla.' 
      });
    }
    res.json(results);
  });
});

/**
 * Filtra registros de una tabla según criterios específicos
 * 
 * @route GET /tabla/:nombre/filtrar
 * @param {string} nombre - Nombre de la tabla a consultar
 * @query {Object} filtros - Pares clave-valor para filtrar registros
 * @returns {Array} - Registros que cumplen con los criterios de filtrado
 */
app.get('/tabla/:nombre/filtrar', (req, res) => {
  const nombreTabla = req.params.nombre;
  const tablaEscapada = mysql.escapeId(nombreTabla);
  const filtros = req.query;
  
  // Validar que se proporcionen filtros
  if (Object.keys(filtros).length === 0) {
    return res.status(400).json({ 
      error: 'Se requiere al menos un parámetro de búsqueda.' 
    });
  }
  
  // Obtener estructura de la tabla para validar campos
  db.query(`DESCRIBE ${tablaEscapada}`, (err, columns) => {
    if (err) {
      console.error('Error al obtener estructura de tabla:', err);
      return res.status(500).json({ 
        error: 'Error al obtener la estructura de la tabla.' 
      });
    }
    
    // Construir condiciones para la consulta SQL
    const conditions = Object.keys(filtros)
      .filter(key => columns.some(col => col.Field === key))
      .map(key => `${mysql.escapeId(key)} = ${mysql.escape(filtros[key])}`)
      .join(' AND ');
    
    // Si no hay condiciones válidas, devolver array vacío
    if (!conditions) {
      return res.json([]);
    }
    
    const query = `SELECT * FROM ${tablaEscapada} WHERE ${conditions}`;
    
    db.query(query, (err, results) => {
      if (err) {
        console.error('Error al ejecutar consulta filtrada:', err);
        return res.status(500).json({ 
          error: 'Error al buscar registros.' 
        });
      }
      res.json(results);
    });
  });
});

/**
 * Inserta un nuevo registro en una tabla específica
 * 
 * @route POST /tabla/:nombre
 * @param {string} nombre - Nombre de la tabla donde insertar
 * @body {Object} datos - Datos a insertar (pares campo-valor)
 * @returns {Object} - ID del registro insertado
 */
app.post('/tabla/:nombre', async (req, res) => {
  const nombreTabla = req.params.nombre;
  const tablaEscapada = mysql.escapeId(nombreTabla);
  const datos = req.body;
  
  // Preparar campos y valores para la consulta de inserción
  const campos = Object.keys(datos)
    .map(campo => mysql.escapeId(campo))
    .join(', ');
    
  const valores = Object.values(datos)
    .map(valor => mysql.escape(valor))
    .join(', ');
  
  const query = `INSERT INTO ${tablaEscapada} (${campos}) VALUES (${valores})`;
  
  db.query(query, (err, results) => {
    if (err) {
      console.error('Error al insertar registro:', err);
      return res.status(500).json({ 
        error: 'Error al insertar el registro' 
      });
    }
    
    res.json({ 
      message: 'Registro insertado exitosamente', 
      id: results.insertId
    });
  });
});

/**
 * Actualiza un registro específico en una tabla
 * 
 * @route PUT /tabla/:nombre/:id
 * @param {string} nombre - Nombre de la tabla donde actualizar
 * @param {string} id - ID del registro a actualizar
 * @query {string} [idColumna=id] - Nombre de la columna que actúa como identificador (por defecto "id")
 * @body {Object} datos - Datos a actualizar (pares campo-valor)
 * @returns {Object} - Mensaje de éxito y número de filas afectadas
 */
app.put('/tabla/:nombre/:id', (req, res) => {
  const nombreTabla = req.params.nombre;
  const id = req.params.id;
  const idColumna = req.query.idColumna || 'id';
  const tablaEscapada = mysql.escapeId(nombreTabla);
  const datos = req.body;

  const updates = Object.keys(datos).map(campo => 
    `${mysql.escapeId(campo)} = ${mysql.escape(datos[campo])}`
  ).join(', ');

  const query = `UPDATE ${tablaEscapada} SET ${updates} WHERE ${mysql.escapeId(idColumna)} = ${mysql.escape(id)}`;

  db.query(query, (err, results) => {
    if (err) {
      console.error('Error al actualizar:', err);
      return res.status(500).json({ error: 'Error al actualizar el registro' });
    }
    
    res.json({ 
      message: 'Registro actualizado exitosamente',
      affectedRows: results.affectedRows
    });
  });
});

/**
 * Elimina un registro específico de una tabla
 * 
 * @route DELETE /tabla/:nombre/:id
 * @param {string} nombre - Nombre de la tabla donde eliminar
 * @param {string} id - ID del registro a eliminar
 * @query {string} [idColumna=id] - Nombre de la columna que actúa como identificador (por defecto "id")
 * @returns {Object} - Mensaje de éxito y número de filas afectadas
 */
app.delete('/tabla/:nombre/:id', (req, res) => {
  const nombreTabla = req.params.nombre;
  const id = req.params.id;
  const idColumna = req.query.idColumna || 'id';
  const tablaEscapada = mysql.escapeId(nombreTabla);

  const deleteQuery = `DELETE FROM ${tablaEscapada} WHERE ${mysql.escapeId(idColumna)} = ${mysql.escape(id)}`;

  db.query(deleteQuery, (err, results) => {
    if (err) {
      console.error('Error al borrar:', err);
      return res.status(500).json({ error: 'Error al borrar el registro' });
    }
    
    res.json({ 
      message: 'Registro borrado exitosamente',
      affectedRows: results.affectedRows
    });
  });
});


/**************************************************************************
 *                          ENDPOINTS ESPECÍFICOS                        *
 * Estos endpoints están diseñados para realizar acciones concretas que  *
 * requieren mayor complejidad y lógica personalizada. Incluyen tareas   *
 * como autenticación de usuarios, generación de tokens, y otras         *
 * operaciones específicas que no se pueden generalizar fácilmente.      *
 *                                                                       *
 * NOTA: Asegúrate de manejar adecuadamente la validación de datos y la  *
 * seguridad en estos endpoints, ya que suelen involucrar información    *
 * sensible o lógica crítica para la aplicación.                         *
 **************************************************************************/


/**
 * Autentica a un usuario mediante su correo electrónico y contraseña
 * 
 * @route POST /login
 * @body {string} email - Correo electrónico del usuario
 * @body {string} contrasena - Contraseña del usuario
 * @returns {Object} - Mensaje de éxito, token JWT si la autenticación es correcta
 * 
 * NOTA: Este endpoint valida las credenciales del usuario y genera un 
 * token JWT para sesiones autenticadas. Asegúrate de proteger el secreto 
 * del token y manejar adecuadamente los errores para evitar fugas de 
 * información sensible.
 */
app.post('/login', (req, res) => {
  const { email, contrasena } = req.body;

  // Modificar la consulta para seleccionar específicamente la contraseña
  const query = 'SELECT Contrasena FROM Clientes WHERE Email = ?';
  
  db.query(query, [email], async (err, results) => {
    if (err) {
      console.error('Error al buscar usuario:', err);
      return res.status(500).json({ 
        success: false, 
        message: 'Error del servidor' 
      });
    }

    if (results.length === 0) {
      return res.status(401).json({ 
        success: false, 
        message: 'Usuario no encontrado' 
      });
    }

    const hashAlmacenado = results[0].Contrasena;
    
    try {
      // Comparar la contraseña proporcionada con el hash almacenado
      const contrasenaValida = await bcrypt.compare(contrasena, hashAlmacenado);
      
      if (!contrasenaValida) {
        return res.status(401).json({ 
          success: false, 
          message: 'Contraseña incorrecta' 
        });
      }

      // Generar token JWT (opcional pero recomendado)
      const token = jwt.sign(
        { email: email },
        'tu_secreto_jwt',
        { expiresIn: '1h' }
      );

      res.json({ 
        success: true, 
        message: 'Inicio de sesión exitoso',
        token: token 
      });

    } catch (error) {
      console.error('Error al comparar contraseñas:', error);
      res.status(500).json({ 
        success: false, 
        message: 'Error de autenticación' 
      });
    }
  });
});

/**
 * Añade un nuevo producto a la base de datos con una imagen
 * 
 * @route POST /productos
 * @body {string} Nombre - Nombre del producto
 * @body {string} Descripcion - Descripción del producto
 * @body {decimal} PrecioProducto - Precio del producto
 * @body {int} StockActual - Stock actual del producto
 * @body {int} StockMinimo - Stock mínimo del producto
 * @body {string} Tipo - Tipo del producto (Conservas, Bebidas, etc.)
 * @body {int} ID_Proveedor - ID del proveedor
 * @file {File} ImagenProducto - Imagen del producto
 * @returns {Object} - Mensaje de éxito y datos del producto añadido
 */
app.post('/productos', upload.single('ImagenProducto'), (req, res) => {
  const { Nombre, Descripcion, PrecioProducto, StockActual, StockMinimo, Tipo, ID_Proveedor } = req.body;
  const imagenPath = req.file ? `imagenes/${req.file.filename}` : null;

  if (!Nombre || !PrecioProducto || !StockActual || !StockMinimo || !Tipo || !ID_Proveedor) {
    return res.status(400).json({ error: 'Todos los campos son obligatorios' });
  }

  const query = `
    INSERT INTO Productos (Nombre, Descripcion, PrecioProducto, StockActual, StockMinimo, Tipo, ImagenProducto, ID_Proveedor)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
  `;

  const values = [Nombre, Descripcion, PrecioProducto, StockActual, StockMinimo, Tipo, imagenPath, ID_Proveedor];

  db.query(query, values, (err, results) => {
    if (err) {
      console.error('Error al insertar producto:', err);
      return res.status(500).json({ error: 'Error al insertar el producto' });
    }

    res.json({
      message: 'Producto añadido exitosamente',
      producto: {
        ID_Producto: results.insertId,
        Nombre,
        Descripcion,
        PrecioProducto,
        StockActual,
        StockMinimo,
        Tipo,
        ImagenProducto: imagenPath,
        ID_Proveedor
      }
    });
  });
});

// Iniciar el servidor HTTP
app.listen(port, () => {
  console.log(`Servidor API REST iniciado en http://localhost:${port}`);
});