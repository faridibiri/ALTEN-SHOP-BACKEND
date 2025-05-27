-- Création des tables
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255),
    name VARCHAR(255),
    description TEXT,
    image VARCHAR(255),
    category VARCHAR(255),
    price DOUBLE,
    quantity INTEGER,
    internal_reference VARCHAR(255),
    shell_id BIGINT,
    inventory_status VARCHAR(20),
    rating INTEGER,
    created_at BIGINT,
    updated_at BIGINT
    );

CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    quantity INTEGER,
    FOREIGN KEY (product_id) REFERENCES products(id)
    );

-- Création de l'utilisateur admin avec mot de passe 'adminpass'
INSERT INTO users (username, firstname, email, password)
VALUES ('admin', 'Admin', 'admin@admin.com', '$2a$10$N/qhbADcqcTVVt.AIqEEUu7yqwZHOpnCu.PQEqu6nNWNY.9NGEs8K');

-- Ajout de quelques produits de test
INSERT INTO products (code, name, description, price, quantity, inventory_status, rating)
VALUES
    ('PROD-001', 'Smartphone XYZ', 'Un smartphone dernière génération', 699.99, 50, 'INSTOCK', 4),
    ('PROD-002', 'Laptop ABC', 'Ordinateur portable performant', 1299.99, 30, 'INSTOCK', 5),
    ('PROD-003', 'Tablette 123', 'Tablette tactile HD', 399.99, 0, 'OUTOFSTOCK', 4);