import nodemailer from 'nodemailer';
import dotenv from 'dotenv';

dotenv.config({ path: './Servidor/.env' });

// Configuración del transporter de Nodemailer
const transporter = nodemailer.createTransport({
  host: process.env.EMAIL_HOST || 'smtp.gmail.com',
  port: parseInt(process.env.EMAIL_PORT || '587'),
  secure: process.env.EMAIL_SECURE === 'true',
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASSWORD
  }
});

// Función genérica para enviar emails
export const enviarEmail = async (to, subject, text, html) => {
  try {
    const mailOptions = {
      from: `"${process.env.EMAIL_SENDER_NAME || 'Tu Tienda de Al Lao'}" <${process.env.EMAIL_USER}>`,
      to,
      subject,
      text,
      html: html || text
    };
    const info = await transporter.sendMail(mailOptions);
    console.log('Email enviado:', info.messageId);
    return { success: true, messageId: info.messageId };
  } catch (error) {
    console.error('Error al enviar email:', error);
    throw error;
  }
};

// Email de bienvenida
export const enviarEmailBienvenida = async (email, nombre) => {
  const subject = '¡Bienvenido a Tu Tienda de Al Lao!';
  const text = `Hola ${nombre},\n\nGracias por registrarte en Tu Tienda de Al Lao.`;
  const html = `<h2>¡Bienvenido a Tu Tienda de Al Lao!</h2><p>Hola <strong>${nombre}</strong>,<br>Gracias por registrarte.</p>`;
  return enviarEmail(email, subject, text, html);
};