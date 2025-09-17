-- Inserir papéis na tabela 'roles'
INSERT INTO roles (role_name, description, created_at) VALUES ('ROLE_ADMIN', 'Administrator role with full permissions', CURRENT_TIMESTAMP);

INSERT INTO roles (role_name, description, created_at) VALUES ('ROLE_USER', 'Regular user role with limited permissions', CURRENT_TIMESTAMP);

-- Inserir usuários na tabela 'users' senha123
INSERT INTO users (name, email, password, created_at, cpf, phone, status, birth_date, email_verified_at) VALUES ('João Silva', 'joao.silva@email.com', '$2a$10$odd29HJ1aXK/5MIYHO2rbu9O1apPF03KRmhx940iy3GZ5Oamwpg4y', CURRENT_TIMESTAMP, '12345678901', '11987654321', 'ACTIVE', '1990-05-15', CURRENT_TIMESTAMP);

INSERT INTO users (name, email, password, created_at, cpf, phone, status, birth_date, email_verified_at) VALUES ('Maria Oliveira', 'maria.oliveira@email.com', '$2a$10$odd29HJ1aXK/5MIYHO2rbu9O1apPF03KRmhx940iy3GZ5Oamwpg4y', CURRENT_TIMESTAMP, '98765432109', '11911223344', 'ACTIVE', '1985-08-22', CURRENT_TIMESTAMP);

-- Associar o usuário 'João Silva' ao papel 'ADMIN'
INSERT INTO users_roles (user_id, role_id, assigned_at) VALUES (1, 1, CURRENT_TIMESTAMP);

-- Associar o usuário 'Maria Oliveira' ao papel 'USER'
INSERT INTO users_roles (user_id, role_id, assigned_at) VALUES (2, 2, CURRENT_TIMESTAMP);