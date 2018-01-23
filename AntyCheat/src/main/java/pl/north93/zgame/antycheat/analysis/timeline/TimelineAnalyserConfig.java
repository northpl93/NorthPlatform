package pl.north93.zgame.antycheat.analysis.timeline;

import static pl.north93.zgame.antycheat.analysis.timeline.TimelineAnalyserConfig.Scope.ALL;

public final class TimelineAnalyserConfig
{
    private Scope scope = ALL;

    public Scope getScope()
    {
        return this.scope;
    }

    public void setScope(final Scope scope)
    {
        this.scope = scope;
    }

    public enum Scope
    {
        TICK,
        SECOND,
        FIVE_SECONDS,
        ALL
    }
}
