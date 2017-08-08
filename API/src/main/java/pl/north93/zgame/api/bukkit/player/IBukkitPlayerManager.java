package pl.north93.zgame.api.bukkit.player;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

/**
 * Komponent pomocniczy umożliwiający używanie Bukkitowych interfejsow
 * wraz z danymi z systemu North Platform.
 */
public interface IBukkitPlayerManager
{
    OfflinePlayer getBukkitOfflinePlayer(UUID uuid);

    OfflinePlayer getBukkitOfflinePlayer(String nick);

    /**
     * Zwraca obiekt gracza bedacego lokalnie na serwerze
     * zwrappowany w pomocniczy interfejs {@link INorthPlayer}.
     *
     * @see org.bukkit.Bukkit#getPlayer(UUID)
     * @param uuid uuid gracza bedacego online lokalnie.
     * @return zwrappowany obiekt gracza online.
     */
    INorthPlayer getPlayer(UUID uuid);

    /**
     * @see #getPlayer(String)
     * @param nick nick gracza bedacego lokalnie.
     * @return zwrappowany obiekt gracza online
     */
    INorthPlayer getPlayer(String nick);
}
