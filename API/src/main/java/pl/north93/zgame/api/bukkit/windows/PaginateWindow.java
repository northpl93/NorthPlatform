package pl.north93.zgame.api.bukkit.windows;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

public abstract class PaginateWindow<T> extends Window
{
    private final Collection<T> elements;
    private final int           contentRows;
    private       int           currentPage;

    public PaginateWindow(final Collection<T> collection, final String title, final int contentRows, final int page)
    {
        super(title, 9 + contentRows * 9);
        this.elements = collection;
        this.contentRows = contentRows;
        this.currentPage = page;
    }

    public final int getElementsPerPage()
    {
        return this.contentRows * 9;
    }

    public final int getPagesCount()
    {
        return (int) Math.ceil((double) this.elements.size() / this.getElementsPerPage());
    }

    public final int getCurrentPage()
    {
        return this.currentPage;
    }

    public final boolean hasNextPage()
    {
        return this.currentPage != this.getPagesCount();
    }

    public final boolean hasPreviousPage()
    {
        return this.currentPage != 1;
    }

    public final void drawPage(final int page)
    {
        this.clear();
        this.currentPage = page;
        this.drawElements();
        this.drawNavigator(this.getElementsPerPage());
    }

    @Override
    protected final void onShow()
    {
        this.drawPage(this.currentPage);
    }

    private void drawElements()
    {
        @SuppressWarnings("unchecked")
        final T[] elements = this.elements.toArray((T[]) new Object[this.elements.size()]);

        final int start = (this.currentPage - 1) * this.getElementsPerPage();
        final int end   = Math.min(elements.length, start + this.getElementsPerPage());

        for (int i = start, windowLoc = 0; i < end; i++, windowLoc++)
        {
            final Pair<ItemStack, ClickHandler> element = this.drawElement(elements[i]);
            this.addElement(windowLoc, element.getKey(), element.getValue());
        }
    }

    protected abstract void drawNavigator(final int offset);

    protected abstract Pair<ItemStack, ClickHandler> drawElement(final T element);

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("elements", this.elements).append("contentRows", this.contentRows).append("currentPage", this.currentPage).toString();
    }
}
