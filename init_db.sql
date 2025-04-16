-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    matricule VARCHAR(50)
);

-- Table des produits
CREATE TABLE IF NOT EXISTS produits (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prix DECIMAL(10, 2) NOT NULL,
    quantite_stock INTEGER NOT NULL,gstock
    seuil_alerte INTEGER NOT NULL,
    date_ajout DATE NOT NULL
);

-- Insérer un admin par défaut
INSERT INTO users (name, email, password, role)
VALUES ('Admin', 'admin@example.com', 'admin123', 'admin')
ON CONFLICT (email) DO NOTHING;