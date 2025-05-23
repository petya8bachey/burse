-- Connect to the trading_system database
\c trading_system

-- Set the schema
SET search_path TO trading, public;

-- Clear existing data (optional - uncomment if needed)
-- TRUNCATE TABLE Broker, Client, Stock, TradingSession, Transaction,
--               Client_TradingSession, Client_Stock, Broker_Stock RESTART IDENTITY;

-- Insert sample brokers
INSERT INTO Broker (license_number, company_name, status) VALUES
('BRK-1001', 'Goldman Sachs', 'Active'),
('BRK-1002', 'Morgan Stanley', 'Active'),
('BRK-1003', 'J.P. Morgan', 'Active'),
('BRK-1004', 'Charles Schwab', 'Active'),
('BRK-1005', 'Fidelity Investments', 'Suspended'),
('BRK-1006', 'TD Ameritrade', 'Revoked');

-- Insert sample clients (individuals and corporations)
INSERT INTO Client (full_name, tax_id, client_type, broker_id, registration_date) VALUES
-- Goldman Sachs clients
('John Smith', 'TID-1001', 'Individual', 1, '2020-01-15'),
('Alice Johnson', 'TID-1002', 'Individual', 1, '2020-03-22'),
('Tech Solutions Inc', 'TID-2001', 'Corporate', 1, '2019-11-05'),

-- Morgan Stanley clients
('Robert Brown', 'TID-1003', 'Individual', 2, '2021-02-18'),
('Global Ventures LLC', 'TID-2002', 'Corporate', 2, '2020-07-30'),

-- J.P. Morgan clients
('Emily Davis', 'TID-1004', 'Individual', 3, '2021-05-10'),
('Michael Wilson', 'TID-1005', 'Individual', 3, '2021-06-15'),
('Innovate Corp', 'TID-2003', 'Corporate', 3, '2020-09-12'),

-- Charles Schwab clients
('Sarah Miller', 'TID-1006', 'Individual', 4, '2021-03-05'),
('Future Holdings', 'TID-2004', 'Corporate', 4, '2020-12-01');

-- Insert sample stocks
INSERT INTO Stock (company_name, sector, current_price, is_active) VALUES
('Apple Inc', 'Technology', 175.50, TRUE),
('Microsoft', 'Technology', 310.20, TRUE),
('Amazon', 'Consumer', 135.75, TRUE),
('Tesla', 'Automotive', 210.30, TRUE),
('JPMorgan Chase', 'Financial', 155.60, TRUE),
('Walmart', 'Retail', 160.40, TRUE),
('Pfizer', 'Healthcare', 42.80, TRUE),
('Boeing', 'Aerospace', 185.90, TRUE),
('Exxon Mobil', 'Energy', 102.30, TRUE),
('Netflix', 'Entertainment', 480.25, TRUE),
('Disney', 'Entertainment', 95.75, TRUE),
('Intel', 'Technology', 42.60, FALSE); -- Inactive stock

-- Insert trading sessions
INSERT INTO TradingSession (start_time, end_time, status) VALUES
-- Past sessions
('2023-10-01 09:30:00+00', '2023-10-01 16:00:00+00', 'Closed'),
('2023-10-02 09:30:00+00', '2023-10-02 16:00:00+00', 'Closed'),
('2023-10-03 09:30:00+00', '2023-10-03 16:00:00+00', 'Closed'),
-- Current session
('2023-10-04 09:30:00+00', '2023-10-04 16:00:00+00', 'Active'),
-- Future session
('2023-10-05 09:30:00+00', '2023-10-05 16:00:00+00', 'Planned');

-- Insert repository data
INSERT INTO Repository (last_update, storage_size_gb) VALUES
(CURRENT_TIMESTAMP, 250.75),
(CURRENT_TIMESTAMP - INTERVAL '1 day', 245.50),
(CURRENT_TIMESTAMP - INTERVAL '2 days', 240.25);

-- Insert sample transactions
INSERT INTO Transaction (stock_id, client_id, price, volume, direction, session_id, repo_id) VALUES
-- John Smith's transactions
(1, 1, 170.25, 10, 'BUY', 1, 1),
(2, 1, 305.50, 5, 'BUY', 1, 1),
(3, 1, 130.00, 8, 'BUY', 2, 1),
(1, 1, 172.75, 5, 'SELL', 3, 2),

