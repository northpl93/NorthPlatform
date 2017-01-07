package pl.north93.zgame.datashare.sharedimpl.basemcdata;

import org.bson.Document;

import pl.north93.zgame.datashare.api.data.IDataUnitPersistence;

public class BaseMcDataPersistence implements IDataUnitPersistence<BaseMcDataContainer>
{
    @Override
    public Document toDatabase(final BaseMcDataContainer dataUnit)
    {
        final Document doc = new Document();

        doc.put("inventory", dataUnit.getInventory());
        doc.put("enderchest", dataUnit.getEnderchest());
        doc.put("helditemslot", dataUnit.getHeldItemSlot());

        doc.put("health", dataUnit.getHealth());

        doc.put("foodlevel", dataUnit.getFoodLevel());
        doc.put("exhaustion", dataUnit.getExhaustion());
        doc.put("saturation", dataUnit.getSaturation());

        doc.put("statistics", dataUnit.getStatistics());
        doc.put("gamemode", dataUnit.getGameMode());

        return doc;
    }

    @Override
    public BaseMcDataContainer fromDatabase(final Document doc)
    {
        final BaseMcDataContainer data = new BaseMcDataContainer();

        data.setInventory(doc.getString("inventory"));
        data.setEnderchest(doc.getString("enderchest"));
        data.setHeldItemSlot(doc.getInteger("helditemslot"));

        data.setHealth(doc.getDouble("health"));

        data.setFoodLevel(doc.getInteger("foodlevel"));
        data.setExhaustion(doc.getDouble("exhaustion").floatValue());
        data.setSaturation(doc.getDouble("saturation").floatValue());

        data.setStatistics(doc.getString("statistics"));
        data.setGameMode(doc.getInteger("gamemode", 1)); // 1 is survival

        return data;
    }
}
