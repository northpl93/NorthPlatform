package pl.north93.northplatform.antycheat.analysis.reaction;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MappedPointsCondition implements TriggerCondition
{
    private final ViolationMapper mapper;
    private final double          points;

    public MappedPointsCondition(final ViolationMapper mapper, final double points)
    {
        this.mapper = mapper;
        this.points = points;
    }

    @Override
    public boolean isTriggered(final TriggerCheckContext context)
    {
        final double points = context.getViolations().stream().mapToDouble(this.mapper::getPoints).sum();

        return points > this.points;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mapper", this.mapper).append("points", this.points).toString();
    }
}
