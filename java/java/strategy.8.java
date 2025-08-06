package com.ultimatefuturesbot.strategies;

import java.util.Optional;

public class OrderBookPressureScalpingStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public OrderBookPressureScalpingStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "OrderBookPressureScalping";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Scalp in direction of persistent bid/ask pressure confirmed by tape speed & depth
        if (ip.isOrderBookPressureStrong(data) && ip.isTapeSpeedHigh(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.006));
        }
        return Optional.empty();
    }
}

public class AdaptiveATRVolatilityExpansionStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public AdaptiveATRVolatilityExpansionStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "AdaptiveATRVolatilityExpansion";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Trade breakouts only when ATR signals expanding volatility
        if (ip.isATRExpanding(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.008));
        }
        return Optional.empty();
    }
}

public class ScheduledEventVolatilityOverlayStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public ScheduledEventVolatilityOverlayStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "ScheduledEventVolatilityOverlay";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Apply volatility buffer around scheduled macro events like FOMC, CPI
        if (ip.isScheduledEventImminent() && ip.isVolatilityHigh(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.009));
        }
        return Optional.empty();
    }
}

public class ImpliedLiquidityMappingStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public ImpliedLiquidityMappingStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "ImpliedLiquidityMapping";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Use options open interest + order book to map hidden liquidity zones
        if (ip.isPriceNearLiquidityZone(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.007));
        }
        return Optional.empty();
    }
}
