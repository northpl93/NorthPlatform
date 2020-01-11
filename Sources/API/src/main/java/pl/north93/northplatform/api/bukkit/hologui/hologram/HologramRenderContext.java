package pl.north93.northplatform.api.bukkit.hologui.hologram;

import java.util.Locale;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class HologramRenderContext
{
    private final IHologram hologram;
    private final Player    player;
    private final Locale    locale;
}
