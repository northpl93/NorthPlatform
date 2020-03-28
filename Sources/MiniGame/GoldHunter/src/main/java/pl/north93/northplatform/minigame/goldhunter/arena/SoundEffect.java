package pl.north93.northplatform.minigame.goldhunter.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;

public enum SoundEffect
{
    ABILITY_READY("entity.player.levelup", 2),
    ABILITY_USE("entity.experience_orb.pickup"),
    CHEST_DESTROY("entity.wither.death"),
    //DAGGER_CRITICAL("block.anvil.destroy"),
    
    ;
    
    
    private final String soundName;
    private final float pitch;
    
    private SoundEffect(String soundName)
    {
        this(soundName, 1);
    }
    
    private SoundEffect(String soundName, float pitch)
    {
        this.soundName = soundName;
        this.pitch = pitch;
    }
    
    public void play(Location loc)
    {
        World world = loc.getWorld();
        world.playSound(loc, soundName, 1.0f, pitch);
    }
    
    public void play(GoldHunterPlayer player)
    {
        play(player.getPlayer());
    }
    
    public void play(GoldHunterArena arena)
    {
        arena.getSignedPlayers().forEach(this::play);
    }
    
    public void play(Player player)
    {
        player.playSound(player.getLocation(), soundName, 1.0f, pitch);
    }
}