-- Alice Johnson's transactions
(4, 2, 205.40, 15, 'BUY', 1, 1),
(5, 2, 150.25, 20, 'BUY', 1, 1),
(6, 2, 155.75, 10, 'BUY', 2, 1),

-- Tech Solutions Inc transactions
(7, 3, 40.50, 100, 'BUY', 1, 1),
(8, 3, 180.25, 50, 'BUY', 2, 1),
(9, 3, 100.75, 75, 'BUY', 3, 2),

-- Robert Brown's transactions
(10, 4, 475.50, 3, 'BUY', 1, 1),
(11, 4, 92.25, 10, 'BUY', 2, 1),

-- Global Ventures LLC transactions
(1, 5, 168.75, 50, 'BUY', 1, 1),
(2, 5, 300.00, 25, 'BUY', 1, 1),
(3, 5, 132.50, 30, 'BUY', 2, 1),

-- Emily Davis's transactions
(4, 6, 208.75, 8, 'BUY', 1, 1),
(5, 6, 152.25, 12, 'BUY', 2, 1),

-- Michael Wilson's transactions
(6, 7, 158.50, 15, 'BUY', 1, 1),
(7, 7, 41.75, 40, 'BUY', 2, 1),

-- Innovate Corp transactions
(8, 8, 182.50, 35, 'BUY', 1, 1),
(9, 8, 101.25, 60, 'BUY', 2, 1),
(10, 8, 478.00, 10, 'BUY', 3, 2);

-- Link clients to trading sessions
INSERT INTO Client_TradingSession (client_id, session_id) VALUES
-- All clients in session 1
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1),
-- Most clients in session 2
(1, 2), (2, 2), (3, 2), (4, 2), (5, 2), (6, 2), (7, 2), (8, 2),
-- Some clients in session 3
(1, 3), (3, 3), (8, 3), (10, 3),
-- Active clients in current session
(1, 4), (2, 4), (4, 4), (6, 4), (8, 4), (10, 4);

-- Link clients to their watched stocks
INSERT INTO Client_Stock (client_id, stock_id) VALUES
-- John Smith watches tech stocks
(1, 1), (1, 2), (1, 12),
-- Alice Johnson watches automotive and financial
(2, 4), (2, 5),
-- Tech Solutions watches tech and healthcare
(3, 1), (3, 2), (3, 7),
-- Robert Brown watches entertainment
(4, 10), (4, 11),
-- Global Ventures watches major companies
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5),
-- Emily Davis watches automotive and financial
(6, 4), (6, 5),
-- Michael Wilson watches retail and healthcare
(7, 6), (7, 7),
-- Innovate Corp watches various sectors
(8, 8), (8, 9), (8, 10),
-- Sarah Miller watches consumer and retail
(9, 3), (9, 6),
-- Future Holdings watches energy and tech
(10, 1), (10, 9);

-- Link brokers to stocks they handle
INSERT INTO Broker_Stock (broker_id, stock_id) VALUES
-- Goldman Sachs handles all stocks
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12),
-- Morgan Stanley handles most stocks
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 10), (2, 11),
-- J.P. Morgan handles financial and tech
(3, 1), (3, 2), (3, 5), (3, 12),
-- Charles Schwab handles all active stocks
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10), (4, 11);

-- Update some stock prices to show variation
UPDATE Stock SET current_price = 177.25, last_updated = CURRENT_TIMESTAMP WHERE stock_id = 1;
UPDATE Stock SET current_price = 312.75, last_updated = CURRENT_TIMESTAMP WHERE stock_id = 2;
UPDATE Stock SET current_price = 137.50, last_updated = CURRENT_TIMESTAMP WHERE stock_id = 3;
UPDATE Stock SET current_price = 208.90, last_updated = CURRENT_TIMESTAMP WHERE stock_id = 4;
UPDATE Stock SET current_price = 157.25, last_updated = CURRENT_TIMESTAMP WHERE stock_id = 5;

-- Display counts of inserted data
SELECT
    (SELECT COUNT(*) FROM Broker) AS broker_count,
    (SELECT COUNT(*) FROM Client) AS client_count,
    (SELECT COUNT(*) FROM Stock) AS stock_count,
    (SELECT COUNT(*) FROM TradingSession) AS session_count,
    (SELECT COUNT(*) FROM Transaction) AS transaction_count;