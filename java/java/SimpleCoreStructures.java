import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core data structures for the trading system (package-private for compilation)
 */

/**
 * Multi-timeframe market data snapshot
 */
class MarketDataSnapshot {
    private final String symbol;
    private final LocalDateTime timestamp;
    private final Map<TimeFrame, OHLCV> timeFrameData;
    private final OrderBookSnapshot orderBook;
    private final Map<String, Double> correlations;
    private final VolatilityMetrics volatility;
    
    public MarketDataSnapshot(String symbol, LocalDateTime timestamp) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.timeFrameData = new ConcurrentHashMap<>();
        this.orderBook = new OrderBookSnapshot();
        this.correlations = new ConcurrentHashMap<>();
        this.volatility = new VolatilityMetrics();
    }
    
    public String getSymbol() { return symbol; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<TimeFrame, OHLCV> getTimeFrameData() { return timeFrameData; }
    public OrderBookSnapshot getOrderBook() { return orderBook; }
    public Map<String, Double> getCorrelations() { return correlations; }
    public VolatilityMetrics getVolatility() { return volatility; }
    
    public OHLCV getData(TimeFrame timeFrame) {
        return timeFrameData.get(timeFrame);
    }
    
    public double getCurrentPrice() {
        OHLCV current = timeFrameData.get(TimeFrame.M1);
        return current != null ? current.close : 0.0;
    }
}

/**
 * OHLCV data structure
 */
class OHLCV {
    public final double open;
    public final double high;
    public final double low;
    public final double close;
    public final long volume;
    public final LocalDateTime timestamp;
    
    public OHLCV(double open, double high, double low, double close, long volume, LocalDateTime timestamp) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.timestamp = timestamp;
    }
    
    public double getTypicalPrice() {
        return (high + low + close) / 3.0;
    }
    
    public double getTrueRange(OHLCV previous) {
        if (previous == null) return high - low;
        
        double tr1 = high - low;
        double tr2 = Math.abs(high - previous.close);
        double tr3 = Math.abs(low - previous.close);
        
        return Math.max(tr1, Math.max(tr2, tr3));
    }
    
    public boolean isGreenCandle() {
        return close > open;
    }
    
    public double getBodySize() {
        return Math.abs(close - open);
    }
    
    public double getUpperWick() {
        return high - Math.max(open, close);
    }
    
    public double getLowerWick() {
        return Math.min(open, close) - low;
    }
}

/**
 * Time frame enumeration
 */
enum TimeFrame {
    M1(1), M5(5), M15(15), M30(30), H1(60), H4(240), D1(1440);
    
    private final int minutes;
    
    TimeFrame(int minutes) {
        this.minutes = minutes;
    }
    
    public int getMinutes() { return minutes; }
    
    public long getMilliseconds() {
        return minutes * 60L * 1000L;
    }
}

/**
 * Order book snapshot for microstructure analysis
 */
class OrderBookSnapshot {
    private final TreeMap<Double, Long> bids = new TreeMap<>(Collections.reverseOrder());
    private final TreeMap<Double, Long> asks = new TreeMap<>();
    private double spread;
    private double imbalance;
    private long totalBidVolume;
    private long totalAskVolume;
    
    public void addBid(double price, long volume) {
        bids.put(price, volume);
        totalBidVolume += volume;
        updateMetrics();
    }
    
    public void addAsk(double price, long volume) {
        asks.put(price, volume);
        totalAskVolume += volume;
        updateMetrics();
    }
    
    private void updateMetrics() {
        if (!bids.isEmpty() && !asks.isEmpty()) {
            double bestBid = bids.firstKey();
            double bestAsk = asks.firstKey();
            spread = bestAsk - bestBid;
            
            // Calculate order flow imbalance
            long topLevelBids = bids.getOrDefault(bestBid, 0L);
            long topLevelAsks = asks.getOrDefault(bestAsk, 0L);
            long total = topLevelBids + topLevelAsks;
            imbalance = total > 0 ? (double)(topLevelBids - topLevelAsks) / total : 0.0;
        }
    }
    
    public double getBestBid() { return bids.isEmpty() ? 0.0 : bids.firstKey(); }
    public double getBestAsk() { return asks.isEmpty() ? 0.0 : asks.firstKey(); }
    public double getSpread() { return spread; }
    public double getImbalance() { return imbalance; }
    public double getSpreadBps() { return getBestBid() > 0 ? (spread / getBestBid()) * 10000 : 0; }
}

/**
 * Volatility metrics for regime detection
 */
class VolatilityMetrics {
    private double realizedVolatility;
    private double impliedVolatility;
    private double garchVolatility;
    private double volatilityRank;
    private boolean highVolatilityRegime;
    private double volatilitySkew;
    
    public double getRealizedVolatility() { return realizedVolatility; }
    public void setRealizedVolatility(double vol) { this.realizedVolatility = vol; }
    
    public double getImpliedVolatility() { return impliedVolatility; }
    public void setImpliedVolatility(double vol) { this.impliedVolatility = vol; }
    
    public double getGarchVolatility() { return garchVolatility; }
    public void setGarchVolatility(double vol) { this.garchVolatility = vol; }
    
    public double getVolatilityRank() { return volatilityRank; }
    public void setVolatilityRank(double rank) { this.volatilityRank = rank; }
    
    public boolean isHighVolatilityRegime() { return highVolatilityRegime; }
    public void setHighVolatilityRegime(boolean regime) { this.highVolatilityRegime = regime; }
    
    public double getVolatilitySkew() { return volatilitySkew; }
    public void setVolatilitySkew(double skew) { this.volatilitySkew = skew; }
}

