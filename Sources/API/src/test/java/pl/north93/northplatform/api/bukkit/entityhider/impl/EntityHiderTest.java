package pl.north93.northplatform.api.bukkit.entityhider.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import pl.north93.northplatform.api.bukkit.entityhider.EntityVisibility;

public class EntityHiderTest
{
    private GlobalVisibility globalVisibility;

    @BeforeEach
    public void setup()
    {
        this.globalVisibility = new GlobalVisibility();
    }

    @Test
    public void simplePerPlayer()
    {
        final Entity entity = Mockito.mock(CraftEntity.class);
        final VisibilityController visibilityController = new VisibilityController(this.globalVisibility);

        assertTrue(visibilityController.isEntityVisible(entity));

        visibilityController.setVisibility(entity, EntityVisibility.VISIBLE);
        assertTrue(visibilityController.isEntityVisible(entity));

        visibilityController.setVisibility(entity, EntityVisibility.HIDDEN);
        assertFalse(visibilityController.isEntityVisible(entity));
    }

    @Test
    public void advancedWithGlobal()
    {
        final Entity entity = Mockito.mock(CraftEntity.class);
        final VisibilityController visibilityController = new VisibilityController(this.globalVisibility);

        this.globalVisibility.setGlobalEntityStatus(entity, EntityVisibility.HIDDEN);
        assertFalse(visibilityController.isEntityVisible(entity));

        visibilityController.setVisibility(entity, EntityVisibility.VISIBLE);
        assertTrue(visibilityController.isEntityVisible(entity));

        this.globalVisibility.setGlobalEntityStatus(entity, EntityVisibility.VISIBLE);
        visibilityController.setVisibility(entity, EntityVisibility.HIDDEN);
        assertFalse(visibilityController.isEntityVisible(entity)); // poniewaz wartosc gracza jest wazniejsza, zgodnie z dokumentacja
    }
}
