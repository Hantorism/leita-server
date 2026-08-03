-- V3__add_language.sql
-- Create languages table and insert default supported languages

CREATE TABLE IF NOT EXISTS languages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(255) NOT NULL UNIQUE,
    extension VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default supported languages safely
INSERT IGNORE INTO languages (created_at, updated_at, name, code, extension) VALUES
(NOW(), NOW(), 'C', 'c', 'c'),
(NOW(), NOW(), 'C++', 'cpp', 'cpp'),
(NOW(), NOW(), 'Java', 'java', 'java'),
(NOW(), NOW(), 'Python', 'python', 'py'),
(NOW(), NOW(), 'JavaScript', 'javascript', 'js'),
(NOW(), NOW(), 'Go', 'go', 'go'),
(NOW(), NOW(), 'Kotlin', 'kotlin', 'kt'),
(NOW(), NOW(), 'Swift', 'swift', 'swift'),
(NOW(), NOW(), 'C#', 'cs', 'cs'),
(NOW(), NOW(), 'TypeScript', 'typescript', 'ts'),
(NOW(), NOW(), 'Rust', 'rust', 'rs');

-- Modify judge table used_language column type from ENUM to VARCHAR(255)
ALTER TABLE judge MODIFY COLUMN used_language VARCHAR(255) NULL;
