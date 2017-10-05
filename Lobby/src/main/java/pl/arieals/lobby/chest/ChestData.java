package pl.arieals.lobby.chest;

import pl.north93.zgame.api.global.network.players.IPlayer;

/**
 * Klasa wrapujaca IPlayera sluzaca do zarzadzania iloscia skrzynek.
 */
class ChestData
{
    private final IPlayer player;

    public ChestData(final IPlayer player)
    {
        this.player = player;
    }

    public int getChests(final ChestType type)
    {
        return 5;
    }

    public void setChests(final ChestType type, final int newChests)
    {
        // todo
    }
}
