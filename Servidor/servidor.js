/**
 * API REST con Express y MySQL
 * Proporciona endpoints para manipular tablas de base de datos
 */
import dotenv from 'dotenv';
dotenv.config({ path: './Servidor/.env' }); // Cargar las variables de entorno
import { enviarEmailBienvenida } from './emailServicio.js';
import { enviarEmail } from './emailServicio.js';
import { generarPDFPedido } from './pdfServicio.js';

import fs from 'fs';
import express from 'express';
import mysql from 'mysql';
import cors from 'cors';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import axios from 'axios';
import http from 'http';
import { Server } from 'socket.io';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path'
import multer from 'multer';
import { initializeApp } from "firebase/app";
import { getAuth } from 'firebase-admin/auth';
import admin from 'firebase-admin';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

if (!admin.apps.length) {
  try {
    if (!process.env.FIREBASE_PRIVATE_KEY) {
      throw new Error('FIREBASE_PRIVATE_KEY environment variable is missing');
    }
    
    const privateKey = process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n');
    
    admin.initializeApp({
      credential: admin.credential.cert({
        type: process.env.FIREBASE_TYPE || 'service_account',
        project_id: process.env.FIREBASE_PROJECT_ID,
        private_key_id: process.env.FIREBASE_PRIVATE_KEY_ID,
        private_key: privateKey,
        client_email: process.env.FIREBASE_CLIENT_EMAIL,
        client_id: process.env.FIREBASE_CLIENT_ID,
        auth_uri: process.env.FIREBASE_AUTH_URI,
        token_uri: process.env.FIREBASE_TOKEN_URI,
        auth_provider_x509_cert_url: process.env.FIREBASE_AUTH_PROVIDER_CERT_URL,
        client_x509_cert_url: process.env.FIREBASE_CLIENT_CERT_URL,
        universe_domain: process.env.FIREBASE_UNIVERSE_DOMAIN || 'googleapis.com',
      }),
    });
    console.log('Firebase Admin SDK initialized successfully');
  } catch (error) {
    console.error('Failed to initialize Firebase Admin SDK:', error.message);
  }
}

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

/**
 * Genera una contraseña aleatoria
 * @returns {string} - Contraseña aleatoria
 */
function generarContrasenaAleatoria() {
  const caracteres = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()';
  const longitud = 12;
  let contrasena = '';
  for (let i = 0; i < longitud; i++) {
    contrasena += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
  }
  return contrasena;
}

// Middleware
app.use('/imagenes', express.static(path.join(__dirname, 'imagenes')));
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

const firebaseConfig = {
  apiKey: "AIzaSyDo1LPV3QFL4jguJKYzjAlHPCvs7ezU-oo",
  authDomain: "tutiendadeallao.firebaseapp.com",
  projectId: "tutiendadeallao",
  storageBucket: "tutiendadeallao.firebasestorage.app",
  messagingSenderId: "690381263340",
  appId: "1:690381263340:web:12e50c7b667da11dc030c9",
  measurementId: "G-FX7BKD6LK7"
};

const firebaseApp = initializeApp(firebaseConfig);

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

app.post('/registro', async (req, res) => {
  const { nombre, apellido, email, telefono, contrasena } = req.body;
  // Validaciones aquí...

  // Insertar usuario en la base de datos
  const query = `INSERT INTO Clientes (Nombre, Apellido, Contrasena, Email, Telefono) VALUES (?, ?, ?, ?, ?)`;
  db.query(query, [nombre, apellido, contrasena, email, telefono], async (err, results) => {
    if (err) {
      return res.status(500).json({ error: 'Error al registrar el usuario' });
    }
    // Enviar email de bienvenida
    try {
      await enviarEmailBienvenida(email, nombre);
    } catch (e) {
      console.error('No se pudo enviar el email de bienvenida:', e);
    }
    res.json({ message: 'Usuario registrado correctamente' });
  });
});

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
 * Autentica a un usuario mediante un token de Google
 * 
 * @route POST /auth/google
 * @body {string} token - Token de autenticación proporcionado por Google
 * @returns {Object} - Mensaje de éxito, token JWT si la autenticación es correcta
 * 
 * NOTA: Este endpoint verifica el token de Google, crea un nuevo usuario 
 * en la base de datos si no existe, y genera un token JWT para sesiones 
 * autenticadas. Asegúrate de manejar adecuadamente los errores y proteger 
 * el secreto del token JWT.
 */
