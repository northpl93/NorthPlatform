package pl.north93.northplatform.api.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

/**
 * Reprezentuje klase renderujaca ikonke do ItemStacka
 * na podstawie otrzymanych danych.
 */
public interface IGuiIcon
{
    /**
     * Metoda wywoływana podczas renderowania, ma zwrócić ItemStack który ostatecznie
     * zostanie umieszczony w ekwipunku.
     *
     * @param messages Plik z wiadomościami z którego korzysta ekwipunek.
     * @param player Gracz dla którego renderowany jest ekwipunek.
     * @param parameters Zmienne używane w tłumaczeniach.
     * @return Przedmiot który zostanie umieszczony w ekwipunku gracza.
     */
    ItemStack toItemStack(MessagesBox messages, Player player, Vars<Object> parameters);
}
