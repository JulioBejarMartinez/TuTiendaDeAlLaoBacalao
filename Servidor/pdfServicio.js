import PDFDocument from 'pdfkit';
import fs from 'fs';

export function generarPDFPedido(venta, cliente, productos, outputPath) {
  return new Promise((resolve, reject) => {
    const doc = new PDFDocument({ margin: 40 });
    const stream = fs.createWriteStream(outputPath);
    doc.pipe(stream);

    doc.fontSize(18).text('Ticket de Venta', { align: 'center' });
    doc.moveDown();
    doc.fontSize(12).text(`Venta Nº: ${venta.ID_Venta}`);
    doc.text(`Fecha: ${new Date(venta.FechaHora).toLocaleString()}`);
    doc.text(`Cliente: ${cliente.Nombre || ''} (${cliente.Email || ''})`);
    doc.moveDown();

    doc.text('Productos:', { underline: true });
    productos.forEach(prod => {
      doc.text(`${prod.Nombre} x${prod.Cantidad} - ${prod.PrecioProducto.toFixed(2)}€`);
    });
    doc.moveDown();
    doc.text(`Total: ${venta.Total.toFixed(2)}€`, { bold: true });

    doc.end();

    stream.on('finish', () => resolve(outputPath));
    stream.on('error', reject);
  });
}