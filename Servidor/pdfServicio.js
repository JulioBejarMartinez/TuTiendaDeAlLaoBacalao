import PDFDocument from 'pdfkit';
import fs from 'fs';
import path from 'path';

export function generarPDFPedido(venta, cliente, productos, outputPath) {
  return new Promise((resolve, reject) => {
    const doc = new PDFDocument({ 
      margin: 50,
      size: 'A4',
      info: {
        Title: `Factura ${venta.ID_Venta} - Tu Tienda de Al Lao`,
        Author: 'Tu Tienda de Al Lao',
        Subject: 'Factura de Compra',
        Keywords: 'factura, compra, tienda'
      }
    });

    const fecha = new Date(venta.FechaHora); 
    
    const stream = fs.createWriteStream(outputPath);
    doc.pipe(stream);

    // Paleta de colores profesional
    const colors = {
      primary: '#7be900',      // Verde vibrante
      primaryDark: '#5cb300',  // Verde más oscuro
      secondary: '#2c3e50',    // Azul gris oscuro
      accent: '#34495e',       // Gris azulado
      light: '#ecf0f1',        // Gris muy claro
      white: '#ffffff',
      text: '#2c3e50',
      textLight: '#7f8c8d'
    };

    // Configuración de página
    const pageWidth = doc.page.width;
    const pageHeight = doc.page.height;
    const margin = 50;

    // HEADER - Encabezado con diseño profesional
    function drawHeader() {
      // Fondo del header con gradiente simulado
      doc.rect(0, 0, pageWidth, 120)
         .fill(colors.primary);
      
      // Rectángulo decorativo
      doc.rect(0, 115, pageWidth, 5)
         .fill(colors.primaryDark);

      // Logo (si existe)
      const logoPath = path.resolve('./assets/TuTiendaDeAlLaoLogo.png');
      if (fs.existsSync(logoPath)) {
        try {
          doc.image(logoPath, margin, 25, { width: 120, height: 70 });
        } catch (error) {
          console.log('Error cargando logo:', error);
        }
      }

      // Información de la empresa
      doc.fillColor(colors.white)
         .fontSize(24)
         .font('Helvetica-Bold')
         .text('TU TIENDA DE AL LAO', pageWidth - 300, 30, { width: 250, align: 'right' });

      doc.fontSize(10)
         .font('Helvetica')
         .text('Calle Principal, 123', pageWidth - 300, 60, { width: 250, align: 'right' })
         .text('28000 Madrid, España', pageWidth - 300, 75, { width: 250, align: 'right' })
         .text('Tel: +34 91 123 45 67', pageWidth - 300, 90, { width: 250, align: 'right' });
    }

    // INFORMACIÓN DE FACTURA
    function drawInvoiceInfo() {
      doc.y = 150;
      
      // Título principal
      doc.fillColor(colors.secondary)
         .fontSize(28)
         .font('Helvetica-Bold')
         .text('FACTURA', margin, doc.y);

      // Número de factura con estilo
      doc.fontSize(14)
         .fillColor(colors.primary)
         .font('Helvetica-Bold')
         .text(`Nº ${String(venta.ID_Venta).padStart(6, '0')}`, margin, doc.y + 5);

      // Fecha con formato mejorado
      const fecha = new Date(venta.FechaHora);
      const fechaFormateada = fecha.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
      
      doc.fillColor(colors.textLight)
         .fontSize(12)
         .font('Helvetica')
         .text(`Fecha de emisión: ${fechaFormateada}`, margin, doc.y + 10);

      doc.moveDown(2);
    }

    // INFORMACIÓN DEL CLIENTE
    function drawClientInfo() {
      const clientY = doc.y;
      
      // Caja para datos del cliente
      doc.rect(margin, clientY, 240, 80)
         .fillAndStroke(colors.light, colors.primary);

      // Título sección cliente
      doc.fillColor(colors.secondary)
         .fontSize(14)
         .font('Helvetica-Bold')
         .text('DATOS DEL CLIENTE', margin + 15, clientY + 15);

      // Información del cliente
      doc.fontSize(11)
         .fillColor(colors.text)
         .font('Helvetica')
         .text(`${cliente.Nombre || 'Cliente'} ${cliente.Apellido || ''}`, margin + 15, clientY + 35)
         .text(`Email: ${cliente.Email || 'No especificado'}`, margin + 15, clientY + 50)
         .text(`Teléfono: ${cliente.Telefono || 'No especificado'}`, margin + 15, clientY + 65);

      // Información de la venta (lado derecho)
      const ventaX = pageWidth - 240 - margin;
      doc.rect(ventaX, clientY, 240, 80)
         .fillAndStroke(colors.white, colors.accent);

      doc.fillColor(colors.secondary)
         .fontSize(14)
         .font('Helvetica-Bold')
         .text('DETALLES DE LA VENTA', ventaX + 15, clientY + 15);

      doc.fontSize(11)
         .fillColor(colors.text)
         .font('Helvetica')
         .text(`ID de Venta: ${venta.ID_Venta}`, ventaX + 15, clientY + 35)
         .text(`Fecha: ${fecha.toLocaleDateString('es-ES')}`, ventaX + 15, clientY + 50)
         .text(`Hora: ${fecha.toLocaleTimeString('es-ES')}`, ventaX + 15, clientY + 65);

      doc.y = clientY + 100;
      doc.moveDown(1);
    }

    // TABLA DE PRODUCTOS PROFESIONAL
    function drawProductTable() {
      const tableY = doc.y;
      const tableHeaders = ['Producto', 'Cantidad', 'Precio Unit.', 'Subtotal'];
      const columnWidths = [260, 80, 90, 90];
      const columnX = [margin, margin + 260, margin + 340, margin + 430];
      
      // Encabezado de tabla con estilo
      doc.rect(margin, tableY, pageWidth - 2 * margin, 30)
         .fill(colors.primary);

      // Textos del encabezado
      doc.fillColor(colors.white)
         .fontSize(12)
         .font('Helvetica-Bold');
      
      tableHeaders.forEach((header, i) => {
        doc.text(header, columnX[i] + 10, tableY + 10, { 
          width: columnWidths[i] - 20, 
          align: i === 0 ? 'left' : 'center' 
        });
      });

      let currentY = tableY + 30;
      let rowIndex = 0;

      // Filas de productos con colores alternados
      productos.forEach(producto => {
        const rowColor = rowIndex % 2 === 0 ? colors.white : colors.light;
        
        // Fondo de fila
        doc.rect(margin, currentY, pageWidth - 2 * margin, 35)
           .fill(rowColor)
           .stroke(colors.textLight);

        // Contenido de la fila
        doc.fillColor(colors.text)
           .fontSize(10)
           .font('Helvetica');

        // Nombre del producto (con descripción si es muy largo)
        const nombreProducto = producto.Nombre.length > 30 
          ? producto.Nombre.substring(0, 27) + '...' 
          : producto.Nombre;
        
        doc.text(nombreProducto, columnX[0] + 10, currentY + 12, { 
          width: columnWidths[0] - 20 
        });

        // Cantidad
        doc.font('Helvetica-Bold')
           .text(producto.Cantidad.toString(), columnX[1] + 10, currentY + 12, { 
             width: columnWidths[1] - 20, 
             align: 'center' 
           });

        // Precio unitario
        doc.font('Helvetica')
           .text(`${producto.PrecioProducto.toFixed(2)} €`, columnX[2] + 10, currentY + 12, { 
             width: columnWidths[2] - 20, 
             align: 'center' 
           });

        // Subtotal
        doc.font('Helvetica-Bold')
           .fillColor(colors.secondary)
           .text(`${(producto.PrecioProducto * producto.Cantidad).toFixed(2)} €`, 
                 columnX[3] + 10, currentY + 12, { 
                   width: columnWidths[3] - 20, 
                   align: 'center' 
                 });

        currentY += 35;
        rowIndex++;
      });

      doc.y = currentY + 10;
    }

    // RESUMEN FINANCIERO
    function drawFinancialSummary() {
      const summaryY = doc.y;
      const summaryX = pageWidth - 250 - margin;
      
      // Calcular subtotal sin IVA
      const subtotal = productos.reduce((sum, prod) => 
        sum + (prod.PrecioProducto * prod.Cantidad), 0);
      const iva = subtotal * 0.21; // IVA 21%
      const total = venta.Total;

      // Caja del resumen
      doc.rect(summaryX, summaryY, 250, 120)
         .fillAndStroke(colors.white, colors.accent);

      // Título del resumen
      doc.fillColor(colors.secondary)
         .fontSize(14)
         .font('Helvetica-Bold')
         .text('RESUMEN', summaryX + 15, summaryY + 15);

      // Líneas del resumen
      const lineY = summaryY + 40;
      const lineSpacing = 18;

      // Subtotal
      doc.fontSize(11)
         .fillColor(colors.text)
         .font('Helvetica')
         .text('Subtotal:', summaryX + 15, lineY)
         .text(`${subtotal.toFixed(2)} €`, summaryX + 150, lineY, { align: 'right', width: 85 });

      // IVA
      doc.text('IVA (21%):', summaryX + 15, lineY + lineSpacing)
         .text(`${iva.toFixed(2)} €`, summaryX + 150, lineY + lineSpacing, { align: 'right', width: 85 });

      // Línea separadora
      doc.moveTo(summaryX + 15, lineY + lineSpacing * 2 - 5)
         .lineTo(summaryX + 235, lineY + lineSpacing * 2 - 5)
         .strokeColor(colors.primary)
         .lineWidth(2)
         .stroke();

      // Total final
      doc.fontSize(14)
         .fillColor(colors.primary)
         .font('Helvetica-Bold')
         .text('TOTAL:', summaryX + 15, lineY + lineSpacing * 2 + 5)
         .text(`${total.toFixed(2)} €`, summaryX + 150, lineY + lineSpacing * 2 + 5, { 
           align: 'right', 
           width: 85 
         });

      doc.y = summaryY + 140;
    }

    // FOOTER PROFESIONAL
    function drawFooter() {
      const footerY = pageHeight - 120;
      
      // Línea decorativa
      doc.moveTo(margin, footerY)
         .lineTo(pageWidth - margin, footerY)
         .strokeColor(colors.primary)
         .lineWidth(3)
         .stroke();

      // Mensaje de agradecimiento
      doc.y = footerY + 20;
      doc.fillColor(colors.primary)
         .fontSize(16)
         .font('Helvetica-BoldOblique')
         .text('¡Gracias por confiar en Tu Tienda de Al Lao!', { align: 'center' });

      // Información adicional
      doc.fillColor(colors.textLight)
         .fontSize(9)
         .font('Helvetica')
         .text('Esta factura ha sido generada electrónicamente', { align: 'center' })
         .moveDown(0.5)
         .text('Para cualquier consulta, contacte con nosotros en info@tutiendadeallao.com', { align: 'center' });

      // Información legal en el pie
      doc.y = pageHeight - 30;
      doc.fontSize(8)
         .fillColor(colors.textLight)
         .text(`Página 1 de 1 | Generado el ${new Date().toLocaleString('es-ES')}`, 
               margin, doc.y, { align: 'left' })
         .text('Tu Tienda de Al Lao - CIF: B12345678', 
               pageWidth - margin - 200, doc.y, { align: 'right', width: 200 });
    }

    // GENERAR EL PDF
    try {
      drawHeader();
      drawInvoiceInfo();
      drawClientInfo();
      drawProductTable();
      drawFinancialSummary();
      drawFooter();

      doc.end();

      stream.on('finish', () => {
        console.log(`PDF generado exitosamente: ${outputPath}`);
        resolve(outputPath);
      });
      
      stream.on('error', (error) => {
        console.error('Error generando PDF:', error);
        reject(error);
      });

    } catch (error) {
      console.error('Error en la generación del PDF:', error);
      reject(error);
    }
  });
}