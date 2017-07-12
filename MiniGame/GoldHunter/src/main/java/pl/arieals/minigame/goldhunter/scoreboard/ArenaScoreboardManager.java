package pl.arieals.minigame.goldhunter.scoreboard;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.goldhunter.GoldHunterArena;
import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ArenaScoreboardManager
{
    private final GoldHunterArena arena;
    
    @Inject
    private IScoreboardManager scoreboardManager;
    
    private final Map<String, Object> contextProperties = new HashMap<>();
    
    public ArenaScoreboardManager(GoldHunterArena arena)
    {
        this.arena = arena;
    }
    
    public void setIngameScoreboardLoayout(GoldHunterPlayer player)
    {
        GoldHunterScoreboardLayout layout = new IngameScoreboardLayout(player);
        setScoreboardLayout(player, layout);
    }
    
    public void setLobbyScoreboardLayout(GoldHunterPlayer player)
    {
        GoldHunterScoreboardLayout layout = createLobbyScoreboardLayout(player);
        setScoreboardLayout(player, layout);
    }
    
    private GoldHunterScoreboardLayout createLobbyScoreboardLayout(GoldHunterPlayer player)
    {
        if ( arena.getLocalArena().getGamePhase() == GamePhase.STARTED 
                || arena.getLocalArena().getGamePhase() == GamePhase.POST_GAME )
        {
            return new LobbyIngameScoreboardLayout(player);
        }
        
        if ( arena.getLocalArena().getStartScheduler().isStartScheduled() )
        {
            return new LobbyStartingScoreboardLayout(player);
        }
        
        return new LobbyOutgameScoreboardLayout(player);
    }
    
    private void setScoreboardLayout(GoldHunterPlayer player, GoldHunterScoreboardLayout layout)
    {
        IScoreboardContext newContext = scoreboardManager.setLayout(player.getPlayer(), layout);
        player.setScoreboardContext(newContext);
        newContext.set(contextProperties);
    }
    
    public void setProperty(String key, Object value)
    {
        Preconditions.checkArgument(key != null);
        
        if ( value != null )
        {
            contextProperties.put(key, value);
        }
        else
        {
            contextProperties.remove(key);
        }
        
        updateAllScoreboards();
    }
    
    public void setProperties(Object... properties)
    {
        Preconditions.checkArgument(properties != null && properties.length > 0 && properties.length % 2 == 0);
        
        for ( int i = 0; i < properties.length; i += 2)
        {
            if ( !( properties[i] instanceof String ) )
            {
                throw new IllegalArgumentException();
            }
        }
        
        for ( int i = 0; i < properties.length; i += 2 )
        {
            String key = (String) properties[i];
            Object value = properties[i + 1];
            
            contextProperties.put(key, value);
        }
        
        updateAllScoreboards();
    }
    
    private void updateAllScoreboards()
    {
        arena.getPlayers().stream().filter(player -> player.getScoreboardContext() != null)
                .forEach(player -> player.getScoreboardContext().set(contextProperties));
    }
    
    
}
