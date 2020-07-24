CREATE TABLE promotions
(
  id SERIAL PRIMARY KEY,
  uuid VARCHAR(50) NOT NULL,
  price DECIMAL(6, 2) NOT NULL,
  expiration_date TIMESTAMP NOT NULL
);