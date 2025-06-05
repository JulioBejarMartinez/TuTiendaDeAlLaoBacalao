/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tendadeallado;

/**
 *
 * @author PRACTICAS
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static ConfigReader configReader;

    public static void setConfigReader(ConfigReader reader) {
        configReader = reader;
    }

    public static Connection getConnection() throws SQLException {
        if (configReader == null) {
            throw new IllegalStateException("ConfigReader no ha sido configurado. Llama a setConfigReader() primero.");
        }

        String url = "jdbc:mysql://" + configReader.getDbHost() + ":" +
                     configReader.getDbPort() + "/" +
                     configReader.getDbName() + "?useSSL=false&serverTimezone=UTC";

        return DriverManager.getConnection(url, configReader.getDbUser(), configReader.getDbPassword());
    }
}

