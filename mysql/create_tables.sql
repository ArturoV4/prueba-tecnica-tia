CREATE DATABASE IF NOT EXISTS wehrmacht_db;
USE wehrmacht_db;

CREATE TABLE IF NOT EXISTS potencia (
    potencia VARCHAR(255),
    hostilidad VARCHAR(255),
    ubicacion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tropas (
    potencia VARCHAR(255),
    frente VARCHAR(255),
    numero_tropas INT,
    tipo_tropas VARCHAR(255),
    hora_despliegue VARCHAR(255)
);