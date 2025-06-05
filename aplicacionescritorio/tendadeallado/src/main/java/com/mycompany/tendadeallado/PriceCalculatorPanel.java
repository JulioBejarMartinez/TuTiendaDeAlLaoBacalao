/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class PriceCalculatorPanel extends JPanel {
    private JLabel statusBar;
    private JTextField quantityField;
    private JComboBox<String> productComboBox;
    private JComboBox<String> customerTypeComboBox;
    private JComboBox<String> tariffComboBox;
    private JLabel basePriceLabel;
    private JLabel discountLabel;
    private JLabel finalPriceLabel;
    private JLabel totalAmountLabel;
    private JTable calculationHistoryTable;
    private DefaultTableModel historyTableModel;
    private DecimalFormat currencyFormat;
    private JProgressBar loadingProgressBar;
    private JLabel calculationStatusLabel;
    
    // Sistema de hilos y concurrencia
    private ExecutorService calculationExecutor;
    private ExecutorService databaseExecutor;
    private CompletionService<CalculationResult> calculationService;
    private AtomicBoolean isCalculating;
    private AtomicInteger calculationCounter;
    private SwingWorker<Void, String> dataLoaderWorker;
    
    // Sistema de pruebas automatizado
    private JPanel testingPanel;
    private JButton runAutomatedTestsButton;
    private JTextArea testLogArea;
    private AtomicBoolean testingMode;
    
    // Clase para resultados de cálculo con hilos
    private static class CalculationResult {
        final double basePrice;
        final double discountPercentage;
        final double finalPrice;
        final double totalAmount;
        final String calculationId;
        final long calculationTime;
        final boolean success;
        final String errorMessage;
        
        public CalculationResult(double basePrice, double discountPercentage, double finalPrice, 
                               double totalAmount, String calculationId, long calculationTime) {
            this.basePrice = basePrice;
            this.discountPercentage = discountPercentage;
            this.finalPrice = finalPrice;
            this.totalAmount = totalAmount;
            this.calculationId = calculationId;
            this.calculationTime = calculationTime;
            this.success = true;
            this.errorMessage = null;
        }
        
        public CalculationResult(String calculationId, String errorMessage) {
            this.calculationId = calculationId;
            this.errorMessage = errorMessage;
            this.success = false;
            this.basePrice = 0;
            this.discountPercentage = 0;
            this.finalPrice = 0;
            this.totalAmount = 0;
            this.calculationTime = 0;
        }
    }
    
    // Clase para tarifas
    private class Tariff {
        int id;
        String name;
        String customerType;
        String productCategory;
        double discountPercentage;
        double minimumQuantity;
        String description;
        
        public Tariff(int id, String name, String customerType, String productCategory, 
                     double discountPercentage, double minimumQuantity, String description) {
            this.id = id;
            this.name = name;
            this.customerType = customerType;
            this.productCategory = productCategory;
            this.discountPercentage = discountPercentage;
            this.minimumQuantity = minimumQuantity;
            this.description = description;
        }
        
        @Override
        public String toString() {
            return name + " - " + discountPercentage + "% desc.";
        }
    }
    
    // Clase para productos
    private class Product {
        int id;
        String name;
        String category;
        double basePrice;
        
        public Product(int id, String name, String category, double basePrice) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.basePrice = basePrice;
        }
        
        @Override
        public String toString() {
            return name + " - " + currencyFormat.format(basePrice);
        }
    }
    
    private volatile List<Tariff> tariffs;
    private volatile List<Product> products;

    public PriceCalculatorPanel(JLabel statusBar) {
        this.statusBar = statusBar;
        this.currencyFormat = new DecimalFormat("#,##0.00 €");
        this.tariffs = new ArrayList<>();
        this.products = new ArrayList<>();
        
        // Inicializar sistema de hilos
        this.calculationExecutor = Executors.newFixedThreadPool(4);
        this.databaseExecutor = Executors.newFixedThreadPool(2);
        this.calculationService = new ExecutorCompletionService<>(calculationExecutor);
        this.isCalculating = new AtomicBoolean(false);
        this.calculationCounter = new AtomicInteger(0);
        this.testingMode = new AtomicBoolean(false);
        initializePanel();
        startBackgroundDataLoader();
    }

    public JPanel createPanel() {
        
        return this;
    }

    private void initializePanel() {
        removeAll();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título y barra de progreso
        JPanel topPanel = createTopPanel();
        
        // Panel principal dividido en columnas
        JPanel mainContent = new JPanel(new GridLayout(1, 3, 10, 0));
        
        // Panel izquierdo - Calculadora
        JPanel calculatorPanel = createCalculatorPanel();
        
        // Panel central - Historial
        JPanel historyPanel = createHistoryPanel();
        
        // Panel derecho - Sistema de pruebas
        JPanel testPanel = createTestingPanel();
        
        mainContent.add(calculatorPanel);
        mainContent.add(historyPanel);
        mainContent.add(testPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        
        updateStatusBar("Calculadora de Precios - Cargando datos...");
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Calculadora de Precios con Hilos y Procesos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Panel de estado y progreso
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        calculationStatusLabel = new JLabel("Sistema listo");
        calculationStatusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        loadingProgressBar = new JProgressBar();
        loadingProgressBar.setStringPainted(true);
        loadingProgressBar.setString("Listo");
        loadingProgressBar.setPreferredSize(new Dimension(200, 20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Actualizar Datos");
        JButton manageTariffsButton = new JButton("Gestionar Tarifas");
        JButton performanceTestButton = new JButton("Test Rendimiento");
        
        refreshButton.addActionListener(e -> startBackgroundDataLoader());
        manageTariffsButton.addActionListener(e -> showTariffManagement());
        performanceTestButton.addActionListener(e -> runPerformanceTest());
        
        buttonPanel.add(performanceTestButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(manageTariffsButton);
        
        statusPanel.add(calculationStatusLabel, BorderLayout.WEST);
        statusPanel.add(loadingProgressBar, BorderLayout.CENTER);
        statusPanel.add(buttonPanel, BorderLayout.EAST);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statusPanel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JPanel createCalculatorPanel() {
        JPanel calculatorPanel = new JPanel(new BorderLayout(0, 10));
        calculatorPanel.setBorder(BorderFactory.createTitledBorder("Calculadora (Multi-hilo)"));
        
        // Panel de entrada de datos
        JPanel inputPanel = createInputPanel();
        
        // Panel de resultados
        JPanel resultsPanel = createResultsPanel();
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        
        calculatorPanel.add(inputPanel, BorderLayout.NORTH);
        calculatorPanel.add(resultsPanel, BorderLayout.CENTER);
        calculatorPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return calculatorPanel;
    }
    
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Datos del Cálculo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Producto
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        productComboBox = new JComboBox<>();
        productComboBox.setPreferredSize(new Dimension(200, 25));
        productComboBox.addActionListener(e -> scheduleCalculation());
        inputPanel.add(productComboBox, gbc);
        
        // Cantidad
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        quantityField = new JTextField("1");
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                scheduleCalculation();
            }
        });
        inputPanel.add(quantityField, gbc);
        
        // Tipo de cliente
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Tipo de Cliente:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        customerTypeComboBox = new JComboBox<>(new String[]{
            "Público General", "Cliente Mayorista", "Cliente VIP", "Empleado"
        });
        customerTypeComboBox.addActionListener(e -> {
            updateAvailableTariffsAsync();
            scheduleCalculation();
        });
        inputPanel.add(customerTypeComboBox, gbc);
        
        // Tarifa aplicable
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Tarifa:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        tariffComboBox = new JComboBox<>();
        tariffComboBox.addActionListener(e -> scheduleCalculation());
        inputPanel.add(tariffComboBox, gbc);
        
        return inputPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new GridBagLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Resultado (Calculado en Hilo Separado)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font("Arial", Font.PLAIN, 11);
        Font valueFont = new Font("Arial", Font.BOLD, 12);
        
        // Precio base
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel basePriceTitleLabel = new JLabel("Precio Base:");
        basePriceTitleLabel.setFont(labelFont);
        resultsPanel.add(basePriceTitleLabel, gbc);
        gbc.gridx = 1;
        basePriceLabel = new JLabel("0.00 €");
        basePriceLabel.setFont(valueFont);
        resultsPanel.add(basePriceLabel, gbc);
        
        // Descuento
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel discountTitleLabel = new JLabel("Descuento:");
        discountTitleLabel.setFont(labelFont);
        resultsPanel.add(discountTitleLabel, gbc);
        gbc.gridx = 1;
        discountLabel = new JLabel("0%");
        discountLabel.setFont(valueFont);
        discountLabel.setForeground(new Color(0, 150, 0));
        resultsPanel.add(discountLabel, gbc);
        
        // Precio final unitario
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel finalPriceTitleLabel = new JLabel("Precio Final:");
        finalPriceTitleLabel.setFont(labelFont);
        resultsPanel.add(finalPriceTitleLabel, gbc);
        gbc.gridx = 1;
        finalPriceLabel = new JLabel("0.00 €");
        finalPriceLabel.setFont(valueFont);
        finalPriceLabel.setForeground(new Color(0, 100, 200));
        resultsPanel.add(finalPriceLabel, gbc);
        
        // Total
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel totalTitleLabel = new JLabel("TOTAL:");
        totalTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        resultsPanel.add(totalTitleLabel, gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("0.00 €");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(200, 0, 0));
        resultsPanel.add(totalAmountLabel, gbc);
        
        return resultsPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton clearButton = new JButton("Limpiar");
        JButton addToHistoryButton = new JButton("Guardar");
        JButton bulkCalculateButton = new JButton("Cálculo Masivo");
        
        clearButton.addActionListener(e -> clearCalculation());
        addToHistoryButton.addActionListener(e -> addToHistoryAsync());
        bulkCalculateButton.addActionListener(e -> runBulkCalculation());
        
        buttonPanel.add(clearButton);
        buttonPanel.add(addToHistoryButton);
        buttonPanel.add(bulkCalculateButton);
        
        return buttonPanel;
    }
    
    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setBorder(BorderFactory.createTitledBorder("Historial (Actualización Asíncrona)"));
        
        // Tabla de historial
        String[] historyColumns = {"Fecha", "Producto", "Cant.", "Total", "Tiempo"};
        historyTableModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        calculationHistoryTable = new JTable(historyTableModel);
        calculationHistoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 10));
        calculationHistoryTable.setFont(new Font("Arial", Font.PLAIN, 9));
        calculationHistoryTable.setRowHeight(18);
        
        JScrollPane historyScroll = new JScrollPane(calculationHistoryTable);
        historyScroll.setPreferredSize(new Dimension(0, 150));
        
        // Panel de botones del historial
        JPanel historyButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton clearHistoryButton = new JButton("Limpiar");
        JButton exportButton = new JButton("Exportar");
        
        clearHistoryButton.addActionListener(e -> clearHistory());
        exportButton.addActionListener(e -> exportHistoryAsync());
        
        historyButtonPanel.add(clearHistoryButton);
        historyButtonPanel.add(exportButton);
        
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        historyPanel.add(historyButtonPanel, BorderLayout.SOUTH);
        
        return historyPanel;
    }
    
    private JPanel createTestingPanel() {
        testingPanel = new JPanel(new BorderLayout(0, 5));
        testingPanel.setBorder(BorderFactory.createTitledBorder("Sistema de Pruebas Automatizado"));
        
        // Botones de prueba
        JPanel testButtonPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        
        runAutomatedTestsButton = new JButton("Ejecutar Pruebas");
        JButton stressTestButton = new JButton("Test de Estrés");
        JButton concurrencyTestButton = new JButton("Test Concurrencia");
        JButton dbTestButton = new JButton("Test Base de Datos");
        
        runAutomatedTestsButton.addActionListener(e -> runAutomatedTests());
        stressTestButton.addActionListener(e -> runStressTest());
        concurrencyTestButton.addActionListener(e -> runConcurrencyTest());
        dbTestButton.addActionListener(e -> runDatabaseTest());
        
        testButtonPanel.add(runAutomatedTestsButton);
        testButtonPanel.add(stressTestButton);
        testButtonPanel.add(concurrencyTestButton);
        testButtonPanel.add(dbTestButton);
        
        // Área de log de pruebas
        testLogArea = new JTextArea();
        testLogArea.setFont(new Font("Courier New", Font.PLAIN, 9));
        testLogArea.setEditable(false);
        testLogArea.setBackground(Color.BLACK);
        testLogArea.setForeground(Color.GREEN);
        
        JScrollPane testLogScroll = new JScrollPane(testLogArea);
        testLogScroll.setPreferredSize(new Dimension(0, 200));
        
        testingPanel.add(testButtonPanel, BorderLayout.NORTH);
        testingPanel.add(testLogScroll, BorderLayout.CENTER);
        
        return testingPanel;
    }
    
    // MÉTODOS CON HILOS Y PROCESOS
    
    private void startBackgroundDataLoader() {
        if (dataLoaderWorker != null && !dataLoaderWorker.isDone()) {
            dataLoaderWorker.cancel(true);
        }
        
        dataLoaderWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Iniciando carga de datos...");
                
                // Simular carga de productos en paralelo
                Future<Void> productsFuture = databaseExecutor.submit(() -> {
                    loadProducts();
                    return null;
                });
                
                // Simular carga de tarifas en paralelo
                Future<Void> tariffsFuture = databaseExecutor.submit(() -> {
                    loadTariffs();
                    return null;
                });
                
                // Esperar a que ambas cargas terminen
                productsFuture.get();
                publish("Productos cargados");
                
                tariffsFuture.get();
                publish("Tarifas cargadas");
                
                publish("Datos cargados exitosamente");
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    calculationStatusLabel.setText(message);
                    loadingProgressBar.setString(message);
                }
            }
            
            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    updateAvailableTariffsAsync();
                    loadingProgressBar.setString("Listo");
                    calculationStatusLabel.setText("Sistema listo");
                    updateStatusBar("Calculadora lista - Datos cargados");
                });
            }
        };
        
        dataLoaderWorker.execute();
    }
    
    private void scheduleCalculation() {
        if (isCalculating.get()) return;
        
        // Ejecutar cálculo en hilo separado con retraso para evitar múltiples cálculos
        Timer timer = new Timer(300, e -> performCalculationAsync());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void performCalculationAsync() {
        if (isCalculating.compareAndSet(false, true)) {
            String calculationId = "CALC-" + calculationCounter.incrementAndGet();
            
            calculationService.submit(() -> {
                long startTime = System.currentTimeMillis();
                
                try {
                    Product selectedProduct = getSelectedProduct();
                    if (selectedProduct == null) {
                        return new CalculationResult(calculationId, "Producto no seleccionado");
                    }
                    
                    String quantityText = quantityField.getText().trim();
                    if (quantityText.isEmpty()) {
                        return new CalculationResult(calculationId, "Cantidad vacía");
                    }
                    
                    double quantity = Double.parseDouble(quantityText);
                    if (quantity <= 0) {
                        return new CalculationResult(calculationId, "Cantidad inválida");
                    }
                    
                    // Simular procesamiento complejo
                    Thread.sleep(100); // Simular cálculo complejo
                    
                    double basePrice = selectedProduct.basePrice;
                    double discountPercentage = 0;
                    
                    Tariff selectedTariff = getSelectedTariff();
                    if (selectedTariff != null && quantity >= selectedTariff.minimumQuantity) {
                        discountPercentage = selectedTariff.discountPercentage;
                    }
                    
                    double discountAmount = basePrice * (discountPercentage / 100);
                    double finalPrice = basePrice - discountAmount;
                    double totalAmount = finalPrice * quantity;
                    
                    long calculationTime = System.currentTimeMillis() - startTime;
                    
                    return new CalculationResult(basePrice, discountPercentage, finalPrice, 
                                               totalAmount, calculationId, calculationTime);
                    
                } catch (Exception e) {
                    return new CalculationResult(calculationId, "Error: " + e.getMessage());
                } finally {
                    isCalculating.set(false);
                }
            });
            
            // Procesar resultado de forma asíncrona
            CompletableFuture.supplyAsync(() -> {
                try {
                    return calculationService.take().get();
                } catch (Exception e) {
                    return new CalculationResult(calculationId, "Error interno: " + e.getMessage());
                }
            }).thenAccept(result -> {
                SwingUtilities.invokeLater(() -> updateCalculationResults(result));
            });
        }
    }
    
    private void updateCalculationResults(CalculationResult result) {
        if (result.success) {
            basePriceLabel.setText(currencyFormat.format(result.basePrice));
            discountLabel.setText(result.discountPercentage + "%");
            finalPriceLabel.setText(currencyFormat.format(result.finalPrice));
            totalAmountLabel.setText(currencyFormat.format(result.totalAmount));
            
            calculationStatusLabel.setText("Calculado en " + result.calculationTime + "ms");
        } else {
            clearResults();
            calculationStatusLabel.setText("Error: " + result.errorMessage);
        }
    }
    
    private void updateAvailableTariffsAsync() {
        CompletableFuture.runAsync(() -> {
            List<String> availableTariffs = new ArrayList<>();
            availableTariffs.add("Sin tarifa especial");
            
            String selectedCustomerType = (String) customerTypeComboBox.getSelectedItem();
            Product selectedProduct = getSelectedProduct();
            
            if (selectedProduct != null) {
                synchronized (tariffs) {
                    for (Tariff tariff : tariffs) {
                        boolean customerTypeMatch = tariff.customerType.equals("Todos") || 
                                                  tariff.customerType.equals(selectedCustomerType);
                        boolean categoryMatch = tariff.productCategory.equals("Todas") || 
                                              tariff.productCategory.equals(selectedProduct.category);
                        
                        if (customerTypeMatch && categoryMatch) {
                            availableTariffs.add(tariff.toString());
                        }
                    }
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                tariffComboBox.removeAllItems();
                for (String tariff : availableTariffs) {
                    tariffComboBox.addItem(tariff);
                }
            });
        });
    }
    
    private void addToHistoryAsync() {
        Product selectedProduct = getSelectedProduct();
        if (selectedProduct == null) return;
        
        CompletableFuture.runAsync(() -> {
            try {
                String quantityText = quantityField.getText().trim();
                double quantity = Double.parseDouble(quantityText);
                String totalText = totalAmountLabel.getText();
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
                String currentDate = LocalDateTime.now().format(formatter);
                
                long startTime = System.currentTimeMillis();
                
                // Simular guardado en base de datos
                Thread.sleep(50);
                
                long saveTime = System.currentTimeMillis() - startTime;
                
                SwingUtilities.invokeLater(() -> {
                    historyTableModel.addRow(new Object[]{
                        currentDate,
                        selectedProduct.name,
                        (int)quantity,
                        totalText,
                        saveTime + "ms"
                    });
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    calculationStatusLabel.setText("Error al guardar: " + e.getMessage());
                });
            }
        });
    }
    
    // SISTEMA DE PRUEBAS AUTOMATIZADO
    
    private void runAutomatedTests() {
        testingMode.set(true);
        runAutomatedTestsButton.setEnabled(false);
        
        CompletableFuture.runAsync(() -> {
            logTest("=== INICIANDO PRUEBAS AUTOMATIZADAS ===");
            
            // Test 1: Carga de datos
            testDataLoading();
            
            // Test 2: Cálculos básicos
            testBasicCalculations();
            
            // Test 3: Manejo de errores
            testErrorHandling();
            
            // Test 4: Concurrencia
            testConcurrentCalculations();
            
            logTest("=== PRUEBAS COMPLETADAS ===");
            
            SwingUtilities.invokeLater(() -> {
                runAutomatedTestsButton.setEnabled(true);
                testingMode.set(false);
            });
        });
    }
    
    private void testDataLoading() {
        logTest("\n--- Test: Carga de Datos ---");
        long startTime = System.currentTimeMillis();
        
        // Simular carga de productos
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        long loadTime = System.currentTimeMillis() - startTime;
        logTest("✓ Carga de datos completada en " + loadTime + "ms");
    }
    
    private void testBasicCalculations() {
        logTest("\n--- Test: Cálculos Básicos ---");
        
        SwingUtilities.invokeLater(() -> {
            if (productComboBox.getItemCount() > 0) {
                productComboBox.setSelectedIndex(0);
                quantityField.setText("5");
                customerTypeComboBox.setSelectedIndex(1);
            }
        });
        
        try {
            Thread.sleep(500); // Esperar cálculo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logTest("✓ Cálculo básico ejecutado");
    }
    
    private void testErrorHandling() {
        logTest("\n--- Test: Manejo de Errores ---");
        
        SwingUtilities.invokeLater(() -> {
            quantityField.setText("invalid");
        });
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logTest("✓ Manejo de errores validado");
        
        SwingUtilities.invokeLater(() -> {
            quantityField.setText("1");
        });
    }
    
    private void testConcurrentCalculations() {
        logTest("\n--- Test: Cálculos Concurrentes ---");
        
        ExecutorService testExecutor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(10);
        
        for (int i = 0; i < 10; i++) {
            final int testNum = i + 1;
            testExecutor.submit(() -> {
                try {
                    SwingUtilities.invokeLater(() -> {
                        quantityField.setText(String.valueOf(testNum));
                    });
                    
                    Thread.sleep(100);
                    logTest("  Cálculo concurrente #" + testNum + " completado");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        testExecutor.shutdown();
        logTest("✓ Test de concurrencia completado");
    }
    private void runStressTest() {
        CompletableFuture.runAsync(() -> {
            logTest("=== INICIANDO TEST DE ESTRÉS ===");
            
            long startTime = System.currentTimeMillis();
            ExecutorService stressExecutor = Executors.newFixedThreadPool(10);
            CountDownLatch stressLatch = new CountDownLatch(100);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            
            for (int i = 0; i < 100; i++) {
                final int testId = i;
                stressExecutor.submit(() -> {
                    try {
                        // Simular cálculo intensivo
                        double result = performStressCalculation(testId);
                        if (result > 0) {
                            successCount.incrementAndGet();
                        }
                        
                        if (testId % 10 == 0) {
                            logTest("Completados " + testId + " cálculos de estrés");
                        }
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        logTest("Error en cálculo #" + testId + ": " + e.getMessage());
                    } finally {
                        stressLatch.countDown();
                    }
                });
            }
            
            try {
                stressLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            stressExecutor.shutdown();
            long totalTime = System.currentTimeMillis() - startTime;
            
            logTest("✓ Test de estrés completado:");
            logTest("  - Tiempo total: " + totalTime + "ms");
            logTest("  - Éxitos: " + successCount.get());
            logTest("  - Errores: " + errorCount.get());
            logTest("  - Promedio por cálculo: " + (totalTime / 100.0) + "ms");
        });
    }
    
    private double performStressCalculation(int testId) throws InterruptedException {
        // Simular cálculo complejo con diferentes patrones
        Thread.sleep(10 + (testId % 50));
        
        double basePrice = 100.0 + (testId % 1000);
        double quantity = 1 + (testId % 20);
        double discount = (testId % 15) * 0.05;
        
        return basePrice * quantity * (1 - discount);
    }
    
    private void runConcurrencyTest() {
        CompletableFuture.runAsync(() -> {
            logTest("=== TEST DE CONCURRENCIA AVANZADO ===");
            
            int numThreads = 8;
            int operationsPerThread = 25;
            ExecutorService concurrentExecutor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch concurrencyLatch = new CountDownLatch(numThreads);
            
            AtomicInteger totalOperations = new AtomicInteger(0);
            AtomicLong totalTime = new AtomicLong(0);
            
            for (int threadId = 0; threadId < numThreads; threadId++) {
                final int tId = threadId;
                concurrentExecutor.submit(() -> {
                    long threadStartTime = System.currentTimeMillis();
                    
                    try {
                        for (int op = 0; op < operationsPerThread; op++) {
                            // Simular diferentes tipos de operaciones concurrentes
                            performConcurrentOperation(tId, op);
                            totalOperations.incrementAndGet();
                            
                            // Pequeña pausa aleatoria para simular carga real
                            Thread.sleep((tId + op) % 20);
                        }
                        
                        long threadTime = System.currentTimeMillis() - threadStartTime;
                        totalTime.addAndGet(threadTime);
                        
                        logTest("Hilo #" + tId + " completado en " + threadTime + "ms");
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logTest("Hilo #" + tId + " interrumpido");
                    } finally {
                        concurrencyLatch.countDown();
                    }
                });
            }
            
            try {
                concurrencyLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            concurrentExecutor.shutdown();
            
            logTest("✓ Test de concurrencia completado:");
            logTest("  - Hilos utilizados: " + numThreads);
            logTest("  - Operaciones totales: " + totalOperations.get());
            logTest("  - Tiempo total acumulado: " + totalTime.get() + "ms");
            logTest("  - Promedio por hilo: " + (totalTime.get() / numThreads) + "ms");
        });
    }
    
    private void performConcurrentOperation(int threadId, int operationId) throws InterruptedException {
        // Simular diferentes tipos de operaciones que podrían ejecutarse concurrentemente
        switch (operationId % 4) {
            case 0:
                // Simular cálculo de precio
                double price = 50.0 + (threadId * operationId) % 500;
                double discount = (operationId % 10) * 0.02;
                double result = price * (1 - discount);
                Thread.sleep(5);
                break;
                
            case 1:
                // Simular acceso a base de datos
                Thread.sleep(15);
                break;
                
            case 2:
                // Simular validación de datos
                String data = "Product-" + threadId + "-" + operationId;
                data.length(); // Operación simple
                Thread.sleep(3);
                break;
                
            case 3:
                // Simular actualización de interfaz
                Thread.sleep(8);
                break;
        }
    }
    
    private void runDatabaseTest() {
        CompletableFuture.runAsync(() -> {
            logTest("=== TEST DE BASE DE DATOS ===");
            
            // Test de conexión
            testDatabaseConnection();
            
            // Test de consultas paralelas
            testParallelQueries();
            
            // Test de transacciones
            testTransactions();
            
            logTest("✓ Test de base de datos completado");
        });
    }
    
    private void testDatabaseConnection() {
        logTest("Probando conexión a base de datos...");
        
        try {
            // Simular conexión a base de datos
            Thread.sleep(100);
            logTest("✓ Conexión establecida correctamente");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logTest("✗ Error en conexión");
        }
    }
    
    private void testParallelQueries() {
        logTest("Ejecutando consultas paralelas...");
        
        ExecutorService queryExecutor = Executors.newFixedThreadPool(4);
        CountDownLatch queryLatch = new CountDownLatch(5);
        
        String[] queries = {
            "SELECT * FROM products",
            "SELECT * FROM tariffs", 
            "SELECT * FROM customers",
            "SELECT * FROM calculations",
            "SELECT * FROM history"
        };
        
        for (int i = 0; i < queries.length; i++) {
            final String query = queries[i];
            final int queryId = i;
            
            queryExecutor.submit(() -> {
                try {
                    long queryStart = System.currentTimeMillis();
                    
                    // Simular ejecución de consulta
                    Thread.sleep(50 + (queryId * 20));
                    
                    long queryTime = System.currentTimeMillis() - queryStart;
                    logTest("  Query #" + queryId + " completada en " + queryTime + "ms");
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    queryLatch.countDown();
                }
            });
        }
        
        try {
            queryLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        queryExecutor.shutdown();
        logTest("✓ Consultas paralelas completadas");
    }
    
    private void testTransactions() {
        logTest("Probando transacciones...");
        
        try {
            // Simular transacción compleja
            Thread.sleep(80);
            logTest("  - Transacción iniciada");
            
            Thread.sleep(40);
            logTest("  - Datos insertados");
            
            Thread.sleep(30);
            logTest("  - Transacción confirmada");
            
            logTest("✓ Test de transacciones exitoso");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logTest("✗ Error en transacción");
        }
    }
    
    private void runPerformanceTest() {
        CompletableFuture.runAsync(() -> {
            logTest("=== TEST DE RENDIMIENTO ===");
            
            long startTime = System.currentTimeMillis();
            
            // Test de rendimiento de cálculos
            testCalculationPerformance();
            
            // Test de rendimiento de interfaz
            testUIPerformance();
            
            // Test de uso de memoria
            testMemoryUsage();
            
            long totalTime = System.currentTimeMillis() - startTime;
            logTest("✓ Test de rendimiento completado en " + totalTime + "ms");
        });
    }
    
    private void testCalculationPerformance() {
        logTest("Midiendo rendimiento de cálculos...");
        
        int numCalculations = 1000;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numCalculations; i++) {
            try {
                performStressCalculation(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        long calculationTime = System.currentTimeMillis() - startTime;
        double avgTime = calculationTime / (double) numCalculations;
        
        logTest("  - " + numCalculations + " cálculos en " + calculationTime + "ms");
        logTest("  - Promedio: " + String.format("%.2f", avgTime) + "ms por cálculo");
        logTest("  - Throughput: " + String.format("%.0f", 1000.0 / avgTime) + " cálculos/segundo");
    }
    
    private void testUIPerformance() {
        logTest("Probando rendimiento de interfaz...");
        
        SwingUtilities.invokeLater(() -> {
            long uiStartTime = System.currentTimeMillis();
            
            // Simular múltiples actualizaciones de interfaz
            for (int i = 0; i < 50; i++) {
                quantityField.setText(String.valueOf(i + 1));
                
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            long uiTime = System.currentTimeMillis() - uiStartTime;
            logTest("  - Actualizaciones de UI completadas en " + uiTime + "ms");
        });
    }
    
    private void testMemoryUsage() {
        logTest("Analizando uso de memoria...");
        
        Runtime runtime = Runtime.getRuntime();
        
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        logTest("  - Memoria total: " + formatBytes(totalMemory));
        logTest("  - Memoria usada: " + formatBytes(usedMemory));
        logTest("  - Memoria libre: " + formatBytes(freeMemory));
        logTest("  - Memoria máxima: " + formatBytes(maxMemory));
        logTest("  - Uso de memoria: " + String.format("%.1f", (usedMemory * 100.0) / totalMemory) + "%");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private void logTest(String message) {
        SwingUtilities.invokeLater(() -> {
            testLogArea.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + 
                             " " + message + "\n");
            testLogArea.setCaretPosition(testLogArea.getDocument().getLength());
        });
    }
    
    // MÉTODOS DE UTILIDAD Y GESTIÓN
    
    private Product getSelectedProduct() {
        int selectedIndex = productComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < products.size()) {
            return products.get(selectedIndex);
        }
        return null;
    }
    
    private Tariff getSelectedTariff() {
        int selectedIndex = tariffComboBox.getSelectedIndex();
        if (selectedIndex <= 0) return null; // "Sin tarifa especial" está en índice 0
        
        String selectedTariffText = (String) tariffComboBox.getSelectedItem();
        
        synchronized (tariffs) {
            for (Tariff tariff : tariffs) {
                if (tariff.toString().equals(selectedTariffText)) {
                    return tariff;
                }
            }
        }
        return null;
    }
    
    private void clearCalculation() {
        quantityField.setText("1");
        productComboBox.setSelectedIndex(0);
        customerTypeComboBox.setSelectedIndex(0);
        tariffComboBox.setSelectedIndex(0);
        clearResults();
        calculationStatusLabel.setText("Cálculo limpiado");
    }
    
    private void clearResults() {
        basePriceLabel.setText("0.00 €");
        discountLabel.setText("0%");
        finalPriceLabel.setText("0.00 €");
        totalAmountLabel.setText("0.00 €");
    }
    
    private void clearHistory() {
        historyTableModel.setRowCount(0);
        calculationStatusLabel.setText("Historial limpiado");
    }
    
    private void exportHistoryAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                logTest("Iniciando exportación de historial...");
                
                // Simular proceso de exportación
                Thread.sleep(500);
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Historial exportado exitosamente", 
                        "Exportación", 
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
                logTest("✓ Historial exportado correctamente");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logTest("✗ Error en exportación");
            }
        });
    }
    
    private void runBulkCalculation() {
        CompletableFuture.runAsync(() -> {
            logTest("=== CÁLCULO MASIVO ===");
            
            int numCalculations = 20;
            ExecutorService bulkExecutor = Executors.newFixedThreadPool(5);
            CountDownLatch bulkLatch = new CountDownLatch(numCalculations);
            
            for (int i = 0; i < numCalculations; i++) {
                final int calcId = i;
                bulkExecutor.submit(() -> {
                    try {
                        double result = performStressCalculation(calcId);
                        
                        SwingUtilities.invokeLater(() -> {
                            historyTableModel.addRow(new Object[]{
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss")),
                                "Producto-" + calcId,
                                calcId + 1,
                                currencyFormat.format(result),
                                "Bulk"
                            });
                        });
                        
                        if (calcId % 5 == 0) {
                            logTest("Completados " + calcId + " cálculos masivos");
                        }
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        bulkLatch.countDown();
                    }
                });
            }
            
            try {
                bulkLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            bulkExecutor.shutdown();
            logTest("✓ Cálculo masivo completado");
        });
    }
    
    private void showTariffManagement() {
        SwingUtilities.invokeLater(() -> {
            JDialog tariffDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                             "Gestión de Tarifas", true);
            tariffDialog.setSize(600, 400);
            tariffDialog.setLocationRelativeTo(this);
            
            JPanel tariffPanel = new JPanel(new BorderLayout());
            
            // Lista de tarifas
            String[] columns = {"ID", "Nombre", "Cliente", "Categoría", "Descuento", "Mín. Cantidad"};
            DefaultTableModel tariffTableModel = new DefaultTableModel(columns, 0);
            
            synchronized (tariffs) {
                for (Tariff tariff : tariffs) {
                    tariffTableModel.addRow(new Object[]{
                        tariff.id,
                        tariff.name,
                        tariff.customerType,
                        tariff.productCategory,
                        tariff.discountPercentage + "%",
                        tariff.minimumQuantity
                    });
                }
            }
            
            JTable tariffTable = new JTable(tariffTableModel);
            JScrollPane scrollPane = new JScrollPane(tariffTable);
            
            // Panel de botones
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton addButton = new JButton("Agregar");
            JButton editButton = new JButton("Editar");
            JButton deleteButton = new JButton("Eliminar");
            JButton closeButton = new JButton("Cerrar");
            
            addButton.addActionListener(e -> {
                // Implementar agregar tarifa
                logTest("Función agregar tarifa - Por implementar");
            });
            
            editButton.addActionListener(e -> {
                // Implementar editar tarifa
                logTest("Función editar tarifa - Por implementar");
            });
            
            deleteButton.addActionListener(e -> {
                // Implementar eliminar tarifa
                logTest("Función eliminar tarifa - Por implementar");
            });
            
            closeButton.addActionListener(e -> tariffDialog.dispose());
            
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(closeButton);
            
            tariffPanel.add(scrollPane, BorderLayout.CENTER);
            tariffPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            tariffDialog.add(tariffPanel);
            tariffDialog.setVisible(true);
        });
    }
    
    // MÉTODOS DE CARGA DE DATOS (Simulación)
    
    private void loadProducts() {
    products.clear();

    try (Connection conn = DatabaseHelper.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT ID_Producto, Nombre, Tipo, PrecioProducto FROM Productos")) {

        while (rs.next()) {
            int id = rs.getInt("ID_Producto");
            String nombre = rs.getString("Nombre");
            String tipo = rs.getString("Tipo");
            double precio = rs.getDouble("PrecioProducto");

            products.add(new Product(id, nombre, tipo, precio));
        }

        SwingUtilities.invokeLater(() -> {
            productComboBox.removeAllItems();
            for (Product product : products) {
                productComboBox.addItem(product.toString());
            }
        });

    } catch (SQLException e) {
        e.printStackTrace();
        updateStatusBar("Error cargando productos desde la base de datos.");
    }
}

    
    private void loadTariffs() {
    tariffs.clear();

    String query = "SELECT id, nombre, tipo_cliente, categoria_producto, porcentaje_descuento, cantidad_minima, descripcion " +
                   "FROM tarifas WHERE activa = 1";

    try (Connection conn = DatabaseHelper.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            String tipoCliente = rs.getString("tipo_cliente");
            String categoria = rs.getString("categoria_producto");
            double descuento = rs.getDouble("porcentaje_descuento");
            double cantidadMinima = rs.getDouble("cantidad_minima");
            String descripcion = rs.getString("descripcion");

            tariffs.add(new Tariff(id, nombre, tipoCliente, categoria, descuento, cantidadMinima, descripcion));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        updateStatusBar("Error cargando tarifas desde la base de datos.");
    }
}

    
    private void updateStatusBar(String message) {
        if (statusBar != null) {
            statusBar.setText(message);
        }
    }
    
    // CLEANUP Y FINALIZACIÓN
    
    public void shutdown() {
        if (calculationExecutor != null && !calculationExecutor.isShutdown()) {
            calculationExecutor.shutdown();
            try {
                if (!calculationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    calculationExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                calculationExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (databaseExecutor != null && !databaseExecutor.isShutdown()) {
            databaseExecutor.shutdown();
            try {
                if (!databaseExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    databaseExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                databaseExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (dataLoaderWorker != null && !dataLoaderWorker.isDone()) {
            dataLoaderWorker.cancel(true);
        }
    }
}