# prueba-tecnica-tia
# Inteligencia Wehrmacht

Esta aplicación Java proporciona una interfaz para interactuar con una base de datos MySQL, permitiendo insertar datos sobre enemigos y tropas, y generar un reporte en formato Excel.

## Tabla de Contenido

* [Requisitos](#requisitos)
* [Configuración](#configuración)
    * [Base de Datos MySQL](#base-de-datos-mysql)
    * [Configuración de la Aplicación Java](#configuración-de-la-aplicación-java)
* [Construcción y Ejecución](#construcción-y-ejecución)
    * [Construcción de la Aplicación Java](#construcción-de-la-aplicación-java)
    * [Ejecución de la Aplicación Java](#ejecución-de-la-aplicación-java)
    * [Ejecución del Script Python](#ejecución-del-script-python)
* [Uso](#uso)
    * [Interfaz Web](#interfaz-web)
    * [Interfaz Gráfica](#interfaz-gráfica)
* [Dependencias](#dependencias)

## Requisitos

* Java 17
* Maven
* MySQL Server
* Python
* Librería `requests` de Python (instalar con `pip install requests`)

## Configuración

### Base de Datos MySQL

1.  Asegúrate de tener un servidor MySQL en ejecución.
2.  Crea la base de datos y las tablas utilizando el script SQL proporcionado: `mysql/create_tables.sql`. Puedes ejecutar este script desde la línea de comandos de MySQL o desde una herramienta como MySQL Workbench.

### Configuración de la Aplicación Java

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


## Construcción y Ejecución

### Construcción de la Aplicación Java

1.  Abre una terminal y navega al directorio del proyecto.
2.  Ejecuta el siguiente comando Maven:

    ```bash
    mvn clean install
    ```

### Ejecución de la Aplicación Java

* Ejecuta la clase `com.tia.wehrmacht.WehrmachtApp` desde tu IDE o utilizando el siguiente comando Maven:

    ```bash
    mvn spring-boot:run -Djdbc.password=<mi_contraseña_mysql>
    ```

### Ejecución del Script Python

1.  Asegúrate de que la aplicación Java esté en ejecución.
2.  Instala la librería `requests` de Python si no la tienes instalada:

    ```bash
    pip install requests
    ```

3.  Ejecuta el script Python proporcionado: `wehrmacht_script.py`. Este script enviará solicitudes POST a los endpoints de la aplicación Java para insertar datos de prueba.

    ```bash
    python wehrmacht_script.py
    ```
	
## Uso

### Interfaz Web

* La aplicación expone dos endpoints HTTP POST:
    * `/enemigos`: Para insertar datos sobre enemigos.
    * `/tropas`: Para insertar datos sobre tropas.
* Puedes usar herramientas como Postman, curl o el script Python proporcionado para enviar solicitudes POST a estos endpoints.

### Interfaz Gráfica

* La aplicación también proporciona una interfaz gráfica simple con un botón "Generar Reporte".
* Al hacer clic en este botón, se genera un reporte Excel con el resumen de las tropas por potencia y frente, y se guarda en la carpeta `reportes`.

## Dependencias

* Spark Framework
* HikariCP
* Apache POI
* SLF4j
* Logback
* MySQL Connector/J
* requests (Python)