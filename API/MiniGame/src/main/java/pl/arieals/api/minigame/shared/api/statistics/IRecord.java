package pl.arieals.api.minigame.shared.api.statistics;

import java.util.UUID;

public interface IRecord
{
    UUID getOwner();

    long value();

    long recordedAt();
}
