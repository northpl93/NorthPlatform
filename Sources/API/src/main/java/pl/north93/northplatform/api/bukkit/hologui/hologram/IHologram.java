package pl.north93.northplatform.api.bukkit.hologui.hologram;

import org.bukkit.entity.Player;

public interface IHologram
{
    void setMessage(IHologramMessage text);

    /**
     * Zwraca odleglosc miedzy kolejnymi linijkami tekstu.
     *
     * @return Odleglosc miedzy linijkami
     */
    double getLinesSpacing();

    void remove();

    boolean isVisibleBy(Player player);
}
