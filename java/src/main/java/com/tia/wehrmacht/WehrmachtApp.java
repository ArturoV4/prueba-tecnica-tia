package com.tia.wehrmacht;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import spark.Spark;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.UUID;

public class WehrmachtApp {

	private static final Logger logger = LoggerFactory.getLogger(WehrmachtApp.class);

	private static final String JDBC_URL = System.getProperty("jdbc.url", "jdbc:mysql://localhost:3306/wehrmacht_db");
	private static final String JDBC_USER = System.getProperty("jdbc.user", "root");
	private static final String JDBC_PASSWORD = System.getProperty("jdbc.password", "pass1234");
	private static final int MAX_POOL_SIZE = Integer.parseInt(System.getProperty("max.pool.size", "10"));
	private static final String REPORT_FILENAME = System.getProperty("report.filename", "reporte_enemigo.xls");
	private static final String REPORT_SHEET_NAME = System.getProperty("report.sheet.name", "Reporte Tropas");
	private static final String APP_TITLE = System.getProperty("app.title", "Inteligencia Wehrmacht");
	private static final String ICON_PATH = "/icono.png";
	private static final String REPORT_DIRECTORY = "reportes";

	private static HikariDataSource dataSource;

	static {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(JDBC_URL);
		config.setUsername(JDBC_USER);
		config.setPassword(JDBC_PASSWORD);
		config.setMaximumPoolSize(MAX_POOL_SIZE);
		dataSource = new HikariDataSource(config);
	}

	public static void main(String[] args) {

		Spark.post("/enemigos", (req, res) -> {
			MDC.put("sid", UUID.randomUUID().toString());
			String potencia = req.queryParams("potencia");
			String hostilidad = req.queryParams("hostilidad");
			String ubicacion = req.queryParams("ubicacion");
			try {
				insertarEnPotencia(potencia, hostilidad, ubicacion);
				return "OK";
			} catch (SQLException e) {
				logger.error("Error al insertar en potencia", e);
				return "Error al insertar en potencia";
			} finally {
				MDC.remove("sid");
			}
		});

		Spark.post("/tropas", (req, res) -> {
			MDC.put("sid", UUID.randomUUID().toString());
			String potencia = req.queryParams("potencia");
			String frente = req.queryParams("frente");
			int numeroTropas = Integer.parseInt(req.queryParams("numero_tropas"));
			String tipoTropas = req.queryParams("tipo_tropas");
			String horaDespliegue = req.queryParams("hora_despliegue");
			try {
				insertarEnTropas(potencia, frente, numeroTropas, tipoTropas, horaDespliegue);
				return "OK";
			} catch (SQLException e) {
				logger.error("Error al insertar en tropas", e);
				return "Error al insertar en tropas";
			} finally {
				MDC.remove("sid");
			}
		});

		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame(APP_TITLE);
			JButton reportButton = new JButton("Generar Reporte");

			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.CENTER;
			gbc.insets = new Insets(20, 20, 20, 20);
			panel.add(reportButton, gbc);

			frame.add(panel);
			frame.setSize(350, 100);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			try {
				ImageIcon icon = new ImageIcon(WehrmachtApp.class.getResource(ICON_PATH));
				frame.setIconImage(icon.getImage());
			} catch (NullPointerException e) {
				logger.warn("Ícono no encontrado: " + ICON_PATH);
			}

			frame.setVisible(true);

			reportButton.addActionListener(e -> generarReporte());
		});
	}

	private static void insertarEnPotencia(String potencia, String hostilidad, String ubicacion) throws SQLException {
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn
						.prepareStatement("INSERT INTO potencia (potencia, hostilidad, ubicacion) VALUES (?, ?, ?)")) {
			stmt.setString(1, potencia);
			stmt.setString(2, hostilidad);
			stmt.setString(3, ubicacion);
			stmt.executeUpdate();
			logger.info("Datos insertados en potencia: {}, {}, {}", potencia, hostilidad, ubicacion);
		}
	}

	private static void insertarEnTropas(String potencia, String frente, int numeroTropas, String tipoTropas,
			String horaDespliegue) throws SQLException {
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO tropas (potencia, frente, numero_tropas, tipo_tropas, hora_despliegue) VALUES (?, ?, ?, ?, ?)")) {
			stmt.setString(1, potencia);
			stmt.setString(2, frente);
			stmt.setInt(3, numeroTropas);
			stmt.setString(4, tipoTropas);
			stmt.setString(5, horaDespliegue);
			stmt.executeUpdate();
			logger.info("Datos insertados en tropas: {}, {}, {}, {}, {}", potencia, frente, numeroTropas, tipoTropas,
					horaDespliegue);
		}
	}

	private static void generarReporte() {
		try (Connection conn = dataSource.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT potencia, frente, SUM(numero_tropas) FROM tropas GROUP BY potencia, frente");
				HSSFWorkbook workbook = new HSSFWorkbook()) {
			Sheet sheet = workbook.createSheet(REPORT_SHEET_NAME);
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

			// Crear la carpeta "reportes" si no existe
			File directory = new File(REPORT_DIRECTORY);
			if (!directory.exists()) {
				if (directory.mkdirs()) {
					logger.info("Carpeta 'reportes' creada con éxito.");
				} else {
					logger.error("Error al crear la carpeta 'reportes'.");
				}
			}

			String filePath = REPORT_DIRECTORY + File.separator + REPORT_FILENAME;

			try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
				workbook.write(fileOut);
			}

			JOptionPane.showMessageDialog(null, "Reporte generado con éxito en: " + filePath);
			logger.info("Reporte generado con éxito en: {}", filePath);
		} catch (Exception e) {
			logger.error("Error al generar el reporte", e);
			JOptionPane.showMessageDialog(null, "Error al generar el reporte: " + e.getMessage());
		}
	}
}