package pl.north93.robbermod;

import static pl.north93.robbermod.data.RobberDataProvider.DATA_CAP;


import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.north93.robbermod.cooldown.CooldownManager;
import pl.north93.robbermod.data.IRobberData;
import pl.north93.robbermod.data.RobberDataProvider;

public class StealListener
{
    private final       CooldownManager<UUID> cooldown   = CooldownManager.createManager(64);
    private final       Configuration         config     = RobberMod.getInstance().getConfig();
    private static final Random random = new Random();
    public static final ResourceLocation      ROBBER_CAP = new ResourceLocation("pl.north93.robbermod", "robberdata");

    @SubscribeEvent
    public void attachData(final AttachCapabilitiesEvent.Entity event)
    {
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        event.addCapability(ROBBER_CAP, new RobberDataProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone event)
    {
        event.getEntityPlayer().getCapability(DATA_CAP, null).setRobberCount(event.getOriginal().getCapability(DATA_CAP, null).getRobberCount());
    }

    @SubscribeEvent
    public void onInteract(final PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (! (event.getTarget() instanceof EntityPlayerMP))
        {
            return;
        }

        final EntityPlayer attacker = event.getEntityPlayer();
        final EntityPlayerMP target = (EntityPlayerMP) event.getTarget();

        if (! attacker.isSneaking())
        {
            return; // gracz musi kucac
        }

        if (! this.cooldown.hasExpired(attacker.getUniqueID()))
        {
            attacker.addChatComponentMessage(new TextComponentString("[DEBUG] COOLDOWN"));
            return;
        }
        this.cooldown.add(attacker.getUniqueID(), this.config.get("general", "cooldown", 5000).getInt()); // add cooldown

        final IRobberData robberData = attacker.getCapability(DATA_CAP, null);

        attacker.addChatComponentMessage(new TextComponentString("robberCount = " + robberData.getRobberCount()));

        final double maxChance = this.config.get("general", "max_chance", 49).getDouble();
        final double baseChance = this.config.get("general", "base_chance", 1).getDouble();
        final double chanceMultiplier = this.config.get("general", "chance_multiplier", 0.5).getDouble();

        final double calcChance = Math.min(baseChance + (robberData.getRobberCount() * chanceMultiplier), maxChance);
        attacker.addChatComponentMessage(new TextComponentString("[DEBUG] chance = " + calcChance / 100));
        if (! this.chance(calcChance / 100))
        {
            attacker.addChatComponentMessage(new TextComponentString("[DEBUG] Sorry, not this time"));
            return;
        }

        attacker.addChatComponentMessage(new TextComponentString("[DEBUG] YAY, processing steal"));

        ItemStack itemToSteal;
        int stealTries = 0;
        while (true)
        {
            if (stealTries < 16)
            {
                stealTries++;
                final int randomSlot = random.nextInt(target.inventory.getSizeInventory());
                itemToSteal = target.inventory.getStackInSlot(randomSlot);
                if (itemToSteal != null)
                {
                    // found a item to steal
                    robberData.incrementRobberCount();
                    target.inventory.removeStackFromSlot(randomSlot);
                    if (! attacker.inventory.addItemStackToInventory(itemToSteal))
                    {
                        attacker.worldObj.spawnEntityInWorld(new EntityItem(attacker.worldObj, attacker.posX, attacker.posY, attacker.posZ, itemToSteal));
                    }
                    attacker.addChatComponentMessage(new TextComponentString("[DEBUG] Steal completed."));
                    return; // complete
                }
            }
            else
            {
                attacker.addChatComponentMessage(new TextComponentString("[DEBUG] Steal cancelled, can't find item."));
                return; // failed to find item
            }
        }
    }

    private boolean chance(final double chance)
    {
        return Math.random() < chance;
    }
}
