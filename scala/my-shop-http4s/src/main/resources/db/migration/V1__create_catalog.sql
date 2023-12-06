CREATE TABLE categories (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255),
  parent_id INT,
  description TEXT
);

CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255),
  description TEXT,
  category_id INT NOT NULL
);

CREATE TABLE skus (
  id SERIAL PRIMARY KEY,
  product_id INT,
  title VARCHAR(255),
  price DECIMAL(10,2),
  in_stock INT
);

CREATE TABLE product_images (
  id SERIAL PRIMARY KEY,
  sku_id INT,
  url_1 VARCHAR(1024),
  url_2 VARCHAR(1024),
  url_3 VARCHAR(1024),
  url_4 VARCHAR(1024),
  url_5 VARCHAR(1024),
  url_6 VARCHAR(1024),
  url_7 VARCHAR(1024),
  url_8 VARCHAR(1024),
  url_9 VARCHAR(1024),
  url_10 VARCHAR(1024)
);

CREATE TABLE customers (
  id SERIAL PRIMARY KEY,
  login VARCHAR(255),
  passwd VARBINARY(40),
  passwd_type VARCHAR(32),
  title VARCHAR(255)
);

CREATE TABLE addresses (
  id SERIAL PRIMARY KEY,
  customer_id INT,
  address_info VARCHAR(1024)
);

CREATE TABLE checkout (
  id SERIAL PRIMARY KEY,
  customer_id INT,
  created_date DATE,
  created_time TIME,
  address_id INT,
  status INT -- 1-OPENED, 2-SUBMITTED, 3-CONFIRMED, 4-PROCESSED, 5-SHIPPED
);

CREATE TABLE item_in_cart (
  id SERIAL PRIMARY KEY,
  checkout_id INT,
  sku_id INT,
  quantity INT
);

INSERT INTO customers(id, login, passwd, passwd_type, title) VALUES
(1, 'stas', X'e10adc3949ba59abbe56e057f20f883e', 'md5', 'Stanislav') -- passwd: 123456
;

INSERT INTO categories (id, title, parent_id, description) VALUES
(1,   'Electronic', NULL, 'All kinds of electronics'),
(101, 'Laptops',    1,    '')
;


INSERT INTO products (title, description, category_id) VALUES
('Thinkpad T590', 'Good laptop', 1)
;