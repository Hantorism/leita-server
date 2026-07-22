-- V1__init_schema.sql
-- Initial Schema setup based on domain entities

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_image VARCHAR(255) NULL,
    github_user_name VARCHAR(255) NULL,
    installation_id VARCHAR(255) NULL,
    github_repository VARCHAR(255) NULL,
    sub VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL,
    main_language VARCHAR(255) NULL,
    department VARCHAR(255) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS qnas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    answer TEXT NULL,
    answered_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_qnas_author FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS problem (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    problem_id VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    description_problem TEXT NULL,
    description_input TEXT NULL,
    description_output TEXT NULL,
    limit_memory BIGINT NOT NULL,
    limit_time BIGINT NOT NULL,
    source VARCHAR(255) NULL,
    solved_success_count BIGINT NOT NULL DEFAULT 0,
    solved_total_count BIGINT NOT NULL DEFAULT 0,
    solved_rate DOUBLE NOT NULL DEFAULT 0.0,
    PRIMARY KEY (id),
    CONSTRAINT fk_problem_author FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS problem_test_cases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    problem_id BIGINT NOT NULL,
    input TEXT NOT NULL,
    output TEXT NOT NULL,
    is_show BIT(1) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_test_cases_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS problem_category (
    problem_id BIGINT NOT NULL,
    category VARCHAR(255) NULL,
    CONSTRAINT fk_problem_category_problem FOREIGN KEY (problem_id) REFERENCES problem (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS judge (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    problem_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    result ENUM('CORRECT', 'PENDING', 'WRONG', 'COMPILE_ERROR', 'RUNTIME_ERROR', 'TIME_OUT', 'MEMORY_OUT', 'UNKNOWN') NULL,
    used_memory BIGINT NULL,
    used_time BIGINT NULL,
    used_language ENUM('C', 'CPP', 'JAVA', 'PYTHON', 'JAVASCRIPT', 'GO', 'KOTLIN', 'SWIFT') NULL,
    size_of_code BIGINT NULL,
    code_url VARCHAR(255) NULL,
    type ENUM('SUBMIT', 'RUN') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_judge_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS study (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    title VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NULL,
    requirement TEXT NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS study_member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    study_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('ADMIN', 'MEMBER', 'PENDING') NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    approved_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_study_user (study_id, user_id),
    CONSTRAINT fk_study_member_study FOREIGN KEY (study_id) REFERENCES study (id),
    CONSTRAINT fk_study_member_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS study_session (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    start_date_time DATETIME(6) NOT NULL,
    end_date_time DATETIME(6) NOT NULL,
    study_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    study_session_id BIGINT NOT NULL,
    open_time DATETIME(6) NOT NULL,
    close_time DATETIME(6) NOT NULL,
    late_threshold_minutes INT NOT NULL DEFAULT 0,
    status ENUM('OPEN', 'CLOSED') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_attendance_session FOREIGN KEY (study_session_id) REFERENCES study_session (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS attendance_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    attendance_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('PRESENT', 'LATE', 'ABSENT') NOT NULL,
    attended_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_attendance_record_attendance FOREIGN KEY (attendance_id) REFERENCES attendance (id),
    CONSTRAINT fk_attendance_record_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS assignment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    study_session_id BIGINT NOT NULL,
    description TEXT NULL,
    start_date_time DATETIME(6) NOT NULL,
    end_date_time DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_assignment_session FOREIGN KEY (study_session_id) REFERENCES study_session (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS assignment_problem (
    assignment_id BIGINT NOT NULL,
    problem_id VARCHAR(255) NULL,
    CONSTRAINT fk_assignment_problem_assignment FOREIGN KEY (assignment_id) REFERENCES assignment (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS assignment_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    assignment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('COMPLETED', 'PARTIAL', 'INCOMPLETE') NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_assignment_user (assignment_id, user_id),
    CONSTRAINT fk_assignment_record_assignment FOREIGN KEY (assignment_id) REFERENCES assignment (id),
    CONSTRAINT fk_assignment_record_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
