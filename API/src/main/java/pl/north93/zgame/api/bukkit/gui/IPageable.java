package pl.north93.zgame.api.bukkit.gui;

public interface IPageable
{
    int getPage();

    void setPage(int newPage);

    void nextPage();

    void previousPage();
}
