CREATE TABLE workflow_items (
    identifier BIGINT AUTO_INCREMENT PRIMARY KEY,
    headline VARCHAR(255),
    narrative VARCHAR(255),
    phase VARCHAR(50),
    initiated_at TIMESTAMP,
    modified_at TIMESTAMP
);