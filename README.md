# prueba-tecnica-tia
# Inteligencia Wehrmacht

Esta aplicación Java proporciona una interfaz para interactuar con una base de datos MySQL, permitiendo insertar datos sobre enemigos y tropas, y generar un reporte en formato Excel.

## Requisitos

* Java 17
* Maven
* MySQL Server
* Un archivo de ícono (icono.png) en la carpeta `src/main/resources`

## Configuración

1.  **Base de Datos MySQL:**
    * Asegúrate de tener un servidor MySQL en ejecución.
    * Crea una base de datos llamada `wehrmacht_db`.
    * Crea las tablas `potencia` y `tropas` con la siguiente estructura:

        ```sql
        CREATE TABLE potencia (
            potencia VARCHAR(255),
            hostilidad VARCHAR(255),
            ubicacion VARCHAR(255)
        );

        CREATE TABLE tropas (
            potencia VARCHAR(255),
            frente VARCHAR(255),
            numero_tropas INT,
            tipo_tropas VARCHAR(255),
            hora_despliegue VARCHAR(255)
        );
        ```

2.  **Configuración de la Aplicación:**
    * Puedes configurar la aplicación utilizando propiedades del sistema o modificando el código fuente.
    * Las siguientes propiedades del sistema son opcionales:
        * `jdbc.url`: URL de la base de datos MySQL (por defecto: `jdbc:mysql://localhost:3306/wehrmacht_db`).
        * `jdbc.user`: Nombre de usuario de la base de datos MySQL (por defecto: `root`).
        * `jdbc.password`: Contraseña de la base de datos MySQL (por defecto: `pass1234`).
        * `max.pool.size`: Tamaño máximo del pool de conexiones (por defecto: `10`).
        * `report.filename`: Nombre del archivo de reporte Excel (por defecto: `reporte_enemigo.xls`).
        * `report.sheet.name`: Nombre de la hoja de Excel en el reporte (por defecto: `Reporte Tropas`).
        * `app.title`: Título de la ventana de la aplicación (por defecto: `Inteligencia Wehrmacht`).
    * Ejemplo de configuración utilizando propiedades del sistema:

        ```bash
        mvn spring-boot:run -Djdbc.url=jdbc:mysql://mi-servidor:3306/mi_base_de_datos -Djdbc.user=mi_usuario -Djdbc.password=mi_contraseña
        ```

3.  **Ícono de la Aplicación:**
    * Coloca un archivo de ícono llamado `icono.png` en la carpeta `src/main/resources`.

## Construcción y Ejecución

1.  **Construir la Aplicación:**
    * Abre una terminal y navega al directorio del proyecto.
    * Ejecuta el siguiente comando Maven:

        ```bash
        mvn clean install
        ```

2.  **Ejecutar la Aplicación:**
    * Ejecuta la clase `com.tia.wehrmacht.WehrmachtApp` desde tu IDE o utilizando el siguiente comando Maven:

        ```bash
        mvn spring-boot:run
        ```

## Uso

1.  **Interfaz Web:**
    * La aplicación expone dos endpoints HTTP POST:
        * `/enemigos`: Para insertar datos sobre enemigos.
        * `/tropas`: Para insertar datos sobre tropas.
    * Puedes usar herramientas como Postman o curl para enviar solicitudes POST a estos endpoints.

2.  **Interfaz Gráfica:**
    * La aplicación también proporciona una interfaz gráfica simple con un botón "Generar Reporte".
    * Al hacer clic en este botón, se genera un reporte Excel con el resumen de las tropas por potencia y frente, y se guarda en la carpeta `reportes`.

## Dependencias

* Spark Framework
* HikariCP
* Apache POI
* SLF4j
* Logback
* MySQL Connector/J