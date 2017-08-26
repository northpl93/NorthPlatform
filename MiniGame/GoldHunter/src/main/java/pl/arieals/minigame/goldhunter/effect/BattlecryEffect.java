package pl.arieals.minigame.goldhunter.effect;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;

public class BattlecryEffect extends RadiusEffect
{
    public BattlecryEffect(double radius)
    {
        super(radius);
        
        setBarColor(EffectBarColor.GREEN);
    }
    
    @Override
    protected void handlePlayer(GoldHunterPlayer player)
    {
        player.getEffectTracker().addEffect(new StrengthEffect(), 60);
    }
}
