package pl.north93.northplatform.minigame.bedwars.arena.generator;

import pl.north93.northplatform.minigame.bedwars.cfg.BwGeneratorItemConfig;

public interface IGeneratorHudHandler
{
    void tick(BwGeneratorItemConfig currentItem, int timer);

    void handleItemRotation();

    void markOverload();
}
