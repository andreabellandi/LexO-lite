CREATE DATABASE LexO_users CHARACTER SET utf8 COLLATE utf8_bin;

use LexO_users;

CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON LexO_users.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
