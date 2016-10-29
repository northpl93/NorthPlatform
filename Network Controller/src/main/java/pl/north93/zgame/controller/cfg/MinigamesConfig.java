package pl.north93.zgame.controller.cfg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;
import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;

import pl.north93.zgame.api.global.network.minigame.MiniGame;
import pl.north93.zgame.api.global.network.minigame.Teaming;

@CfgComments({"Konfiguracja gier w tej sieci"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class MinigamesConfig
{
    private static List<MiniGame> getDefaultMiniGames()
    {
        final Template<MiniGame> template = TemplateCreator.getTemplate(MiniGame.class, true);
        final ArrayList<MiniGame> list = new ArrayList<>(2);

        {
            final MiniGame exampleMinigame = template.fillDefaults(new MiniGame());
            exampleMinigame.setSystemName("example");
            exampleMinigame.setServersGroupName("minigame_example");
            exampleMinigame.setDisplayName("Example MiniGame");
            exampleMinigame.setMaxPlayers(16);
            exampleMinigame.setMinPlayersToStart(4);
            exampleMinigame.setVipSlots(2);
            exampleMinigame.setTeaming(Teaming.ALLOW_TEAM);
            exampleMinigame.setMinTeamSize(2);
            exampleMinigame.setMaxTeamSize(2);

            list.add(exampleMinigame);
        }

        return list;
    }

    @CfgDelegateDefault("getDefaultMiniGames")
    private List<MiniGame> miniGames;

    public List<MiniGame> getMiniGames()
    {
        return this.miniGames;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("miniGames", this.miniGames).toString();
    }
}
