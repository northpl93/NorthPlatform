package pl.north93.northplatform.minigame.goldhunter.scoreboard;

import java.util.Arrays;
import java.util.List;

import pl.north93.northplatform.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;

public class IngameScoreboardLayout extends GoldHunterScoreboardLayout
{
    public IngameScoreboardLayout(GoldHunterPlayer player)
    {
        super(player);
    }
    
    @Override
    public List<String> getContent(IScoreboardContext context)
    {
        String gameTime = context.get("gameTime") + "";
        
        String team1Count = context.get("team1Count") + "";
        String team2Count = context.get("team2Count") + "";
        
        String team1Chests = context.get("team1Chests") + "";
        String team2Chests = context.get("team2Chests") + "";
        
        String stats = context.get("stats") + "";
        
        String skulls = context.get("skulls") + "";
        
        return Arrays.asList(player.getMessageLines("scoreboard_ingame", gameTime, team1Count, team2Count, team1Chests, team2Chests, stats, skulls));
    }
}
