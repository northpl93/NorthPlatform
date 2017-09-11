package pl.arieals.minigame.goldhunter;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.abilities.BattlecryAbility;
import pl.arieals.minigame.goldhunter.abilities.BombArrowAbility;
import pl.arieals.minigame.goldhunter.abilities.BrigdeAbility;
import pl.arieals.minigame.goldhunter.abilities.CallOfBloodAbility;
import pl.arieals.minigame.goldhunter.abilities.DazzleAbility;
import pl.arieals.minigame.goldhunter.abilities.DoubleJumpAbility;
import pl.arieals.minigame.goldhunter.abilities.PoisonArrowAbility;
import pl.arieals.minigame.goldhunter.abilities.ShadowAbility;
import pl.arieals.minigame.goldhunter.abilities.ShieldAttackAbility;
import pl.arieals.minigame.goldhunter.abilities.SirenTearsAbility;
import pl.arieals.minigame.goldhunter.abilities.WallAbility;

public enum SpecialAbilityType
{
    // lucznik
    POISON_ARROW(new PoisonArrowAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    BOMB_ARROW(new BombArrowAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    
    // wojownik
    CALL_OF_BLOOD(new CallOfBloodAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    SHIELD_ATTACK(new ShieldAttackAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    
    // zwiadowca
    DOUBLE_JUMP(new DoubleJumpAbility(), 23 * 20, 20 * 20, 17 * 20, 14 * 20),
    DAZZLE(new DazzleAbility(), 40 * 20, 35 * 20, 30 * 20, 25 * 20),
    
    // Medyk
    BATTLECRY(new BattlecryAbility(), 55 * 20, 50 * 20, 45 * 20, 40 * 20),
    SIRENS_TEARS(new SirenTearsAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    
    // Technik
    BRIDGE(new BrigdeAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    // TODO: dozownik
    
    SHADOW(new ShadowAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 2),
    WALL(new WallAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 2),
    
    BETRAYAL(new BetrayalAbility(), 50 * 20, 45 * 20, 40 * 20, 35 * 2),
    REDEMPTION(new RedemptionAbility(), 55 * 20, 50 * 20, 45 * 20, 40 * 20),
    ;
    
    private static final Map<String, SpecialAbilityType> byName = new HashMap<>();
    
    private final AbilityHandler handler;
    private final int[] loadingTimes;
    
    private SpecialAbilityType(AbilityHandler handler, int... loadingTimesInSeconds)
    {
        Preconditions.checkNotNull(handler);
        
        this.handler = handler;
        this.loadingTimes = loadingTimesInSeconds;
    }
    
    public AbilityHandler getHandler()
    {
        return handler;
    }
    
    public int[] getLoadingTimes()
    {
        return loadingTimes;
    }
    
    public int getLoadingTimeAtLevel(int level)
    {
        return loadingTimes[Math.min(level, loadingTimes.length - 1)];
    }

    public static SpecialAbilityType byName(String name)
    {
        return byName.get(name);
    }
    
    static {
        
        for ( SpecialAbilityType value : values() )
        {
            byName.put(value.name(), value);
        }
    }
}
