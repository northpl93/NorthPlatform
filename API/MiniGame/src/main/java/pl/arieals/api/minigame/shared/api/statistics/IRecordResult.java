package pl.arieals.api.minigame.shared.api.statistics;

public interface IRecordResult
{
    IRecord getProcessedRecord();

    boolean isNewPersonalRecord();

    IRecord previousPersonal();

    boolean isNewGlobalRecord();

    IRecord previousGlobal();
}
