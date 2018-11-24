package pl.north93.northplatform.antycheat.analysis.reaction;

import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;

public interface ViolationMapper
{
    double getPoints(SingleAnalysisResult.ViolationEntry violationEntry);
}
