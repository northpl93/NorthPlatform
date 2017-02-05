package pl.north93.zgame.skyplayerexp.bungee.tablist;

@FunctionalInterface
public interface ICellProvider
{
    String process(TablistDrawingContext ctx);
}
