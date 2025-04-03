import requests
import logging
import os
import datetime

LOGS_FOLDER = "logs"
os.makedirs(LOGS_FOLDER, exist_ok=True)
log_filename = os.path.join(LOGS_FOLDER, datetime.date.today().strftime("wehrmacht_%Y-%m-%d.log"))
logging.basicConfig(filename=log_filename, level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

BASE_URL = "http://localhost:4567"
DATA_FILENAME = "enemigos.txt"
DATA_FOLDER = "Descargas"
LOGS_HISTORY_DAYS = 3

def send_data(potencia, hostilidad, ubicacion, frente, numero_tropas, tipo_tropas, hora_despliegue):
    """Envía datos a los servicios web de la aplicación Java."""
    try:
        data_enemigos = {
            "potencia": potencia,
            "hostilidad": hostilidad,
            "ubicacion": ubicacion
        }
        response_enemigos = requests.post(f"{BASE_URL}/enemigos", data=data_enemigos)
        response_enemigos.raise_for_status()

        data_tropas = {
            "potencia": potencia,
            "frente": frente,
            "numero_tropas": numero_tropas,
            "tipo_tropas": tipo_tropas,
            "hora_despliegue": hora_despliegue
        }
        response_tropas = requests.post(f"{BASE_URL}/tropas", data=data_tropas)
        response_tropas.raise_for_status()

        logging.info(f"Datos enviados correctamente: {potencia}, {frente}")

    except requests.exceptions.RequestException as e:
        logging.error(f"Error al enviar datos: {e}")

def delete_old_logs(days=LOGS_HISTORY_DAYS):
    """Elimina archivos de log más antiguos que el número de días especificado."""
    cutoff_date = datetime.date.today() - datetime.timedelta(days=days)
    for filename in os.listdir(LOGS_FOLDER):
        if filename.startswith("wehrmacht_") and filename.endswith(".log"):
            try:
                file_date = datetime.datetime.strptime(filename, "wehrmacht_%Y-%m-%d.log").date()
                if file_date < cutoff_date:
                    os.remove(os.path.join(LOGS_FOLDER, filename))
                    logging.info(f"Archivo de log eliminado: {filename}")
            except ValueError:
                pass

def main():
    """Lee el archivo de datos y envía los datos."""
    try:
        file_path = os.path.join(DATA_FOLDER, DATA_FILENAME)
        with open(file_path, "r") as file:
            next(file)
            for line in file:
                data = line.strip().split("|")
                if len(data) == 7:
                    potencia, hostilidad, ubicacion, frente, numero_tropas, tipo_tropas, hora_despliegue = data
                    send_data(potencia, hostilidad, ubicacion, frente, numero_tropas, tipo_tropas, hora_despliegue)
    except FileNotFoundError:
        logging.error(f"Error: El archivo {DATA_FILENAME} no se encontró en {DATA_FOLDER}.")
    except Exception as e:
        logging.error(f"Ocurrió un error inesperado: {e}")

if __name__ == "__main__":
    main()
    delete_old_logs()