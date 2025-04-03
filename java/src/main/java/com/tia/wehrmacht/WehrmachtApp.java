package com.tia.wehrmacht;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.*;
import javax.swing.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import spark.Spark;

public class WehrmachtApp {
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/wehrmacht_db";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "pass1234";
    
    public static void main(String[] args) {

        Spark.post("/enemigos", (req, res) -> {
            String potencia = req.queryParams("potencia");
            String hostilidad = req.queryParams("hostilidad");
            String ubicacion = req.queryParams("ubicacion");
            insertarEnPotencia(potencia, hostilidad, ubicacion);
            return "OK";
        });

        Spark.post("/tropas", (req, res) -> {
            String potencia = req.queryParams("potencia");
            String frente = req.queryParams("frente");
            int numeroTropas = Integer.parseInt(req.queryParams("numero_tropas"));
            String tipoTropas = req.queryParams("tipo_tropas");
            String horaDespliegue = req.queryParams("hora_despliegue");
            insertarEnTropas(potencia, frente, numeroTropas, tipoTropas, horaDespliegue);
            return "OK";
        });


        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wehrmacht Intelligence");
            JButton reportButton = new JButton("Generar Reporte");
            reportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generarReporte();
                }
            });
            frame.add(reportButton, BorderLayout.CENTER);
            frame.setSize(300, 100);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private static void insertarEnPotencia(String potencia, String hostilidad, String ubicacion) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO potencia (potencia, hostilidad, ubicacion) VALUES (?, ?, ?)")) {
            stmt.setString(1, potencia);
            stmt.setString(2, hostilidad);
            stmt.setString(3, ubicacion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertarEnTropas(String potencia, String frente, int numeroTropas, String tipoTropas, String horaDespliegue) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tropas (potencia, frente, numero_tropas, tipo_tropas, hora_despliegue) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, potencia);
            stmt.setString(2, frente);
            stmt.setInt(3, numeroTropas);
            stmt.setString(4, tipoTropas);
            stmt.setString(5, horaDespliegue);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generarReporte() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT potencia, frente, SUM(numero_tropas) FROM tropas GROUP BY potencia, frente")) {

            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Reporte Tropas");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Potencia");
            headerRow.createCell(1).setCellValue("Frente");
            headerRow.createCell(2).setCellValue("Total Tropas");

            int rowNum = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rs.getString(1));
                row.createCell(1).setCellValue(rs.getString(2));
                row.createCell(2).setCellValue(rs.getInt(3));
            }

            FileOutputStream fileOut = new FileOutputStream("reporte_enemigo.xls");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            JOptionPane.showMessageDialog(null, "Reporte generado con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el reporte.");
        }
    }

}
