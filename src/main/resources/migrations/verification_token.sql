CREATE TABLE verification_token (
        id VARCHAR(255) PRIMARY KEY,
        user_id VARCHAR(255),

        token VARCHAR(255),

        status VARCHAR(50) NOT NULL,
        reason VARCHAR(50) NOT NULL,

        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        expire_at TIMESTAMP NOT NULL,

        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

        ip_address VARCHAR(255) NOT NULL,
        user_agent VARCHAR(500) NOT NULL
);