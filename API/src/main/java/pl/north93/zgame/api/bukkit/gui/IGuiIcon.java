package pl.north93.zgame.api.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

/**
 * Reprezentuje klase renderujaca ikonke do ItemStacka
 * na podstawie otrzymanych danych.
 */
public interface IGuiIcon
{
    ItemStack toItemStack(MessagesBox messages, Player player, Vars<Object> parameters);
}