app.post('/auth/google', async (req, res) => {
  const { token } = req.body;

  try {
    // Verificar el token de Google
    const decodedToken = await admin.auth().verifyIdToken(token);
    const { email, name } = decodedToken;

    // Verificar si el usuario ya existe en la base de datos
    const query = 'SELECT * FROM Clientes WHERE Email = ?';
    db.query(query, [email], async (err, results) => {
      if (err) {
        console.error('Error al buscar usuario:', err);
        return res.status(500).json({ error: 'Error del servidor' });
      }

      if (results.length === 0) {
        // Si el usuario no existe, generarle una contraseña aleatoria
        const contrasenaAleatoria = generarContrasenaAleatoria();
        const hashContrasena = await bcrypt.hash(contrasenaAleatoria, 10);

        // Insertar el nuevo usuario en la base de datos
        const insertQuery = `
          INSERT INTO Clientes (Nombre, Email, Contrasena)
          VALUES (?, ?, ?)
        `;
        db.query(insertQuery, [name, email, hashContrasena], (err) => {
          if (err) {
            console.error('Error al crear usuario:', err);
            return res.status(500).json({ error: 'Error al crear usuario' });
          }

          console.log(`Usuario creado con contraseña aleatoria: ${contrasenaAleatoria}`);
        });
      }

      // Generar un token JWT para la sesión
      const jwtToken = jwt.sign({ email }, 'tu_secreto_jwt', { expiresIn: '1h' });
      res.json({ success: true, token: jwtToken });
    });
  } catch (error) {
    console.error('Error al verificar token de Google:', error);
    res.status(401).json({ error: 'Token inválido' });
  }
});

/**
 * Obtiene los datos del usuario autenticado
 * 
 * @route GET /usuario
 * @header {string} Authorization - Token JWT
 * @returns {Object} - Datos del usuario sin contraseña
 */
app.get('/usuario', (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({ error: 'Token no proporcionado' });
  }

  try {
    const decoded = jwt.verify(token, 'tu_secreto_jwt');
    const query = 'SELECT ID_Cliente, Nombre, Email, Telefono, PuntosFidelidad FROM Clientes WHERE Email = ?';
    
    db.query(query, [decoded.email], (err, results) => {
      if (err || results.length === 0) {
        return res.status(404).json({ error: 'Usuario no encontrado' });
      }
      res.json(results[0]);
    });
  } catch (error) {
    res.status(401).json({ error: 'Token inválido o expirado' });
  }
});

/**
 * Actualiza la información del usuario autenticado
 * 
 * @route PUT /usuario
 * @header {string} Authorization - Token JWT
 * @body {string} [Nombre] - Nuevo nombre del usuario
 * @body {string} [Contrasena] - Nueva contraseña del usuario
 * @returns {Object} - Mensaje de éxito
 */
