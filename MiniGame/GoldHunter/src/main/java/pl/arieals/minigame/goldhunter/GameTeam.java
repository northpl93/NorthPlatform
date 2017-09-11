package pl.arieals.minigame.goldhunter;

import org.bukkit.ChatColor;
import org.bukkit.Color;

import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;

public enum GameTeam
{
    RED(ChatColor.RED, Color.RED),
    BLUE(ChatColor.AQUA, Color.BLUE),
    ;
    
    private final ChatColor teamColor;
    private final Color armorColor;
    
    private TranslatableString nominativeName;
    private TranslatableString genitiveName;
    
    private GameTeam(ChatColor teamColor, Color armorColor)
    {
        this.armorColor = armorColor;
        this.teamColor = teamColor;
        
        MessagesBox messages = new MessagesBox(GameTeam.class.getClassLoader(), "GoldHunter");
        
        nominativeName = TranslatableString.of(messages, "@team." + name().toLowerCase() + ".nominative");
        genitiveName = TranslatableString.of(messages, "@team." + name().toLowerCase() + ".genitive");
    }
    
    public ChatColor getTeamColor()
    {
        return teamColor;
    }
    
    public Color getArmorColor()
    {
        return armorColor;
    }
    
    public TranslatableString getNominative()
    {
        return nominativeName;
    }
    
    public TranslatableString getGenitive()
    {
        return genitiveName;
    }
    
    public TranslatableString getColoredNominative()
    {
        return TranslatableString.constant(teamColor + "").concat(nominativeName);
    }
    
    public TranslatableString getColoredBoldNominative()
    {
        return TranslatableString.constant(teamColor + "§l").concat(nominativeName);
    }
    
    public TranslatableString getColoredGenitive()
    {
        return TranslatableString.constant(teamColor + "").concat(genitiveName);
    }
    
    public TranslatableString getColoredBoldGenitive()
    {
        return TranslatableString.constant(teamColor + "§l").concat(genitiveName);
    }
    
    public GameTeam opositeTeam()
    {
        return opositeTeam(this);
    }
    
    public static GameTeam opositeTeam(GameTeam team)
    {
        if ( team == RED )
        {
            return BLUE;
        }
        if ( team == BLUE )
        {
            return RED;
        }
        
        throw new IllegalArgumentException();
    }
}
