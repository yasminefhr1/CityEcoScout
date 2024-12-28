import requests
import mysql.connector

# Connexion à la base de données MySQL
conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="cityscout"
)
cursor = conn.cursor()

# Récupérer les lignes avec country NULL
cursor.execute("SELECT id, latitude, longitude FROM places WHERE country IS NULL")
rows = cursor.fetchall()

# Clé API Google Maps
api_key = "AIzaSyCuv8OC8qgqbVeLKgDFDE6zVoFdN6dyyIw"

# Parcourir chaque ligne et chercher le pays
for row in rows:
    id, lat, lng = row
    try:
        # Appel à l'API Google Maps Geocoding
        url = f"https://maps.googleapis.com/maps/api/geocode/json?latlng={lat},{lng}&key={api_key}"
        response = requests.get(url).json()

        # Extraire le nom du pays
        country = None
        for result in response.get("results", []):
            for comp in result["address_components"]:
                if "country" in comp["types"]:
                    country = comp["long_name"]
                    break
            if country:
                break

        # Mettre à jour la table si un pays a été trouvé
        if country:
            cursor.execute("UPDATE places SET country = %s WHERE id = %s", (country, id))
            conn.commit()
            print(f"ID {id} mis à jour avec le pays : {country}")
        else:
            print(f"ID {id}: Pays non trouvé pour lat {lat}, lng {lng}")

    except Exception as e:
        print(f"Erreur pour ID {id}: {e}")

# Fermer la connexion
conn.close()
print("Mise à jour terminée.")
