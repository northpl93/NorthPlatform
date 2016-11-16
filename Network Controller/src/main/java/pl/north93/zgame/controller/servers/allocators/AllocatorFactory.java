package pl.north93.zgame.controller.servers.allocators;

import pl.north93.zgame.api.global.deployment.ServersAllocatorType;

public class AllocatorFactory
{
    public static final AllocatorFactory INSTANCE = new AllocatorFactory();

    public IAllocator getAllocator(final ServersAllocatorType allocatorType)
    {
        switch (allocatorType)
        {
            case STATIC:
                return new StaticAllocator();
            case PLAYER_COUNT:
                return new PlayerCountAllocator();
            case JOINING_POLICY:
                return null;
        }

        throw new IllegalArgumentException(allocatorType + " is not supported");
    }
}
