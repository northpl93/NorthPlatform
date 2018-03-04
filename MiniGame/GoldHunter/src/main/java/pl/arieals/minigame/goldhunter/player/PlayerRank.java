package pl.arieals.minigame.goldhunter.player;

import org.bukkit.entity.Player;

public enum PlayerRank
{
    SVIP,
    VIP,
    ;
    
    private final String permission;
    
    private PlayerRank()
    {
        this.permission = "goldhunter." + name().toLowerCase();
    }
    
    public String getPermission()
    {
        return permission;
    }
    
    public boolean has(GoldHunterPlayer player)
    {
        return has(player.getPlayer());
    }
    
    public boolean has(Player player)
    {
        return player.hasPermission(permission);
    }
}
