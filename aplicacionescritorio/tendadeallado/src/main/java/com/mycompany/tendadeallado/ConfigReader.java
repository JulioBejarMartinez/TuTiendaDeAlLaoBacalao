/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ConfigReader {
    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String apiBaseUrl;
    private String loginEndpoint;
    private String registroEndpoint;
    private String productosEndpoint;
    private String pedidoEndpoint;
    private String usuarioEndpoint;

    public ConfigReader(String configPath) {
        try {
            File xmlFile = new File(configPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Leer configuración de base de datos
            NodeList dbConfig = doc.getElementsByTagName("database").item(0).getChildNodes();
            for (int i = 0; i < dbConfig.getLength(); i++) {
                if (dbConfig.item(i) instanceof Element) {
                    Element element = (Element) dbConfig.item(i);
                    switch (element.getTagName()) {
                        case "host" -> dbHost = element.getTextContent();
                        case "port" -> dbPort = element.getTextContent();
                        case "name" -> dbName = element.getTextContent();
                        case "user" -> dbUser = element.getTextContent();
                        case "password" -> dbPassword = element.getTextContent();
                    }
                }
            }

            // Leer configuración de API
            Element apiConfig = (Element) doc.getElementsByTagName("api").item(0);
            apiBaseUrl = apiConfig.getElementsByTagName("baseUrl").item(0).getTextContent();
            
            Element endpoints = (Element) apiConfig.getElementsByTagName("endpoints").item(0);
            loginEndpoint = endpoints.getElementsByTagName("login").item(0).getTextContent();
            registroEndpoint = endpoints.getElementsByTagName("registro").item(0).getTextContent();
            productosEndpoint = endpoints.getElementsByTagName("productos").item(0).getTextContent();
            pedidoEndpoint = endpoints.getElementsByTagName("pedido").item(0).getTextContent();
            usuarioEndpoint = endpoints.getElementsByTagName("usuario").item(0).getTextContent();

        } catch (Exception e) {
            e.printStackTrace();
            // Manejar errores adecuadamente
        }
    }

    // Getters para todos los campos
    public String getDbHost() { return dbHost; }
    public String getDbPort() { return dbPort; }
    public String getDbName() { return dbName; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    public String getApiBaseUrl() { return apiBaseUrl; }
    public String getLoginEndpoint() { return loginEndpoint; }
    public String getRegistroEndpoint() { return registroEndpoint; }
    public String getProductosEndpoint() { return productosEndpoint; }
    public String getPedidoEndpoint() { return pedidoEndpoint; }
    public String getUsuarioEndpoint() { return usuarioEndpoint; }
}
