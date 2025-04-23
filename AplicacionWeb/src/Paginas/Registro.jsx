import React, { useState } from 'react';
import axios from 'axios';
import bcrypt from 'bcryptjs';

const Registro = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    contrasena: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // Generar hash de la contraseña
      const hashedPassword = await bcrypt.hash(formData.contrasena, 10);

      // Crear un nuevo objeto con la contraseña hasheada
      const dataToSend = {
        ...formData,
        contrasena: hashedPassword,
      };

      // Enviar los datos al servidor
      const response = await axios.post('http://localhost:3000/tabla/Clientes', dataToSend);
      alert('Registro exitoso');
    } catch (error) {
      console.error('Error al registrar:', error);
      alert('Hubo un error al registrar el cliente');
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow-lg">
            <div className="card-header text-center bg-primary text-white">
              <h3><i className="bi bi-person-plus-fill"></i> Registro de Clientes</h3>
            </div>
            <div className="card-body">
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="nombre" className="form-label">Nombre</label>
                  <input
                    type="text"
                    id="nombre"
                    name="nombre"
                    className="form-control"
                    placeholder="Introduce tu nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="apellido" className="form-label">Apellido</label>
                  <input
                    type="text"
                    id="apellido"
                    name="apellido"
                    className="form-control"
                    placeholder="Introduce tu apellido"
                    value={formData.apellido}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="email" className="form-label">Correo Electrónico</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    className="form-control"
                    placeholder="Introduce tu correo electrónico"
                    value={formData.email}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="telefono" className="form-label">Teléfono</label>
                  <input
                    type="tel"
                    id="telefono"
                    name="telefono"
                    className="form-control"
                    placeholder="Introduce tu teléfono"
                    value={formData.telefono}
                    onChange={handleChange}
                  />
                </div>
                <div className="mb-3">
                  <label htmlFor="contrasena" className="form-label">Contraseña</label>
                  <input
                    type="password"
                    id="contrasena"
                    name="contrasena"
                    className="form-control"
                    placeholder="Introduce tu contraseña"
                    value={formData.contrasena}
                    onChange={handleChange}
                    required
                  />
                </div>
                <div className="d-grid">
                  <button type="submit" className="btn btn-primary">
                    <i className="bi bi-check-circle-fill"></i> Registrarse
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Registro;