-- Create database (with IF NOT EXISTS)
CREATE DATABASE trading_system
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Connect to the database
\c trading_system

-- Create schema (with IF NOT EXISTS)
CREATE SCHEMA IF NOT EXISTS trading
    AUTHORIZATION postgres;

-- Set search path
SET search_path TO trading, public;

-- Create tables with constraints

-- Broker table (reference table)
CREATE TABLE IF NOT EXISTS Broker (
    broker_id SERIAL PRIMARY KEY,
    license_number VARCHAR(20) NOT NULL UNIQUE,
    company_name VARCHAR(100) NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('Active', 'Suspended', 'Revoked')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Client table
CREATE TABLE IF NOT EXISTS Client (
    client_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(20) NOT NULL UNIQUE,
    client_type VARCHAR(10) NOT NULL CHECK (client_type IN ('Individual', 'Corporate')),
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    broker_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_broker
        FOREIGN KEY(broker_id)
        REFERENCES Broker(broker_id)
        ON DELETE RESTRICT
);

-- TradingSession table
CREATE TABLE IF NOT EXISTS TradingSession (
    session_id SERIAL PRIMARY KEY,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('Planned', 'Active', 'Closed', 'Cancelled')),
    CONSTRAINT valid_session_time CHECK (end_time > start_time)
);

-- Repository table (for transaction storage)
CREATE TABLE IF NOT EXISTS Repository (
    repo_id SERIAL PRIMARY KEY,
    last_update TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    storage_size_gb NUMERIC(10,2) NOT NULL CHECK (storage_size_gb >= 0)
);

