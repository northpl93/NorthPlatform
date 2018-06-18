package pl.north93.zgame.antycheat.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SingleAnalysisResult
{
    public static final SingleAnalysisResult EMPTY = new SingleAnalysisResult();
    private final Set<ViolationEntry> violations = new HashSet<>();

    private SingleAnalysisResult()
    {
    }

    public static SingleAnalysisResult create()
    {
        return new SingleAnalysisResult();
    }

    public void addViolation(final Violation violation, final String description)
    {
        this.violations.add(new ViolationEntry(violation, description));
    }

    public void addViolation(final Violation violation, final String description, final FalsePositiveProbability falsePositiveProbability)
    {
        this.violations.add(new ViolationEntry(violation, description, falsePositiveProbability));
    }

    public Collection<ViolationEntry> getViolations()
    {
        return Collections.unmodifiableCollection(this.violations);
    }

    public boolean isEmpty()
    {
        return this.violations.isEmpty();
    }

    public final class ViolationEntry
    {
        private final Violation                violation;
        private final String                   description;
        private final FalsePositiveProbability falsePositiveProbability;

        public ViolationEntry(final Violation violation, final String description, final FalsePositiveProbability falsePositiveProbability)
        {
            this.violation = violation;
            this.description = description;
            this.falsePositiveProbability = falsePositiveProbability;
        }

        public ViolationEntry(final Violation violation, final String description)
        {
            this(violation, description, FalsePositiveProbability.MEDIUM);
        }

        public Violation getViolation()
        {
            return this.violation;
        }

        public String getDescription()
        {
            return this.description;
        }

        public FalsePositiveProbability getFalsePositiveProbability()
        {
            return this.falsePositiveProbability;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("violation", this.violation).append("description", this.description).append("falsePositiveProbability", this.falsePositiveProbability).toString();
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("violations", this.violations).toString();
    }
}
