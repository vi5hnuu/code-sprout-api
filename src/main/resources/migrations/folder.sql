CREATE TABLE folder (
    id VARCHAR(32) PRIMARY KEY,
    owner_id VARCHAR(32) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    parent_id VARCHAR(32) DEFAULT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    password VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_folders_parent FOREIGN KEY (parent_id) REFERENCES folder(id) ON DELETE RESTRICT,  -- Prevent parent folder deletion if child exists
    INDEX idx_owner_parent (owner_id, parent_id),
    INDEX idx_owner_folder_name (owner_id, name),
    INDEX idx_owner (owner_id)
);

CREATE TABLE file (
    id VARCHAR(32) PRIMARY KEY,
    owner_id VARCHAR(32) NOT NULL,
    folder_id VARCHAR(32) DEFAULT NULL,
    name VARCHAR(255) NOT NULL,
    file_extension VARCHAR(20) CHECK(file_extension in ('txt','md')) NOT NULL,
    mime_type VARCHAR(50) CHECK(mime_type in ('text/plain','text/markdown')) NOT NULL,
    s3_key VARCHAR(1024) NOT NULL,
    file_size BIGINT UNSIGNED NOT NULL, --size in bytes
    visibility ENUM('public', 'private') DEFAULT 'private',
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_files_folder FOREIGN KEY (folder_id) REFERENCES folder(id)  ON DELETE RESTRICT,  -- Prevent folder deletion if it contains any files
    INDEX idx_owner_folder (owner_id, folder_id),
    INDEX idx_s3_key (s3_key)  -- Index added for quick lookups by S3 key
);