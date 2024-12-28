from flask import Flask, request, jsonify
from flask_cors import CORS
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)  # Utilisez __name__ ici
CORS(app)

def connect_to_db():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="cityscout"
    )

@app.route("/get-data", methods=["POST"])
def get_data():
    try:
        query = request.json.get("query")
        if not query:
            return jsonify({"error": "Query missing"}), 400

        connection = connect_to_db()
        cursor = connection.cursor(dictionary=True)
        cursor.execute(query)
        results = cursor.fetchall()
        connection.close()
        
        return jsonify(results)
    except Error as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":  
    app.run(debug=True, host="0.0.0.0", port=5000)  # Écoute sur toutes les interfaces réseau
