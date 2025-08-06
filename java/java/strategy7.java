package com.ultimatefuturesbot.strategies;

import java.util.Optional;

public class AdaptiveBreakoutConfirmationStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public AdaptiveBreakoutConfirmationStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "AdaptiveBreakoutConfirmation";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Confirm breakout only if volume spike + order flow imbalance + volatility band
        if (ip.isVolumeSpike(data) && ip.isOrderFlowImbalance(data) && ip.isVolatilityExpanding(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.009));
        }
        return Optional.empty();
    }
}

public class SwingFailurePatternStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public SwingFailurePatternStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "SwingFailurePattern";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Detect failed swing high/low with volume confirmation
        if (ip.isSwingFailure(data) && ip.isVolumeConfirming(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.008));
        }
        return Optional.empty();
    }
}

public class RealTimeSentimentOverlayStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public RealTimeSentimentOverlayStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "RealTimeSentimentOverlay";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Use sentiment score to filter or reinforce trades
        if (ip.getSentimentScore(data) > 0.7 && ip.isTrending(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.007));
        }
        return Optional.empty();
    }
}

public class MultiAssetCorrelationBreakStrategy implements StrategyBlueprint {
    private final IndicatorProcessor ip;

    public MultiAssetCorrelationBreakStrategy(IndicatorProcessor ip) {
        this.ip = ip;
    }

    @Override
    public String getName() {
        return "MultiAssetCorrelationBreak";
    }

    @Override
    public Optional<TradeSignal> evaluate(MarketData data) {
        // Trade when asset correlation breaks beyond threshold
        if (ip.isCorrelationDiverging(data)) {
            return Optional.of(new TradeSignal(getName(), data.price, 0.009));
        }
        return Optional.empty();
    }
}
