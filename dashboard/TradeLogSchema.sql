-- TradeLogSchema.sql
-- SQLite database schema for trading bot and dashboard
-- Defines tables for logging trades and trading mistakes

-- Enable foreign key constraints
PRAGMA foreign_keys = ON;

-- Trades table: logs all trading signals and their execution status
CREATE TABLE IF NOT EXISTS trades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    strategy_name TEXT NOT NULL,
    symbol TEXT NOT NULL,
    direction TEXT NOT NULL CHECK (direction IN ('LONG', 'SHORT')),
    entry_price REAL NOT NULL,
    stop_loss REAL,
    take_profit REAL,
    confidence REAL CHECK (confidence >= 0.0 AND confidence <= 1.0),
    risk_amount REAL,
    position_size REAL,
    status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'EXECUTED', 'CLOSED', 'CANCELLED')),
    entry_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    exit_timestamp DATETIME,
    exit_price REAL,
    profit_loss REAL,
    execution_type TEXT,
    metadata TEXT, -- JSON string for additional data
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Mistakes table: logs trading mistakes and lessons learned
CREATE TABLE IF NOT EXISTS mistakes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    trade_id INTEGER,
    mistake_type TEXT NOT NULL,
    description TEXT NOT NULL,
    financial_impact REAL,
    lesson_learned TEXT,
    category TEXT, -- e.g., 'ENTRY', 'EXIT', 'RISK_MANAGEMENT', 'STRATEGY', 'EMOTIONAL'
    severity TEXT DEFAULT 'MEDIUM' CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    identified_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved BOOLEAN DEFAULT 0,
    resolution_notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trade_id) REFERENCES trades(id) ON DELETE SET NULL
);

-- Indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_trades_strategy_symbol ON trades(strategy_name, symbol);
CREATE INDEX IF NOT EXISTS idx_trades_timestamp ON trades(entry_timestamp);
CREATE INDEX IF NOT EXISTS idx_trades_status ON trades(status);
CREATE INDEX IF NOT EXISTS idx_mistakes_trade_id ON mistakes(trade_id);
CREATE INDEX IF NOT EXISTS idx_mistakes_type ON mistakes(mistake_type);
CREATE INDEX IF NOT EXISTS idx_mistakes_category ON mistakes(category);

-- Triggers to automatically update the updated_at timestamp
CREATE TRIGGER IF NOT EXISTS update_trades_updated_at 
    AFTER UPDATE ON trades
BEGIN
    UPDATE trades SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS update_mistakes_updated_at 
    AFTER UPDATE ON mistakes
BEGIN
    UPDATE mistakes SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;

-- View for trade performance summary
CREATE VIEW IF NOT EXISTS trade_performance AS
SELECT 
    strategy_name,
    COUNT(*) as total_trades,
    COUNT(CASE WHEN profit_loss > 0 THEN 1 END) as winning_trades,
    COUNT(CASE WHEN profit_loss < 0 THEN 1 END) as losing_trades,
    ROUND(COUNT(CASE WHEN profit_loss > 0 THEN 1 END) * 100.0 / COUNT(*), 2) as win_rate,
    ROUND(AVG(profit_loss), 2) as avg_profit_loss,
    ROUND(SUM(profit_loss), 2) as total_profit_loss,
    ROUND(MAX(profit_loss), 2) as max_profit,
    ROUND(MIN(profit_loss), 2) as max_loss
FROM trades 
WHERE status = 'CLOSED' AND profit_loss IS NOT NULL
GROUP BY strategy_name;

-- View for mistake analysis
CREATE VIEW IF NOT EXISTS mistake_summary AS
SELECT 
    mistake_type,
    category,
    COUNT(*) as occurrence_count,
    ROUND(AVG(financial_impact), 2) as avg_financial_impact,
    ROUND(SUM(financial_impact), 2) as total_financial_impact,
    COUNT(CASE WHEN resolved = 1 THEN 1 END) as resolved_count,
    ROUND(COUNT(CASE WHEN resolved = 1 THEN 1 END) * 100.0 / COUNT(*), 2) as resolution_rate
FROM mistakes 
WHERE financial_impact IS NOT NULL
GROUP BY mistake_type, category
ORDER BY total_financial_impact DESC;