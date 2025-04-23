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

// Configuración de la aplicación
const app = express();
const port = 3000;

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

app.post('/login', (req, res) => {
  const { email, contrasena } = req.body;

  const query = 'SELECT * FROM Clientes WHERE Email = ?';
  db.query(query, [email], async (err, results) => {
    if (err) {
      console.error('Error al buscar usuario:', err);
      return res.status(500).json({ success: false, message: 'Error del servidor' });
    }

    if (results.length === 0) {
      return res.status(401).json({ success: false, message: 'Usuario no encontrado' });
    }

    const usuario = results[0];
    const contrasenaValida = await bcrypt.compare(contrasena, usuario.Contrasena);

    if (!contrasenaValida) {
      return res.status(401).json({ success: false, message: 'Contraseña incorrecta' });
    }

    res.json({ success: true, message: 'Inicio de sesión exitoso' });
  });
});

// Iniciar el servidor HTTP
app.listen(port, () => {
  console.log(`Servidor API REST iniciado en http://localhost:${port}`);
});