package pl.north93.zgame.api.bukkit.gui.impl.click;

import pl.north93.zgame.api.bukkit.gui.event.ClickEvent;
import pl.north93.zgame.api.bukkit.gui.impl.NorthUriUtils;
import pl.north93.zgame.api.global.utils.Vars;

public class NorthUriClickHandler implements IClickHandler
{
    private final String northUri;

    public NorthUriClickHandler(final String northUri)
    {
        this.northUri = northUri;
    }

    @Override
    public void handle(final IClickSource source, final ClickEvent event)
    {
        final Vars.Builder<Object> builder = Vars.builder();

        builder.and(source.getVariables());
        builder.and("$playerId", event.getWhoClicked().getUniqueId());
        builder.and("$playerName", event.getWhoClicked().getName());

        NorthUriUtils.getInstance().call(this.northUri, builder.build());
    }
}