app.put('/usuario', async (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({ error: 'Token no proporcionado' });
  }

  try {
    const decoded = jwt.verify(token, 'tu_secreto_jwt');
    const { Nombre, Contrasena } = req.body;

    if (!Nombre && !Contrasena) {
      return res.status(400).json({ error: 'Se requiere al menos un campo para actualizar' });
    }

    const updates = [];
    const values = [];

    if (Nombre) {
      updates.push('Nombre = ?');
      values.push(Nombre);
    }

    if (Contrasena) {
      const hashContrasena = await bcrypt.hash(Contrasena, 10);
      updates.push('Contrasena = ?');
      values.push(hashContrasena);
    }

    values.push(decoded.email);

    const query = `UPDATE Clientes SET ${updates.join(', ')} WHERE Email = ?`;

    db.query(query, values, (err, results) => {
      if (err) {
        console.error('Error al actualizar usuario:', err);
        return res.status(500).json({ error: 'Error al actualizar la información del usuario' });
      }

      res.json({ message: 'Información actualizada exitosamente' });
    });
  } catch (error) {
    res.status(401).json({ error: 'Token inválido o expirado' });
  }
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

/**
 * Obtiene todos los productos de la base de datos
 * 
 * @route GET /productos
 * @returns {Array} - Lista de productos con todos sus datos
 * 
 * NOTA: Este endpoint devuelve todos los registros de la tabla `Productos`.
 * Útil para mostrar el catálogo completo de productos en la aplicación.
 * Asegúrate de manejar adecuadamente los errores en el cliente.
 */
app.get('/productos', (req, res) => {
  const query = 'SELECT * FROM Productos';

  db.query(query, (err, results) => {
    if (err) {
      console.error('Error al obtener productos:', err);
      return res.status(500).json({ error: 'Error al obtener los productos' });
    }

    res.json(results);
  });
});

/**
 * Obtiene los detalles de un producto específico según su ID
 * 
 * @route GET /productos/:id
 * @param {int} id - ID del producto
 * @returns {Object} - Detalles del producto
 * 
 * NOTA: Este endpoint devuelve todos los datos del producto solicitado.
 * Asegúrate de manejar adecuadamente los errores en el cliente.
 */
app.get('/productos/:id', (req, res) => {
  const { id } = req.params;

  // Excluir StockMinimo e ID_Proveedor de la consulta
  const query = `
    SELECT ID_Producto, Nombre, Descripcion, PrecioProducto, StockActual, Tipo, ImagenProducto
    FROM Productos
    WHERE ID_Producto = ?
  `;

  db.query(query, [id], (err, results) => {
    if (err) {
      console.error('Error al obtener el producto:', err);
      return res.status(500).json({ error: 'Error al obtener el producto' });
    }

    if (results.length === 0) {
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    res.json(results[0]); // Devolver el primer resultado (único producto)
  });
});

/**
 * Obtiene la imagen de un producto según su ID
 * 
 * @route GET /productos/imagen/:id
 * @param {int} id - ID del producto
 * @returns {File} - Archivo de imagen del producto
 * 
 * NOTA: Este endpoint busca la ruta de la imagen en la base de datos y 
 * sirve el archivo correspondiente. Si la imagen no existe o no se encuentra, 
 * devuelve un error 404. Asegúrate de que las rutas de las imágenes sean válidas 
 * y que la carpeta de imágenes sea accesible desde el servidor.
 */
app.get('/productos/imagen/:id', (req, res) => {
  const { id } = req.params;

  const query = 'SELECT ImagenProducto FROM Productos WHERE ID_Producto = ?';

  db.query(query, [id], (err, results) => {
    if (err) {
      console.error('Error al obtener la imagen del producto:', err);
      return res.status(500).json({ error: 'Error al obtener la imagen del producto' });
    }

    if (results.length === 0) {
      return res.status(404).json({ error: 'Producto no encontrado' });
    }

    const imagenPath = results[0].ImagenProducto;

    if (!imagenPath) {
      return res.status(404).json({ error: 'Imagen no encontrada para este producto' });
    }

    const fullPath = path.join(__dirname, '..', imagenPath); // Ajuste para la ruta relativa
    res.sendFile(fullPath, (err) => {
      if (err) {
        console.error('Error al enviar la imagen:', err);
        res.status(500).json({ error: 'Error al cargar la imagen' });
      }
    });
  });
});

/**
 * Registra una compra (venta) de productos por parte de un cliente
 * 
 * @route POST /realizar-pedido
 * @body {int} clienteId - ID del cliente
 * @body {Array} productos - [{ ID_Producto, cantidad }]
 * @body {decimal} total - Total de la compra
 * @returns {Object} - Mensaje de éxito y datos de la venta
 */
app.post('/realizar-pedido', (req, res) => {
  const { clienteId, productos, total } = req.body;
  const empleadoId = 1;

  if (!clienteId || !Array.isArray(productos) || productos.length === 0 || !total) {
    return res.status(400).json({ error: 'Datos incompletos para registrar la venta' });
  }

  const fechaHora = new Date();

  const ventaQuery = `
    INSERT INTO Ventas (FechaHora, ID_Empleado, ID_Cliente, Total)
    VALUES (?, ?, ?, ?)
  `;
  db.query(ventaQuery, [fechaHora, empleadoId, clienteId, total], (err, ventaResult) => {
    if (err) {
      console.error('Error al registrar la venta:', err);
      return res.status(500).json({ error: 'Error al registrar la venta' });
    }
    const ventaId = ventaResult.insertId;

    const detalleQueries = productos.map(prod => {
      return new Promise((resolve, reject) => {
        const detalleQuery = `
          INSERT INTO DetallesVenta (ID_Venta, ID_Producto, Cantidad)
          VALUES (?, ?, ?)
        `;
        db.query(detalleQuery, [ventaId, prod.ID_Producto, prod.cantidad], (err) => {
          if (err) return reject(err);

          const stockQuery = `
            UPDATE Productos SET StockActual = StockActual - ?
            WHERE ID_Producto = ? AND StockActual >= ?
          `;
          db.query(stockQuery, [prod.cantidad, prod.ID_Producto, prod.cantidad], (err, stockResult) => {
            if (err) return reject(err);
            if (stockResult.affectedRows === 0) {
              return reject(new Error(`Stock insuficiente para el producto ID ${prod.ID_Producto}`));
            }
            resolve();
          });
        });
      });
    });

    Promise.all(detalleQueries)
      .then(() => {
        // Obtener datos de la venta, cliente y productos para el PDF y email
        const ventaDatosQuery = 'SELECT * FROM Ventas WHERE ID_Venta = ?';
        const clienteDatosQuery = 'SELECT * FROM Clientes WHERE ID_Cliente = ?';
        const productosDatosQuery = `
          SELECT p.Nombre, p.PrecioProducto, dv.Cantidad
          FROM DetallesVenta dv
          JOIN Productos p ON dv.ID_Producto = p.ID_Producto
          WHERE dv.ID_Venta = ?
        `;

        db.query(ventaDatosQuery, [ventaId], (err, ventaRows) => {
          if (err || ventaRows.length === 0) return res.json({ message: 'Pedido registrado, pero error al obtener datos de venta.' });

          db.query(clienteDatosQuery, [clienteId], (err, clienteRows) => {
            if (err || clienteRows.length === 0) return res.json({ message: 'Pedido registrado, pero error al obtener datos de cliente.' });

            db.query(productosDatosQuery, [ventaId], async (err, productosRows) => {
              if (err) return res.json({ message: 'Pedido registrado, pero error al obtener productos.' });

              // Generar PDF
              const outputPath = `./facturas/factura_${ventaId}.pdf`;
              try {
                // Asegúrate de que la carpeta 'facturas' existe
                if (!fs.existsSync('./facturas')) fs.mkdirSync('./facturas');

                await generarPDFPedido(ventaRows[0], clienteRows[0], productosRows, outputPath);

                // Enviar email con el PDF adjunto
                const email = clienteRows[0].Email;
                const nombre = clienteRows[0].Nombre;
                const subject = 'Factura de tu compra en Tu Tienda de Al Lao';
                const text = `Hola ${nombre},\nAdjuntamos la factura de tu compra. ¡Gracias por confiar en nosotros!`;
                const html = `<p>Hola <strong>${nombre}</strong>,<br>Adjuntamos la factura de tu compra.<br>¡Gracias por confiar en nosotros!</p>`;

                await enviarEmail(email, subject, text, html, [
                  {
                    filename: `factura_${ventaId}.pdf`,
                    path: outputPath
                  }
                ]);
              } catch (e) {
                console.error('Error al generar o enviar la factura:', e);
                // No cortar la respuesta al cliente si el pedido fue registrado
              }

              res.json({
                message: 'Pedido registrado correctamente y factura enviada por email',
                ventaId,
                productos
              });
            });
          });
        });
      })
      .catch(error => {
        console.error('Error en detalles de venta:', error);
        res.status(500).json({ error: error.message || 'Error al registrar detalles de la venta' });
      });
  });
});


// Iniciar el servidor HTTP
app.listen(port, () => {
  console.log(`Servidor API REST iniciado en http://localhost:${port}`);
});