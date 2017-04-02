package pl.north93.zgame.api.bukkit.windows.test;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import pl.north93.zgame.api.bukkit.windows.ClickHandler;
import pl.north93.zgame.api.bukkit.windows.PaginateWindow;

public class TestPaginateWindow
{
    public static class TestWindow extends PaginateWindow<String>
    {
        public TestWindow(final Collection<String> collection, final int contentRows, final int page)
        {
            super(collection, "", contentRows, page);
        }

        @Override
        protected void drawNavigator(final int offset)
        {
        }

        @Override
        protected Pair<ItemStack, ClickHandler> drawElement(final String element)
        {
            return null;
        }
    }

    @Test
    public void testPagesCount()
    {
        final TestWindow testWindow1 = new TestWindow(this.generateTestData(9), 1, 1);
        assertEquals(1, testWindow1.getPagesCount());

        final TestWindow testWindow2 = new TestWindow(this.generateTestData(10), 1, 1);
        assertEquals(2, testWindow2.getPagesCount());

        final TestWindow testWindow3 = new TestWindow(this.generateTestData(18), 1, 1);
        assertEquals(2, testWindow3.getPagesCount());
    }

    private Collection<String> generateTestData(final int count)
    {
        final ArrayList<String> data = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
        {
            data.add("");
        }
        return data;
    }
}
