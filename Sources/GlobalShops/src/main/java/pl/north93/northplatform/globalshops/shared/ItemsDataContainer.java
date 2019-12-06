package pl.north93.northplatform.globalshops.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.globalshops.controller.cfg.ItemsGroupCfg;
import pl.north93.serializer.platform.annotations.NorthField;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa uzywana do wymieniania danych przez system configow.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemsDataContainer
{
    @NorthField(type = ArrayList.class)
    private List<ItemsGroupCfg> groups;
}
