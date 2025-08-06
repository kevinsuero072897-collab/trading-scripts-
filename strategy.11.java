public static class ScheduledEventVolatilityOverlayStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public ScheduledEventVolatilityOverlayStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "ScheduledEventVolatilityOverlay"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.isDuringScheduledEvent() && ip.isVolatilityHigh()) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.012));
        }
        return Optional.empty();
    }
}

public static class ImpliedLiquidityMappingStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public ImpliedLiquidityMappingStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "ImpliedLiquidityMapping"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.isPriceNearLiquidityZone() && ip.isVolumeSpike(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.009));
        }
        return Optional.empty();
    }
}

public static class RealTimeRiskParityAllocationStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public RealTimeRiskParityAllocationStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "RealTimeRiskParityAllocation"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.isRiskBalanced() && ip.isTrending(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.008));
        }
        return Optional.empty();
    }
}

public static class MicrostructureAnalyticsAlphaStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;
    public MicrostructureAnalyticsAlphaStrategy(IndicatorProcessor ip) { this.ip = ip; }
    public String getName() { return "MicrostructureAnalyticsAlpha"; }
    public Optional<TradeSignal> evaluate(MarketData data) {
        if (ip.detectsMicrostructureAlpha() && !ip.isChoppy(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.011));
        }
        return Optional.empty();
    }
}
