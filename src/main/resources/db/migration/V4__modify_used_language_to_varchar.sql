-- V4__modify_used_language_to_varchar.sql
-- Modify judge table used_language column type from ENUM to VARCHAR(255) for dynamic language support

ALTER TABLE judge MODIFY COLUMN used_language VARCHAR(255) NULL;
