# Trading Engine

Core trading engine implementation with simplified architecture for demonstration and professional-grade features.

## Contents

### Source Files

- **`Main.java`** - Main entry point for the professional trading system with backward compatibility
- **`SimpleTradingEngine.java`** - Core trading engine with advanced architecture, strategy management, and performance metrics
- **`SimpleCoreStructures.java`** - Core data structures and foundational classes
- **`StrategyA.java`** - Example trading strategy implementation

### Compiled Classes

The directory also contains compiled `.class` files for all Java sources, including additional strategy implementations:
- `AdvancedTrendStrategy.class`
- `BaseStrategy.class` 
- `ProfessionalBreakoutStrategy.class`
- `SmartExecutionStrategy.class`
- `VolatilityStrategy.class`
- And various supporting data structure classes

## Features

### Trading Engine Core
- **Singleton Pattern**: Thread-safe singleton instance management
- **Concurrent Execution**: Multi-threaded strategy execution with `ScheduledExecutorService`
- **Strategy Management**: Dynamic strategy loading and management
- **Performance Metrics**: Real-time performance tracking and monitoring
- **Professional Logging**: Comprehensive logging system with proper log levels

### Architecture Highlights
- **Backward Compatibility**: Maintains compatibility with legacy implementations
- **Advanced Risk Management**: Integrated risk management and position sizing
- **Real-time Processing**: Concurrent market data processing and strategy execution
- **Extensible Design**: Plugin-based strategy architecture for easy extension

## Usage

### Compilation

```bash
javac *.java
```

### Running

```bash
java Main
```

### Adding New Strategies

1. Implement the `Strategy` interface
2. Add your strategy to the engine using the strategy management system
3. The engine will automatically execute your strategy according to the configured schedule

## Integration

This trading engine integrates with:
- **Database Logging**: Uses the schema defined in `../dashboard/TradeLogSchema.sql`
- **Token Services**: Integrates with `../aside/topstep-token-service/` for authentication
- **Advanced Features**: Works with the sophisticated implementations in `../src/main/java/com/yourorg/tradingbot/`

## Development

### Key Classes

- `SimpleTradingEngine`: Main engine orchestration
- `Strategy`: Base interface for all trading strategies  
- `MarketData`: Market data representation
- `TradeSignal`: Signal generation and management
- `PerformanceMetrics`: Real-time performance tracking

### Thread Safety

The engine is designed to be thread-safe with:
- Concurrent collections for shared data
- Proper synchronization for critical sections
- Atomic operations for performance metrics
- Immutable data structures where appropriate