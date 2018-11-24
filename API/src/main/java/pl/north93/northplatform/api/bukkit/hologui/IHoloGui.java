package pl.north93.northplatform.api.bukkit.hologui;

public interface IHoloGui
{
    void openGui(IHoloContext context);

    void closeGui(IHoloContext context);

    void iconClicked(IHoloContext context, IIcon icon);
}
