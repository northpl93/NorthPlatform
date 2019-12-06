package pl.arieals.minigame.goldhunter.scoreboard;

import java.util.Arrays;
import java.util.List;

import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;

public class LobbyOutgameScoreboardLayout extends GoldHunterScoreboardLayout
{
    public LobbyOutgameScoreboardLayout(GoldHunterPlayer player)
    {
        super(player);
    }

    @Override
    public List<String> getContent(IScoreboardContext context)
    {
        String players = context.get("playersCount") + "";
        String signed = context.get("signedCount") + "";
        String max = context.get("maxPlayers") + "";
        
        String skulls = context.get("skulls") + "";
        
        return Arrays.asList(this.player.getMessageLines("scoreboard_lobby_outgame", players, signed, max, skulls));
    }
}
