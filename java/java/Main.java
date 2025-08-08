import java.util.logging.Logger;

/**
 * Main entry point for the event-driven trading system
 * 
 * Demonstrates live, API-ready trading bot suitable for prop firm integration
 */
public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        try {
            System.out.println("Starting Event-Driven Trading Bot...");
            logger.info("Initializing live trading system for prop firm API integration");
            
            // Initialize the event-driven trading engine
            SimpleTradingEngine engine = SimpleTradingEngine.getInstance();
            engine.initialize();
            
            // Start the trading operations (ready for market data)
            engine.start();
            
            // Legacy compatibility - run simple strategy examples
            runLegacyStrategies();
            
            System.out.println("Event-Driven Trading Bot ready for live market data!");
            
            // Demonstrate the event-driven features
            demonstrateEventDrivenFeatures(engine);
            
            // Setup graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down event-driven trading system...");
                engine.stop();
                System.out.println("Trading Bot stopped safely with all positions closed.");
            }));
            
            // Demonstrate event-driven market data processing
            demonstrateMarketDataProcessing(engine);
            
        } catch (Exception e) {
            logger.severe("Failed to start event-driven trading system: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Run legacy strategies for backward compatibility
     */
    private static void runLegacyStrategies() {
        try {
            logger.info("Running legacy strategy examples for compatibility...");
            
            // Example: Run StrategyA (legacy compatibility)
            StrategyA stratA = new StrategyA();
            stratA.run();
            
            System.out.println("Legacy strategies executed successfully.");
            
        } catch (Exception e) {
            logger.warning("Error running legacy strategies: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrate the event-driven features of the system
     */
    private static void demonstrateEventDrivenFeatures(SimpleTradingEngine engine) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("EVENT-DRIVEN TRADING SYSTEM - LIVE API INTEGRATION");
        System.out.println("=".repeat(60));
        
        System.out.println("✓ Event-Driven Architecture:");
        System.out.println("  - Real-time market data processing via processMarketData()");
        System.out.println("  - No timer-based simulation or demo loops");
        System.out.println("  - Ready for prop firm API integration");
        System.out.println("  - Instant strategy evaluation on new data");
        
        System.out.println("\n✓ Multi-Timeframe Analysis:");
        System.out.println("  - Simultaneous analysis across M1, M5, M15, M30, H1, H4, D1 timeframes");
        System.out.println("  - Trend alignment confirmation across multiple timeframes");
        System.out.println("  - Higher timeframe bias with lower timeframe entries");
        
        System.out.println("\n✓ Advanced Risk Management:");
        System.out.println("  - Dynamic position sizing based on volatility and correlation");
        System.out.println("  - Portfolio-level risk controls and drawdown limits");
        System.out.println("  - Kelly Criterion position sizing with confidence weighting");
        System.out.println("  - Real-time correlation-based exposure management");
        
        System.out.println("\n✓ Sophisticated Strategy Implementations:");
        for (String strategyName : engine.getStrategyNames()) {
            System.out.println("  - " + strategyName + " with institutional-grade logic");
        }
        
        System.out.println("\n✓ Live Market Data Processing:");
        System.out.println("  - Event-driven market data evaluation");
        System.out.println("  - Real-time order book analysis");
        System.out.println("  - Volatility regime detection and adaptation");
        System.out.println("  - Correlation analysis across multiple instruments");
        
        System.out.println("\n✓ Professional Execution & Risk Controls:");
        System.out.println("  - TWAP execution algorithms ready");
        System.out.println("  - Real-time risk validation");
        System.out.println("  - Position concentration limits");
        System.out.println("  - Performance metrics tracking");
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("System Status: " + engine.getSystemStatus());
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Demonstrate event-driven market data processing
     * Shows how the bot processes real market data when it arrives
     */
    private static void demonstrateMarketDataProcessing(SimpleTradingEngine engine) {
        System.out.println("Demonstrating event-driven market data processing...\n");
        
        // Create sample market data that would come from a real API
        MarketDataSnapshot sampleData1 = createSampleMarketData("ES", 4520.50);
        MarketDataSnapshot sampleData2 = createSampleMarketData("ES", 4525.75);
        MarketDataSnapshot sampleData3 = createSampleMarketData("ES", 4518.25);
        
        System.out.println("Processing market data tick 1...");
        engine.processMarketData(sampleData1);
        engine.updatePerformanceMetrics();
        System.out.println("Current metrics: " + engine.getPerformanceMetrics());
        
        System.out.println("\nProcessing market data tick 2...");
        engine.processMarketData(sampleData2);
        engine.updatePerformanceMetrics();
        System.out.println("Current metrics: " + engine.getPerformanceMetrics());
        
        System.out.println("\nProcessing market data tick 3...");
        engine.processMarketData(sampleData3);
        engine.updatePerformanceMetrics();
        System.out.println("Current metrics: " + engine.getPerformanceMetrics());
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Event-driven processing demonstration complete!");
        System.out.println("Bot is ready to process real market data from prop firm API");
        System.out.println("=".repeat(60));
    }
    
    /**
     * Create sample market data for demonstration
     * In production, this would come from the actual API
     */
    private static MarketDataSnapshot createSampleMarketData(String symbol, double basePrice) {
        MarketDataSnapshot snapshot = new MarketDataSnapshot(symbol, java.time.LocalDateTime.now());
        
        // Create realistic OHLCV data
        OHLCV currentBar = new OHLCV(
            basePrice - 1, basePrice + 2, basePrice - 2, basePrice,
            1500L, java.time.LocalDateTime.now()
        );
        
        snapshot.getTimeFrameData().put(TimeFrame.M1, currentBar);
        snapshot.getTimeFrameData().put(TimeFrame.M5, currentBar);
        snapshot.getTimeFrameData().put(TimeFrame.M15, currentBar);
        
        // Add order book data
        snapshot.getOrderBook().addBid(basePrice - 0.25, 100);
        snapshot.getOrderBook().addAsk(basePrice + 0.25, 100);
        
        // Set volatility metrics
        snapshot.getVolatility().setRealizedVolatility(0.18);
        snapshot.getVolatility().setVolatilityRank(0.65);
        snapshot.getVolatility().setHighVolatilityRegime(true);
        
        return snapshot;
    }
}
