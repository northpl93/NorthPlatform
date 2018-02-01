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
    POISON_ARROW(new PoisonArrowAbility(), "archer.poison.time", 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    BOMB_ARROW(new BombArrowAbility(), "archer.bomb.time", 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    
    // wojownik
    CALL_OF_BLOOD(new CallOfBloodAbility(), "warrior.berserker.time", 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    SHIELD_ATTACK(new ShieldAttackAbility(), "warrior.knight.time", 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    
    // zwiadowca
    DOUBLE_JUMP(new DoubleJumpAbility(), "scout.sprinter.time", 23 * 20, 20 * 20, 17 * 20, 14 * 20),
    DAZZLE(new DazzleAbility(), "scout.slinger.time", 40 * 20, 35 * 20, 30 * 20, 25 * 20),
    
    // Medyk
    BATTLECRY(new BattlecryAbility(), "medic.battle.time", 55 * 20, 50 * 20, 45 * 20, 40 * 20),
    SIRENS_TEARS(new SirenTearsAbility(), "medic.healer.time", 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    
    // Technik
    BRIDGE(new BrigdeAbility(), "engineer.architect.time", 50 * 20, 45 * 20, 40 * 20, 35 * 20),
    // TODO: dozownik
    
    SHADOW(new ShadowAbility(), "vip.assasyn.time", 50 * 20, 45 * 20, 40 * 20, 35 * 2),
    WALL(new WallAbility(), "vip.defender.time", 50 * 20, 45 * 20, 40 * 20, 35 * 2),
    
    BETRAYAL(new BetrayalAbility(), "svip.spy.time", 50 * 20, 45 * 20, 40 * 20, 35 * 2),
    REDEMPTION(new RedemptionAbility(), "svip.paladin.time", 55 * 20, 50 * 20, 45 * 20, 40 * 20),
    ;
    
    private static final Map<String, SpecialAbilityType> byName = new HashMap<>();
    
    private final AbilityHandler handler;
    private final String shopItemName;
    private final int[] loadingTimes;
    
    private SpecialAbilityType(AbilityHandler handler, String shopItem, int... loadingTimesInSeconds)
    {
        Preconditions.checkNotNull(handler);
        
        this.handler = handler;
        this.shopItemName = shopItem;
        this.loadingTimes = loadingTimesInSeconds;
    }
    
    public AbilityHandler getHandler()
    {
        return handler;
    }
    
    public String getShopItemName()
    {
        return shopItemName;
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
