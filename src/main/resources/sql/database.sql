psql -U postgres
CREATE DATABASE burce;
CREATE SCHEMA IF NOT EXISTS app;
CREATE USER client WITH PASSWORD '123';
CREATE USER broker WITH PASSWORD '123';

-- Grant all privileges on the new database to the users
GRANT ALL PRIVILEGES ON DATABASE burce TO postgres; -- The main admin user
-- GRANT ALL PRIVILEGES ON DATABASE burce TO client;
-- GRANT ALL PRIVILEGES ON DATABASE burce TO broker;
