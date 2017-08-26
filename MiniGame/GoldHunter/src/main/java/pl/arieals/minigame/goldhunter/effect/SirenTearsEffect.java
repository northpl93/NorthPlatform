package pl.arieals.minigame.goldhunter.effect;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;

public class SirenTearsEffect extends RadiusEffect
{
    public SirenTearsEffect(double radius)
    {
        super(radius);

        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void handlePlayer(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new HealingEffect(), 100);
    }
}
