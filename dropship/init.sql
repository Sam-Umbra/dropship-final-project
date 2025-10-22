SET NAMES utf8mb4;

-- ======================================
-- Banco de dados: center_db
-- ======================================
CREATE DATABASE IF NOT EXISTS center_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE center_db;

-- ======================================
-- 1. Usuários
-- ======================================
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    phone CHAR(16) NOT NULL,
    status ENUM('ACTIVE','INACTIVE','OUT_OF_STOCK','DISCONTINUED', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    birth_date DATE NOT NULL,
    email_verified_at TIMESTAMP NULL,
    last_login TIMESTAMP NULL,
    last_exit TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (user_id, name, email, password, created_at, cpf, phone, status, birth_date, email_verified_at) VALUES
(1, 'João Silva', 'joao.silva@email.com', '$2a$10$odd29HJ1aXK/5MIYHO2rbu9O1apPF03KRmhx940iy3GZ5Oamwpg4y', NOW(), '12345678901', '+5511987654321', 'ACTIVE', '1990-05-15', NOW()),
(2, 'Maria Oliveira', 'maria.oliveira@email.com', '$2a$10$odd29HJ1aXK/5MIYHO2rbu9O1apPF03KRmhx940iy3GZ5Oamwpg4y', NOW(), '98765432109', '+5511911223344', 'ACTIVE', '1985-08-22', NOW()),
(3, 'Kaue', 'kaue.pinheiro@email.com', '$2a$10$odd29HJ1aXK/5MIYHO2rbu9O1apPF03KRmhx940iy3GZ5Oamwpg4y', NOW(), '51906649880', '+5511811223344', 'ACTIVE', '1985-08-21', NOW());


-- ======================================
-- 2. Fornecedores (Suppliers)
-- ======================================

DROP TABLE IF EXISTS suppliers;

CREATE TABLE suppliers (
    supplier_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(150) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    tier ENUM('OFFICIAL', 'ESPECIALIZED', 'VERIFIED', 'NORMAL') NOT NULL,  -- SupplierTier enum
    db_url VARCHAR(255) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    commission_rate DECIMAL(5,2) NOT NULL CHECK (commission_rate >= 0.0 AND commission_rate <= 100.0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inserindo fornecedores de exemplo
INSERT INTO suppliers (supplier_name, cnpj, approved, tier, db_url, status, contact_email, contact_phone, commission_rate)
VALUES
('TechMax Distribuidora', '12.345.678/0001-99', TRUE, 'OFFICIAL', 'jdbc:mysql://db.techmax.com/tech_db', 'ACTIVE', 'contato@techmax.com', '+5511999999999', 12.5);

-- ======================================
-- 8. Relacionamento Fornecedor-Usuário
-- ======================================

DROP TABLE IF EXISTS supplier_user;

CREATE TABLE supplier_user (
    supplier_user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    association_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_su_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers(supplier_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_su_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    UNIQUE KEY uq_supplier_user (supplier_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO supplier_user (supplier_id, user_id, association_date) VALUES
    (1, 2, NOW()),
    (1, 3, NOW());

-- ======================================
-- 3. Categorias
-- ======================================
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    parent_category_id INT DEFAULT NULL,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_category_id)
        REFERENCES categories(category_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO categories (category_id, category_name, parent_category_id) VALUES
    (1, 'Eletrônicos', NULL),
    (2, 'Computadores', 1),
    (3, 'Notebooks', 2),
    (4, 'Celulares', 1),
    (5, 'Smartphones', 4),
    (6, 'Acessórios', 1),
    (7, 'Fones de Ouvido', 6),
    (8, 'Jogos', 1);

-- ======================================
-- 4. Produtos
-- ======================================
CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(80) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    img_url VARCHAR(500) NOT NULL,
    discount DECIMAL(5,2) NOT NULL DEFAULT 0,
    supplier_id BIGINT NOT NULL,

    CONSTRAINT fk_products_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO products (product_id, product_name, description, price, stock, status, img_url, discount, supplier_id) VALUES
    (1, 'Notebook Gamer', 'Notebook de alto desempenho para jogos', 4500.00, 10, 'ACTIVE', 'https://m.media-amazon.com/images/I/51Wv-tEUn6L._AC_SX679_.jpg', 10, 1),
    (2, 'Smartphone X', 'Smartphone moderno com câmera avançada e alto desempenho', 2200.00, 20, 'ACTIVE', 'https://d3qoj2c6mu9s8x.cloudfront.net/Custom/Content/Products/40/06/4006975_smartphone-apple-iphone-x-5-8-camera-12mp-dual-frontal-7mp-com-ios-11-prata-256gb_m2_637223855454369999.webp', 12, 1),
    (3, 'Fone de Ouvido Bluetooth', 'Fone de ouvido sem fio com bateria de longa duração', 150.00, 50, 'ACTIVE', 'https://m.media-amazon.com/images/I/51olNZRjn+L._AC_SY300_SX300_.jpg', 25, 1);

-- ======================================
-- 5. Relacionamento Produto-Categoria
-- ======================================
-- Corrigido: product_id agora é BIGINT para corresponder à tabela products
CREATE TABLE product_categories (
    product_id BIGINT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO product_categories (product_id, category_id) VALUES
    (1, 3),
    (2, 5),
    (3, 7);

-- ======================================
-- 6. Papéis (Roles)
-- ======================================
DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(45) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Corrigido: removido ponto-e-vírgula prematuro
INSERT INTO roles (role_id, role_name, description, created_at) VALUES
    (1, 'ROLE_ADMIN', 'Administrator role with full permissions', NOW()),
    (2, 'ROLE_USER', 'Regular user role with limited permissions', NOW()),
    (3, 'ROLE_SUPPLIER_PRIMARY', 'Primary supplier role with full supplier permissions', NOW()),
    (4, 'ROLE_SUPPLIER', 'Regular supplier role with limited supplier permissions', NOW());

-- ======================================
-- 7. Usuário-Papéis
-- ======================================
-- Corrigido: user_id agora é BIGINT para corresponder à tabela users
CREATE TABLE users_roles (
    user_role_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users_roles (user_id, role_id, assigned_at) VALUES
    (1, 1, NOW()),
    (1, 2, NOW()),

    (2, 2, NOW()),
    (2, 4, NOW()),

    (3, 2, NOW()),
    (3, 3, NOW());