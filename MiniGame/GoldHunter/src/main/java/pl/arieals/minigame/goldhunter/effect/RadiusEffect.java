package pl.arieals.minigame.goldhunter.effect;

import org.bukkit.entity.Player;

import pl.arieals.minigame.goldhunter.GoldHunter;
import pl.arieals.minigame.goldhunter.player.Effect;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public abstract class RadiusEffect extends Effect
{
    @Inject
    private static GoldHunter goldHunter;
    
    private double radius;
    
    protected RadiusEffect(double radius)
    {
        this.radius = radius;
    }
    
    public void setRadius(double radius)
    {
        this.radius = radius;
    }
    
    public double getRadius()
    {
        return radius;
    }
    
    @Tick
    private void onTick()
    {
        getPlayer().getPlayer().getNearbyEntities(radius, radius, radius).stream().filter(e -> e instanceof Player).map(e -> (Player) e)
                .map(goldHunter::getPlayer).filter(p -> p != null).forEach(this::handlePlayer);
    }
    
    protected abstract void handlePlayer(GoldHunterPlayer player);
}
