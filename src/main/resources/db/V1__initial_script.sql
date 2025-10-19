CREATE TABLE supplier (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(50)
);

CREATE TABLE dogs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200),
    breed VARCHAR(200),
    badge_id VARCHAR(200),
    gender VARCHAR(10),
    birth_date DATE,
    date_acquired DATE,
    current_status VARCHAR(50),
    leaving_date DATE,
    leaving_reason VARCHAR(50),
    kennelling_characteristic VARCHAR(500),
    deleted BOOLEAN DEFAULT FALSE,
    supplier_id INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

