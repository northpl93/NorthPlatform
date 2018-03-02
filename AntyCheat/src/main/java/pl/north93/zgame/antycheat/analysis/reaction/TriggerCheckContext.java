package pl.north93.zgame.antycheat.analysis.reaction;

import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;

/**
 * Kontekst przekazywany do {@link TriggerCondition} w celu sprawdzenia czy
 * dany poziom jest aktualnie wywołany czy nie.
 */
public class TriggerCheckContext
{
    private final Collection<SingleAnalysisResult.ViolationEntry> violations;

    public TriggerCheckContext(final Collection<SingleAnalysisResult.ViolationEntry> violations)
    {
        this.violations = violations;
    }

    public Collection<SingleAnalysisResult.ViolationEntry> getViolations()
    {
        return this.violations;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("violations", this.violations).toString();
    }
}
