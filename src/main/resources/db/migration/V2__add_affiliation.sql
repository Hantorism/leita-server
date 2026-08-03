-- V2__add_affiliation.sql
-- Create affiliations table and update users reference

CREATE TABLE IF NOT EXISTS affiliations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    email_domain VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default affiliation: 아주대학교
INSERT INTO affiliations (created_at, updated_at, name, email_domain)
VALUES (NOW(), NOW(), '아주대학교', 'ajou.ac.kr');

-- Add affiliation_id column to users table
ALTER TABLE users ADD COLUMN affiliation_id BIGINT NULL;

-- Update existing users to map to 아주대학교 (id = 1)
UPDATE users SET affiliation_id = 1 WHERE email LIKE '%@ajou.ac.kr';

-- Fallback for other users
UPDATE users SET affiliation_id = 1 WHERE affiliation_id IS NULL;

-- Modify column to be NOT NULL
ALTER TABLE users MODIFY COLUMN affiliation_id BIGINT NOT NULL;

-- Add foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_affiliation FOREIGN KEY (affiliation_id) REFERENCES affiliations (id);
