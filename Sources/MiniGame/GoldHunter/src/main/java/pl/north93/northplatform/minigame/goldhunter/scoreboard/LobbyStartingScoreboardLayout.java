package pl.north93.northplatform.minigame.goldhunter.scoreboard;

import java.util.Arrays;
import java.util.List;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;

public class LobbyStartingScoreboardLayout extends GoldHunterScoreboardLayout
{
    public LobbyStartingScoreboardLayout(GoldHunterPlayer player)
    {
        super(player);
    }
    
    @Override
    public List<String> getContent(IScoreboardContext context)
    {
        String players = context.get("playersCount") + "";
        String signed = context.get("signedCount") + "";
        String max = context.get("maxPlayers") + "";
        String counter = context.get("startCounter") + "";
        
        String skulls = context.get("skulls") + "";
        
        return Arrays.asList(player.getMessageLines("scoreboard_lobby_starting", players, signed, max, counter, skulls));
    }
}
