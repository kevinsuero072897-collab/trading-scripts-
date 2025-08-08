# Dashboard

Trading dashboard and database components for logging and monitoring trading activities.

## Contents

- **`TradeLogSchema.sql`** - SQLite database schema for comprehensive trade logging and mistake tracking
- **`.gitignore`** - Git ignore rules for dashboard-specific files

## Database Schema

### Tables

#### `trades`
Comprehensive trade logging with the following key fields:
- **Trade Details**: strategy_name, symbol, direction (LONG/SHORT), entry/exit prices
- **Risk Management**: stop_loss, take_profit, confidence, risk_amount, position_size  
- **Status Tracking**: status (PENDING/EXECUTED/CLOSED/CANCELLED), timestamps
- **Performance**: profit_loss, execution_type
- **Metadata**: JSON string for additional custom data

#### `trading_mistakes`
Trading mistake logging system for performance improvement:
- **Mistake Tracking**: mistake_type, severity, description
- **Context**: strategy_name, symbol, associated trade_id
- **Analysis**: cause_analysis, lesson_learned, prevention_strategy
- **Progress**: resolved status, correction_actions

### Features

- **Foreign Key Constraints**: Enabled for data integrity
- **Data Validation**: CHECK constraints for direction, confidence, status, and severity
- **Automatic Timestamps**: created_at and updated_at fields
- **JSON Metadata Support**: Flexible additional data storage
- **Referential Integrity**: Mistakes linked to specific trades

## Usage

### Database Setup

1. Install SQLite3:
```bash
# Ubuntu/Debian
sudo apt-get install sqlite3

# macOS
brew install sqlite3

# Windows
# Download from https://sqlite.org/download.html
```

2. Create the database:
```bash
sqlite3 trading_bot.db < TradeLogSchema.sql
```

### Integration

The schema is designed to work with:
- Trading bot implementations in `../java/java/` and `../src/main/java/com/yourorg/tradingbot/`
- Dashboard applications for monitoring and analysis
- Performance tracking and mistake analysis tools

### Example Queries

```sql
-- View recent trades
SELECT * FROM trades ORDER BY entry_timestamp DESC LIMIT 10;

-- Calculate strategy performance
SELECT strategy_name, 
       COUNT(*) as total_trades,
       AVG(profit_loss) as avg_pnl,
       SUM(CASE WHEN profit_loss > 0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as win_rate
FROM trades 
WHERE status = 'CLOSED' 
GROUP BY strategy_name;

-- Analyze common mistakes
SELECT mistake_type, COUNT(*) as frequency, AVG(severity) as avg_severity
FROM trading_mistakes 
GROUP BY mistake_type 
ORDER BY frequency DESC;
```