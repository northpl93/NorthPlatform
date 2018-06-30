package pl.arieals.minigame.bedwars.arena.generator;

import pl.arieals.minigame.bedwars.cfg.BwGeneratorItemConfig;

public interface IGeneratorHudHandler
{
    void tick(BwGeneratorItemConfig currentItem, int timer);

    void handleItemRotation();

    void markOverload();
}
