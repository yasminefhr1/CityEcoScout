import mysql.connector
from mysql.connector import Error

# Dictionnaire de correspondance entre noms de pays et codes ISO
COUNTRY_CODES = {
    "Afghanistan": "af",
    "Albania": "al",
    "Algeria": "dz",
    "Andorra": "ad",
    "Angola": "ao",
    "Argentina": "ar",
    "Australia": "au",
    "Austria": "at",
    "Belgium": "be",
    "Brazil": "br",
    "Canada": "ca",
    "China": "cn",
    "Egypt": "eg",
    "France": "fr",
    "Germany": "de",
    "India": "in",
    "Indonesia": "id",
    "Italy": "it",
    "Japan": "jp",
    "Morocco": "ma",
    "Netherlands": "nl",
    "New Zealand": "nz",
    "Nigeria": "ng",
    "Norway": "no",
    "Portugal": "pt",
    "Russia": "ru",
    "Saudi Arabia": "sa",
    "South Africa": "za",
    "Spain": "es",
    "Sweden": "se",
    "Switzerland": "ch",
    "Thailand": "th",
    "Turkey": "tr",
    "United Arab Emirates": "ae",
    "United Kingdom": "gb",
    "United States": "us",
    "Vietnam": "vn"
    # Ajoutez d'autres pays selon vos besoins
}

def get_country_code(country_name):
    """Convertit le nom du pays en code ISO."""
    # Nettoyer le nom du pays (enlever les espaces en trop, mettre en titre)
    country_name = country_name.strip().title()
    return COUNTRY_CODES.get(country_name)

def get_flag_url(country_name):
    """Génère l'URL du drapeau pour un pays donné."""
    country_code = get_country_code(country_name)
    if country_code:
        return f"https://flagcdn.com/w40/{country_code}.png"
    return None

def update_flag_urls():
    try:
        # Connexion à la base de données
        connection = mysql.connector.connect(
            host="localhost",
            user="root",
            password="",  # Remplacez par votre mot de passe
            database="cityscout"
        )

        if connection.is_connected():
            cursor = connection.cursor()

            # Sélectionner tous les lieux avec leurs pays
            select_query = "SELECT id, country FROM places"
            cursor.execute(select_query)
            places = cursor.fetchall()

            # Mettre à jour chaque lieu avec l'URL de son drapeau
            update_query = "UPDATE places SET flag_url = %s WHERE id = %s"
            
            for place_id, country in places:
                if country:  # Vérifier que le pays n'est pas null
                    flag_url = get_flag_url(country)
                    if flag_url:
                        cursor.execute(update_query, (flag_url, place_id))
                        print(f"Mise à jour du drapeau pour le pays {country}: {flag_url}")
                    else:
                        print(f"Pas de code pays trouvé pour: {country}")

            # Valider les modifications
            connection.commit()
            print("Mise à jour terminée avec succès!")

            # Afficher les pays qui n'ont pas de correspondance
            print("\nListe des pays dans la base de données:")
            cursor.execute("SELECT DISTINCT country FROM places")
            db_countries = cursor.fetchall()
            for (country,) in db_countries:
                code = get_country_code(country) if country else None
                status = "✓" if code else "✗"
                print(f"{status} {country}")

    except Error as e:
        print(f"Erreur lors de la connexion à MySQL: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("Connexion MySQL fermée")

# Exécuter la mise à jour
if __name__ == "__main__":
    update_flag_urls()