package pl.north93.northplatform.api.global.network;

public enum JoiningPolicy
{
    EVERYONE, // wszyscy
    ONLY_VIP, // użytkownicy z uprawnieniem vip.join
    ONLY_ADMIN, // użytkownicy z uprawnieniem admin.join
    NOBODY // nikt
}
