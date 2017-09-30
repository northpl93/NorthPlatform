package pl.north93.zgame.api.bukkit.entityhider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface IEntityHider
{
    /**
     * Ustawia widocznosc danych entities dla podanego gracza.
     * Informacje o przetwarzaniu widocznosci sa dostepne w {@link EntityVisibility}.
     *
     * @param player Gracz dla ktorego modyfikujemy wartosc.
     * @param visibility Docelowa widocznosc.
     * @param entities Kolekcja entities.
     */
    void setVisibility(Player player, EntityVisibility visibility, Collection<Entity> entities);

    /**
     * Ustawia globalna widocznosc danych entities.
     * Informacje o przetwarzaniu widocznosci sa dostepne w {@link EntityVisibility}.
     *
     * @param visibility Docelowa widocznosc.
     * @param entities Kolekcja entities.
     */
    void setVisibility(EntityVisibility visibility, Collection<Entity> entities);

    /**
     * Sprawdza czy dany gracz moze zobaczyc dane entity.
     *
     * @param player Gracz w ktorego kontekscie sprawdzamy widocznosc.
     * @param entity Entity ktorego widocznosc sprawdzamy.
     * @return Czy entity jest widoczne dla gracza.
     */
    boolean isVisible(Player player, Entity entity);

    default void showEntities(Player player, List<Entity> entities)
    {
        // tutaj teoretycznie mozna by uzyc neutral, ale dla zapewnienia pelnej
        // kompatybilnosci musi byc VISIBLE
        this.setVisibility(player, EntityVisibility.VISIBLE, entities);
    }

    default void hideEntities(Player player, List<Entity> entities)
    {
        this.setVisibility(player, EntityVisibility.HIDDEN, entities);
    }

    default void setEntityVisible(Player player, Entity entity, boolean visible)
    {
        final EntityVisibility visibility = visible ? EntityVisibility.VISIBLE : EntityVisibility.HIDDEN;
        this.setVisibility(player, visibility, Collections.singleton(entity));
    }
}
