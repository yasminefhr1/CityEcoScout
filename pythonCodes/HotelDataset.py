import requests
import csv
import time

# Votre clé API Google Places
API_KEY = "AIzaSyA8UR32EXc8Mzd_hVoyL5Ri9RFO7Ew3wyM"

# Fonction pour rechercher des lieux
def search_places(query, location, radius, next_page_token=None):
    url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
    params = {
        "query": query,
        "location": location,
        "radius": radius,
        "key": API_KEY
    }
    # Ajouter le paramètre de pagetoken si disponible
    if next_page_token:
        params["pagetoken"] = next_page_token

    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Erreur API : {response.status_code}, {response.text}")
        return {}

# Fonction pour gérer la pagination
def fetch_all_places(query, location, radius):
    results = []
    response = search_places(query, location, radius)
    if "results" in response:
        results.extend(response["results"])
    
    # Boucle pour gérer la pagination si un "next_page_token" est disponible
    while "next_page_token" in response:
        next_page_token = response["next_page_token"]
        time.sleep(2)  # Google API nécessite une pause avant d'utiliser le token
        response = search_places(query, location, radius, next_page_token)  # Ajout du token
        if "results" in response:
            results.extend(response["results"])
    
    return results

# Fonction pour récupérer les URLs des photos
def get_photo_url(photo_reference, max_width=400):
    if photo_reference:
        return f"https://maps.googleapis.com/maps/api/place/photo?maxwidth={max_width}&photo_reference={photo_reference}&key={API_KEY}"
    return None

# Fonction pour filtrer uniquement les Hotels avec des photos
def filter_places_with_photos(results):
    filtered_results = []
    for place in results:
        if "photos" in place:  # Vérifie si des photos sont disponibles
            photo_reference = place["photos"][0].get("photo_reference")  # Prend la première photo
            if photo_reference:
                place["photo_url"] = get_photo_url(photo_reference)
                filtered_results.append(place)
    return filtered_results

def save_to_csv(results, filename="Hotels_with_photos.csv"):
    with open(filename, mode="w", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        writer.writerow([
            "name", "address", "latitude", "longitude", "rating", "photo_url", "types",
            "place_id", "user_ratings_total", "opening_hours", "price_level", "vicinity", "website", "phone_number"
        ])
        for place in results:
            writer.writerow([
                place.get("name", "Unknown"),
                place.get("formatted_address", "Unknown"),
                place["geometry"]["location"]["lat"],
                place["geometry"]["location"]["lng"],
                place.get("rating", "N/A"),
                place.get("photo_url", "No photo available"),
                ", ".join(place.get("types", [])),
                place.get("place_id", "Unknown"),
                place.get("user_ratings_total", "0"),
                place.get("opening_hours", {}).get("open_now", "Unknown"),
                place.get("price_level", "Unknown"),
                place.get("vicinity", "Unknown"),
                place.get("website", "Unknown"),
                place.get("formatted_phone_number", "Unknown") 
            ])


# Génération d'une grille globale
def generate_global_grid(step=5):
    points = []
    for lat in range(-90, 90, step):
        for lng in range(-180, 180, step):
            points.append((lat, lng))
    return points

# Script principal
def main():
    query = "sustainable Hotel"  # Mot-clé pour rechercher des Hotels
    radius = 50000  # Rayon de recherche (50 km max)
    grid_points = generate_global_grid(step=10)  # Espacement de 10 degrés
    all_results = []

    print(f"Nombre de points à couvrir : {len(grid_points)}")
    for i, (lat, lng) in enumerate(grid_points):
        location = f"{lat},{lng}"
        print(f"[{i+1}/{len(grid_points)}] Recherche à la position {location}...")
        results = fetch_all_places(query, location, radius)
        
        # Filtrer les Hotels avec photos uniquement
        filtered_results = filter_places_with_photos(results)
        all_results.extend(filtered_results)
        print(f"{len(filtered_results)} Hotels avec photos trouvés à {location}.")

        # Pause pour éviter de dépasser les quotas de l'API
        time.sleep(1)

    # Sauvegarde des résultats dans un fichier CSV
    save_to_csv(all_results, "Hotels_with_photos.csv")
    print(f"Tous les Hotels avec photos ont été sauvegardés dans 'Hotels_with_photos.csv'.")

if __name__ == "__main__":
    main()
