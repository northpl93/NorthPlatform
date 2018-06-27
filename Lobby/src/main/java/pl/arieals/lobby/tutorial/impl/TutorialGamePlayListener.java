package pl.arieals.lobby.tutorial.impl;

import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.auth.api.IAuthManager;

public class TutorialGamePlayListener implements AutoListener
{
    @Inject
    private IAuthManager       authManager;
    @Inject
    private IScoreboardManager scoreboardManager;
}