/**
 * Trade signal from strategies
 */
class TradeSignal {
    private final String strategyName;
    private final String symbol;
    private final TradeDirection direction;
    private final double entryPrice;
    private final double stopLoss;
    private final double takeProfit;
    private final double confidence;
    private final double riskAmount;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    
    public TradeSignal(String strategyName, String symbol, TradeDirection direction, 
                      double entryPrice, double stopLoss, double takeProfit, 
                      double confidence, double riskAmount) {
        this.strategyName = strategyName;
        this.symbol = symbol;
        this.direction = direction;
        this.entryPrice = entryPrice;
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
        this.confidence = confidence;
        this.riskAmount = riskAmount;
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }
    
    // Getters
    public String getStrategyName() { return strategyName; }
    public String getSymbol() { return symbol; }
    public TradeDirection getDirection() { return direction; }
    public double getEntryPrice() { return entryPrice; }
    public double getStopLoss() { return stopLoss; }
    public double getTakeProfit() { return takeProfit; }
    public double getConfidence() { return confidence; }
    public double getRiskAmount() { return riskAmount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public double getRiskRewardRatio() {
        double risk = Math.abs(entryPrice - stopLoss);
        double reward = Math.abs(takeProfit - entryPrice);
        return risk > 0 ? reward / risk : 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("TradeSignal[%s: %s %s @ %.2f, SL: %.2f, TP: %.2f, RR: %.2f, Conf: %.2f]",
                strategyName, direction, symbol, entryPrice, stopLoss, takeProfit, 
                getRiskRewardRatio(), confidence);
    }
}

/**
 * Trade direction enumeration
 */
enum TradeDirection {
    LONG, SHORT
}

/**
 * Optimized trade after portfolio optimization
 */
class OptimizedTrade {
    private final TradeSignal originalSignal;
    private final double optimizedSize;
    private final double adjustedEntryPrice;
    private final double portfolioWeight;
    private final String executionStrategy;
    
    public OptimizedTrade(TradeSignal signal, double size, double entryPrice, 
                         double weight, String executionStrategy) {
        this.originalSignal = signal;
        this.optimizedSize = size;
        this.adjustedEntryPrice = entryPrice;
        this.portfolioWeight = weight;
        this.executionStrategy = executionStrategy;
    }
    
    public TradeSignal getOriginalSignal() { return originalSignal; }
    public double getOptimizedSize() { return optimizedSize; }
    public double getAdjustedEntryPrice() { return adjustedEntryPrice; }
    public double getPortfolioWeight() { return portfolioWeight; }
    public String getExecutionStrategy() { return executionStrategy; }
}

/**
 * Strategy interface for implementation
 */
interface Strategy {
    String getName();
    Optional<TradeSignal> evaluate(MarketDataSnapshot snapshot);
    void initialize();
    void reset();
    Map<String, Object> getParameters();
    Map<String, Object> getMetrics();
    boolean isEnabled();
    void setEnabled(boolean enabled);
}

/**
 * Base strategy implementation with common functionality
 */
abstract class BaseStrategy implements Strategy {
    protected final String name;
    protected boolean enabled = true;
    protected final Map<String, Object> parameters = new ConcurrentHashMap<>();
    protected final Map<String, Object> metrics = new ConcurrentHashMap<>();
    
    protected BaseStrategy(String name) {
        this.name = name;
        // Note: initialize() will be called by implementing classes
    }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
    
    @Override
    public Map<String, Object> getMetrics() { return new HashMap<>(metrics); }
    
    @Override
    public boolean isEnabled() { return enabled; }
    
    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    @Override
    public void reset() {
        metrics.clear();
        metrics.put("signals_generated", 0);
        metrics.put("last_reset", LocalDateTime.now());
    }
    
    protected void incrementSignalCount() {
        metrics.merge("signals_generated", 1, (a, b) -> (Integer) a + (Integer) b);
    }
    
    protected double calculateATR(List<OHLCV> data, int period) {
        if (data.size() < period + 1) return 0.0;
        
        double sum = 0.0;
        for (int i = data.size() - period; i < data.size(); i++) {
            OHLCV current = data.get(i);
            OHLCV previous = i > 0 ? data.get(i - 1) : null;
            sum += current.getTrueRange(previous);
        }
        return sum / period;
    }
    
    protected double calculateEMA(List<Double> values, int period) {
        if (values.isEmpty()) return 0.0;
        
        double multiplier = 2.0 / (period + 1);
        double ema = values.get(0);
        
        for (int i = 1; i < values.size(); i++) {
            ema = (values.get(i) * multiplier) + (ema * (1 - multiplier));
        }
        return ema;
    }
    
    protected double calculateRSI(List<OHLCV> data, int period) {
        if (data.size() < period + 1) return 50.0;
        
        double avgGain = 0.0;
        double avgLoss = 0.0;
        
        // Calculate initial average gain and loss
        for (int i = 1; i <= period; i++) {
            double change = data.get(i).close - data.get(i - 1).close;
            if (change > 0) {
                avgGain += change;
            } else {
                avgLoss -= change;
            }
        }
        avgGain /= period;
        avgLoss /= period;
        
        if (avgLoss == 0) return 100.0;
        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }
    
    protected boolean isMarketOpen() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));
        int hour = now.getHour();
        int minute = now.getMinute();
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        // Monday to Friday, 9:30 AM to 4:00 PM EST
        return dayOfWeek >= 1 && dayOfWeek <= 5 && 
               ((hour == 9 && minute >= 30) || (hour > 9 && hour < 16));
    }
}