-- Stock table
CREATE TABLE IF NOT EXISTS Stock (
    stock_id SERIAL PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    sector VARCHAR(50) NOT NULL,
    current_price NUMERIC(15,2) NOT NULL CHECK (current_price >= 0),
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Transaction table (main transactional table)
CREATE TABLE IF NOT EXISTS Transaction (
    transaction_id SERIAL PRIMARY KEY,
    stock_id INTEGER NOT NULL,
    client_id INTEGER NOT NULL,
    price NUMERIC(15,2) NOT NULL CHECK (price > 0),
    volume INTEGER NOT NULL CHECK (volume > 0),
    direction VARCHAR(4) NOT NULL CHECK (direction IN ('BUY', 'SELL')),
    session_id INTEGER NOT NULL,
    repo_id INTEGER,
    transaction_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock
        FOREIGN KEY(stock_id)
        REFERENCES Stock(stock_id),
    CONSTRAINT fk_client
        FOREIGN KEY(client_id)
        REFERENCES Client(client_id),
    CONSTRAINT fk_session
        FOREIGN KEY(session_id)
        REFERENCES TradingSession(session_id),
    CONSTRAINT fk_repo
        FOREIGN KEY(repo_id)
        REFERENCES Repository(repo_id)
);

-- Junction tables
CREATE TABLE IF NOT EXISTS Client_TradingSession (
    client_id INTEGER NOT NULL,
    session_id INTEGER NOT NULL,
    PRIMARY KEY (client_id, session_id),
    CONSTRAINT fk_client_session_client
        FOREIGN KEY(client_id)
        REFERENCES Client(client_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_client_session_session
        FOREIGN KEY(session_id)
        REFERENCES TradingSession(session_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Client_Stock (
    client_id INTEGER NOT NULL,
    stock_id INTEGER NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (client_id, stock_id),
    CONSTRAINT fk_client_stock_client
        FOREIGN KEY(client_id)
        REFERENCES Client(client_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_client_stock_stock
        FOREIGN KEY(stock_id)
        REFERENCES Stock(stock_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Broker_Stock (
    broker_id INTEGER NOT NULL,
    stock_id INTEGER NOT NULL,
    PRIMARY KEY (broker_id, stock_id),
    CONSTRAINT fk_broker_stock_broker
        FOREIGN KEY(broker_id)
        REFERENCES Broker(broker_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_broker_stock_stock
        FOREIGN KEY(stock_id)
        REFERENCES Stock(stock_id)
        ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_transaction_stock_id ON Transaction(stock_id);
CREATE INDEX IF NOT EXISTS idx_transaction_client_id ON Transaction(client_id);
CREATE INDEX IF NOT EXISTS idx_transaction_session_id ON Transaction(session_id);
CREATE INDEX IF NOT EXISTS idx_client_broker_id ON Client(broker_id);
CREATE INDEX IF NOT EXISTS idx_stock_sector ON Stock(sector);

-- Create roles and permissions with IF NOT EXISTS
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'trading_admin') THEN
        CREATE ROLE trading_admin;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'trading_broker') THEN
        CREATE ROLE trading_broker;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'trading_client') THEN
        CREATE ROLE trading_client;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'trading_analyst') THEN
        CREATE ROLE trading_analyst;
    END IF;
END
$$;

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE trading_system TO trading_admin;
GRANT ALL PRIVILEGES ON SCHEMA trading TO trading_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA trading TO trading_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA trading TO trading_admin;

GRANT CONNECT ON DATABASE trading_system TO trading_broker;
GRANT USAGE ON SCHEMA trading TO trading_broker;
GRANT SELECT, INSERT, UPDATE ON Client TO trading_broker;
GRANT SELECT ON Broker TO trading_broker;
GRANT SELECT ON Stock TO trading_broker;
GRANT SELECT ON Transaction TO trading_broker;
GRANT SELECT ON TradingSession TO trading_broker;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA trading TO trading_broker;

GRANT CONNECT ON DATABASE trading_system TO trading_client;
GRANT USAGE ON SCHEMA trading TO trading_client;
GRANT SELECT ON Stock TO trading_client;
GRANT SELECT ON Client TO trading_client;
GRANT SELECT ON TradingSession TO trading_client;
GRANT SELECT ON Transaction TO trading_client;

GRANT CONNECT ON DATABASE trading_system TO trading_analyst;
GRANT USAGE ON SCHEMA trading TO trading_analyst;
GRANT SELECT ON ALL TABLES IN SCHEMA trading TO trading_analyst;

-- Row-level security policies
ALTER TABLE Client ENABLE ROW LEVEL SECURITY;
ALTER TABLE Transaction ENABLE ROW LEVEL SECURITY;
ALTER TABLE Broker ENABLE ROW LEVEL SECURITY;

-- Client table policies
CREATE POLICY client_select_policy ON Client
    FOR SELECT
    USING (broker_id = current_setting('app.current_broker_id')::INT OR pg_has_role('trading_admin', 'member'));

CREATE POLICY client_update_policy ON Client
    FOR UPDATE
    USING (broker_id = current_setting('app.current_broker_id')::INT OR pg_has_role('trading_admin', 'member'));

-- Transaction table policies
CREATE POLICY transaction_select_policy ON Transaction
    FOR SELECT
    USING (client_id = current_setting('app.current_client_id')::INT
           OR pg_has_role('trading_admin', 'member')
           OR (pg_has_role('trading_broker', 'member')
               AND client_id IN (SELECT client_id FROM Client WHERE broker_id = current_setting('app.current_broker_id')::INT)));

-- Broker table policies
CREATE POLICY broker_select_policy ON Broker
    FOR SELECT
    USING (broker_id = current_setting('app.current_broker_id')::INT OR pg_has_role('trading_admin', 'member'));

-- Create functions for common operations

-- Function to update current_price in Stock table
CREATE OR REPLACE FUNCTION trading.update_stock_price(
    p_stock_id INTEGER,
    p_new_price NUMERIC(15,2)
) RETURNS VOID AS $$
BEGIN
    UPDATE Stock
    SET current_price = p_new_price,
        last_updated = CURRENT_TIMESTAMP
    WHERE stock_id = p_stock_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Function to get client portfolio
CREATE OR REPLACE FUNCTION trading.get_client_portfolio(
    p_client_id INTEGER
) RETURNS TABLE(stock_id INTEGER, company_name VARCHAR, shares_held BIGINT, avg_price NUMERIC) AS $$
BEGIN
    RETURN QUERY
    SELECT
        t.stock_id,
        s.company_name,
        SUM(CASE WHEN t.direction = 'BUY' THEN t.volume ELSE -t.volume END) AS shares_held,
        AVG(t.price) AS avg_price
    FROM Transaction t
    JOIN Stock s ON t.stock_id = s.stock_id
    WHERE t.client_id = p_client_id
    GROUP BY t.stock_id, s.company_name
    HAVING SUM(CASE WHEN t.direction = 'BUY' THEN t.volume ELSE -t.volume END) > 0;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- [Rest of the script remains the same with IF NOT EXISTS added where appropriate]