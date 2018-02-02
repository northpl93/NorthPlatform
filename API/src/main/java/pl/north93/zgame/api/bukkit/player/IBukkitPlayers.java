package pl.north93.zgame.api.bukkit.player;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Komponent pomocniczy umożliwiający używanie Bukkitowych interfejsow
 * wraz z danymi z systemu North Platform.
 */
public interface IBukkitPlayers
{
    OfflinePlayer getBukkitOfflinePlayer(UUID uuid);

    OfflinePlayer getBukkitOfflinePlayer(String nick);

    /**
     * Wrapuje podany obiekt gracza w INorthPlayer.
     *
     * @param player Obiekt do zwrapowania.
     * @return Obiekt INorthPlayer.
     */
    INorthPlayer getPlayer(Player player);

    /**
     * Zwraca obiekt gracza bedacego lokalnie na serwerze
     * zwrappowany w pomocniczy interfejs {@link INorthPlayer}.
     *
     * @param uuid uuid gracza bedacego online lokalnie.
     * @return zwrappowany obiekt gracza online.
     * @see org.bukkit.Bukkit#getPlayer(UUID)
     */
    INorthPlayer getPlayer(UUID uuid);

    /**
     * @param nick nick gracza bedacego lokalnie.
     * @return zwrappowany obiekt gracza online
     * @see #getPlayer(String)
     */
    INorthPlayer getPlayer(String nick);

    /**
     * Zwraca instancję CraftPlayera wydobytą z danego Playera.
     *
     * @param player Player z którego zdobywamy CraftPlayera.
     * @return Instancja CraftPlayer danego gracza.
     */
    CraftPlayer getCraftPlayer(Player player);
}
