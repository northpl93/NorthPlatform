package pl.arieals.minigame.bedwars.arena.generator;

import java.util.Arrays;
import java.util.List;

import pl.arieals.minigame.bedwars.cfg.BwGeneratorItemConfig;
import pl.arieals.minigame.bedwars.cfg.BwGeneratorType;
import pl.north93.northplatform.api.bukkit.hologui.hologram.HologramRenderContext;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologram;
import pl.north93.northplatform.api.bukkit.hologui.hologram.IHologramMessage;
import pl.north93.northplatform.api.bukkit.hologui.hologram.impl.HologramFactory;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class GeneratorHologram implements IHologramMessage
{
    @Inject @Messages("BedWars")
    private MessagesBox messages;

    private final IHologram           hologram;
    private final GeneratorController controller;
    private BwGeneratorItemConfig currentItem;
    private int                   timer;

    public GeneratorHologram(final GeneratorController generatorController)
    {
        this.hologram = HologramFactory.create(generatorController.getLocation().clone().add(0, 3.3, 0));

        this.controller = generatorController;
    }

    @Override
    public List<String> render(final HologramRenderContext renderContext)
    {
        final LegacyMessage tier = this.messages.getLegacy(renderContext.getLocale(), "generator.tier", this.currentItem.getName());

        final BwGeneratorType generatorType = this.controller.getGeneratorType();
        final LegacyMessage type = this.messages.getLegacy(renderContext.getLocale(), "generator.type.nominative." + generatorType.getName());

        final LegacyMessage status;
        if (this.timer < 0)
        {
            status = this.messages.getLegacy(renderContext.getLocale(), "generator.overload");
        }
        else if (this.currentItem.getStartAt() > this.controller.getGameTime())
        {
            status = this.messages.getLegacy(renderContext.getLocale(), "generator.disabled");
        }
        else
        {
            final int timeTo = (this.currentItem.getEvery() - this.timer) / 20;
            status = this.messages.getLegacy(renderContext.getLocale(), "generator.next_item_in", timeTo);
        }

        return Arrays.asList(tier.asString(), type.asString(), status.asString());
    }

    public void update(final BwGeneratorItemConfig currentItem, final int timer)
    {
        this.currentItem = currentItem;
        this.timer = timer;

        this.hologram.setMessage(this);
    }

    public void overload()
    {
        this.timer = -1;

        this.hologram.setMessage(this);
    }
}
