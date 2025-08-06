public static class RealTimeSentimentOverlayStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public RealTimeSentimentOverlayStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "RealTimeSentimentOverlay"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.getSentimentScore() > 0.7 && ip.isTrending(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.009));
        }
        if (ip.getSentimentScore() < 0.3 && ip.isChoppy(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.008));
        }
        return Optional.empty();
    }
}

public static class MultiAssetCorrelationBreakStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public MultiAssetCorrelationBreakStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "MultiAssetCorrelationBreak"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.isCorrelationDiverging() && ip.isTrending(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.007));
        }
        return Optional.empty();
    }
}

public static class OrderBookPressureScalpingStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public OrderBookPressureScalpingStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "OrderBookPressureScalping"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.isPersistentBidPressure() && !ip.isChoppy(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.006));
        }
        return Optional.empty();
    }
}

public static class AdaptiveATRVolatilityExpansionStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public AdaptiveATRVolatilityExpansionStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "AdaptiveATRVolatilityExpansion"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.isATRExpanding() && ip.isTrending(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.01));
        }
        return Optional.empty();
    }
}
