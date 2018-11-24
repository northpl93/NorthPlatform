package pl.north93.northplatform.antycheat.analysis.reaction;

import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;

public class DefaultViolationMapper implements ViolationMapper
{
    @Override
    public double getPoints(final SingleAnalysisResult.ViolationEntry violationEntry)
    {
        switch (violationEntry.getFalsePositiveProbability())
        {
            case LOW:
                return 2;
            case MEDIUM:
                return 1;
            case HIGH:
                return 0.25;
        }
        return 0;
    }
}
