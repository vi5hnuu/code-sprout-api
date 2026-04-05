CREATE TABLE IF NOT EXISTS file_view (
    id          VARCHAR(32)  NOT NULL PRIMARY KEY,
    file_id     VARCHAR(32)  NOT NULL,
    viewer_id   VARCHAR(32)  NULL,       -- NULL for anonymous viewers
    ip_address  VARCHAR(45)  NULL,       -- IPv4 / IPv6
    viewed_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_fv_file FOREIGN KEY (file_id) REFERENCES file(id) ON DELETE CASCADE,
    INDEX idx_fv_file_id   (file_id),
    INDEX idx_fv_viewer_id (viewer_id),
    INDEX idx_fv_viewed_at (viewed_at)
);
