package pl.north93.northplatform.api.bukkit.gui;

public interface IPageable
{
    int getPage();

    void setPage(int newPage);

    void nextPage();

    void previousPage();
}
