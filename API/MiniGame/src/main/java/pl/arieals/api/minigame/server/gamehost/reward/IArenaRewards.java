package pl.arieals.api.minigame.server.gamehost.reward;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.zgame.api.global.network.players.Identity;

/**
 * Interfejs dostępowy do systemu nagród powiązany z daną areną.
 * Przechowuje rankingi sluzace do obliczania nagrod, liste nagrod.
 */
public interface IArenaRewards
{
    /**
     * @return arena powiazana z tym obiektem.
     */
    LocalArena getArena();

    void addReward(Identity identity, IReward reward);

    Collection<IReward> getRewardsOf(Player player);

    Map<String, List<IReward>> groupRewardsOf(Player player);
}
