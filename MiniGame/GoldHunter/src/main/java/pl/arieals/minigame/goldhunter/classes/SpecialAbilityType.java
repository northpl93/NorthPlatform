package pl.arieals.minigame.goldhunter.classes;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.abilities.BattlecryAbility;
import pl.arieals.minigame.goldhunter.abilities.BombArrowAbility;
import pl.arieals.minigame.goldhunter.abilities.BrigdeAbility;
import pl.arieals.minigame.goldhunter.abilities.CallOfBloodAbility;
import pl.arieals.minigame.goldhunter.abilities.DazzleAbility;
import pl.arieals.minigame.goldhunter.abilities.DeathArrowAbility;
import pl.arieals.minigame.goldhunter.abilities.DoubleJumpAbility;
import pl.arieals.minigame.goldhunter.abilities.PoisonArrowAbility;
import pl.arieals.minigame.goldhunter.abilities.RedemptionAbility;
import pl.arieals.minigame.goldhunter.abilities.ShadowAbility;
import pl.arieals.minigame.goldhunter.abilities.ShieldAttackAbility;
import pl.arieals.minigame.goldhunter.abilities.SirenTearsAbility;
import pl.arieals.minigame.goldhunter.abilities.SupplierAbility;
import pl.arieals.minigame.goldhunter.abilities.WallAbility;
import pl.arieals.minigame.goldhunter.player.AbilityHandler;

public enum SpecialAbilityType
{
    BATTLE_SCREAM(new BattlecryAbility(), "info.ability.press_q"),
    BOMB_ARROW(new BombArrowAbility(), "info.ability.press_q"),
    BRIDGE(new BrigdeAbility(), "info.ability.press_q"),
    CALL_OF_BLOOD(new CallOfBloodAbility(), "info.ability.press_q"),
    DAZZLE(new DazzleAbility(), "info.ability.press_q"),
    DEATH_ARROW(new DeathArrowAbility(), "info.ability.press_q"),
    DIVINE_SHIELD(new RedemptionAbility(), "info.ability.press_q"), // TODO:
    DOUBLE_JUMP(new DoubleJumpAbility(), "info.ability.press_double_space"),
    HEAL(new SirenTearsAbility(), "info.ability.press_q"),
    POISON_ARROW(new PoisonArrowAbility(), "info.ability.press_q"),
    SHADOW(new ShadowAbility(), "info.ability.press_q"),
    SHIELD_ATTACK(new ShieldAttackAbility(), "info.ability.press_q"),
    SUPPLIER(new SupplierAbility(), "info.ability.press_q"),
    WALL(new WallAbility(), "info.ability.press_q"),
    
    ;
    
    private static final Map<String, SpecialAbilityType> byName = new HashMap<>();
    
    private final AbilityHandler handler;
    private final String abilityReadyMessage;
    
    private SpecialAbilityType(AbilityHandler handler, String abilityReadyMessage)
    {
        Preconditions.checkNotNull(handler);
        this.handler = handler;
        this.abilityReadyMessage = abilityReadyMessage;
    }
    
    public AbilityHandler getHandler()
    {
        return handler;
    }
    
    public String getAbilityReadyMessage()
    {
        return abilityReadyMessage;
    }

    public static SpecialAbilityType byName(String name)
    {
        return byName.get(name);
    }
    
    static
    {
        for ( SpecialAbilityType value : values() )
        {
            byName.put(value.name(), value);
        }
    }
}
