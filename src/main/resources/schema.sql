-- ============================================================
-- Script de base de datos H2
-- Proyecto: API REST de Usuarios
-- ============================================================

DROP TABLE IF EXISTS phones;
DROP TABLE IF EXISTS users;

-- ============================================================
-- Tabla: users
-- ============================================================
CREATE TABLE users (
    id CHAR(36) PRIMARY KEY,                -- UUID del usuario
    name VARCHAR(255) NOT NULL,             -- Nombre del usuario
    email VARCHAR(255) NOT NULL UNIQUE,     -- Correo único
    password VARCHAR(255) NOT NULL,         -- Contraseña (encriptada)
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha de creación
    modified TIMESTAMP,                     -- Última modificación
    last_login TIMESTAMP,                   -- Último inicio de sesión
    is_active BOOLEAN DEFAULT TRUE,         -- Usuario activo/inactivo
    token VARCHAR(500)                      -- Token JWT
);

-- ============================================================
-- Tabla: phones
-- ============================================================
CREATE TABLE phones (
    id CHAR(36) PRIMARY KEY,                -- UUID del teléfono
    number VARCHAR(50) NOT NULL,            -- Número telefónico
    citycode VARCHAR(10) NOT NULL,          -- Código de ciudad
    contrycode VARCHAR(10) NOT NULL,        -- Código de país
    user_id CHAR(36),                       -- Relación con usuario
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ============================================================
-- Índices
-- ============================================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_phones_user_id ON phones(user_id);

-- ============================================================
-- Datos de Prueba
-- ============================================================

-- Usuario: Juan Rodriguez
INSERT INTO users (id, name, email, password, created, is_active)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Juan Rodriguez',
    'juan@rodriguez.org',
    '$2a$10$7Q7QKx4s.8o4skfS2WFeuOYe/2hZT0tfTq6j3CnZPE8jG7Z7KqXum',
    CURRENT_TIMESTAMP,
    TRUE
);

INSERT INTO phones (id, number, citycode, contrycode, user_id)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '1234567',
    '1',
    '57',
    '11111111-1111-1111-1111-111111111111'
);
