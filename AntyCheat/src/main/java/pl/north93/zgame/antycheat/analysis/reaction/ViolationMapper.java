package pl.north93.zgame.antycheat.analysis.reaction;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;

public interface ViolationMapper
{
    double getPoints(SingleAnalysisResult.ViolationEntry violationEntry);
}
