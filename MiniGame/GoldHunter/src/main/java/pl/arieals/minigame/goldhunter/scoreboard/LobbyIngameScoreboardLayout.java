package pl.arieals.minigame.goldhunter.scoreboard;

import java.util.Arrays;
import java.util.List;

import pl.arieals.minigame.goldhunter.GoldHunterPlayer;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;

public class LobbyIngameScoreboardLayout extends GoldHunterScoreboardLayout
{
    public LobbyIngameScoreboardLayout(GoldHunterPlayer player)
    {
        super(player);
    }

    @Override
    public List<String> getContent(IScoreboardContext context)
    {
        String gameTime = context.get("gameTime") + "";
        
        String team1Count = context.get("team1Count") + "";
        String team2Count = context.get("team2Count") + "";
        
        return Arrays.asList(player.getMessageLines("scoreboard_lobby_ingame", gameTime, team1Count, team2Count));
    }
}
