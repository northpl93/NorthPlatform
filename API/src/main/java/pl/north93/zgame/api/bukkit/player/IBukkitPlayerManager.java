package pl.north93.zgame.api.bukkit.player;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

/**
 * Komponent pomocniczy umożliwiający używanie Bukkitowego interfejsu {@link OfflinePlayer}
 * na podstawie danych z systemu API.
 */
public interface IBukkitPlayerManager
{
    OfflinePlayer getBukkitOfflinePlayer(UUID uuid);

    OfflinePlayer getBukkitOfflinePlayer(String nick);
}
