package pl.north93.zgame.antycheat.cheat.movement;

import pl.north93.zgame.antycheat.timeline.DataKey;
import pl.north93.zgame.antycheat.timeline.PlayerData;

public class JumpController
{
    private static final DataKey<JumpController> KEY = new DataKey<>("jumpController", JumpController::new);

    public static JumpController get(final PlayerData playerData)
    {
        return playerData.get(KEY);
    }

    public void tearOffGround()
    {

    }
}
