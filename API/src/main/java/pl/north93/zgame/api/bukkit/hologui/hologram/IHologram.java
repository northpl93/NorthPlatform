package pl.north93.zgame.api.bukkit.hologui.hologram;

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
}
