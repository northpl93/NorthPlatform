package pl.north93.northplatform.api.bukkit.world;

import java.util.function.Consumer;

import org.bukkit.World;

import pl.north93.northplatform.api.bukkit.utils.ISyncCallback;

public interface IWorldLoadCallback extends ISyncCallback
{
    World getWorld();

    default void onComplete(Consumer<World> task)
    {
        onComplete(() -> task.accept(getWorld()));
    }
}
