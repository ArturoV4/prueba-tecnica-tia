import requests

def send_data(potencia, hostilidad, ubicacion, frente, numero_tropas, tipo_tropas, hora_despliegue):
    url_enemigos = "http://localhost:4567/enemigos"
    data_enemigos = {
        "potencia": potencia,
        "hostilidad": hostilidad,
        "ubicacion": ubicacion
    }
    requests.post(url_enemigos, data=data_enemigos)

    url_tropas = "http://localhost:4567/tropas"
    data_tropas = {
        "potencia": potencia,
        "frente": frente,
        "numero_tropas": numero_tropas,
        "tipo_tropas": tipo_tropas,
        "hora_despliegue": hora_despliegue
    }
    requests.post(url_tropas, data=data_tropas)

def main():
    try:
        with open("Descargas/enemigos.txt", "r") as file:
            for line in file:
                data = line.strip().split("|")
                if len(data) == 7:
                    potencia, hostilidad, ubicacion, frente, numero_tropas, tipo_tropas, hora_despliegue = data
                    send_data(potencia, hostilidad, ubicacion, frente, numero_tropas, tipo_tropas, hora_despliegue)
    except FileNotFoundError:
        print("Error: El archivo enemigos.txt no se encontró.")

if __name__ == "__main__":
    main()