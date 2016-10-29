package pl.north93.zgame.lobby.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgComments;
import org.diorite.cfg.annotations.CfgFooterComment;
import org.diorite.cfg.annotations.defaults.CfgBooleanDefault;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

@CfgComments({"Konfiguracja pluginy lobby"})
@CfgFooterComment("Koniec konfiguracji!")
@CfgDelegateDefault("{new}")
public class LobbyConfig
{
    public static List<PetConfig> getDefaultPets()
    {
        final ArrayList<PetConfig> list = new ArrayList<>(2);

        {
            final PetConfig petConfig = new PetConfig();
            petConfig.systemName = "duck";
            petConfig.displayName = "Kaczka";
            petConfig.headOwnerData = "";
            petConfig.description = Arrays.asList("To jest kaczka.", "Kaczki są fajne.");
            petConfig.requirePermission = false;
            petConfig.permission = "lobby.pets.duck";
            list.add(petConfig);
        }

        return list;
    }

    @CfgComment("Czy lobby ma mieć włączony tryb deva po uruchomieniu serwera.")
    @CfgBooleanDefault(true)
    public boolean devMode;

    @CfgComment("Czy na lobby ma być nieskończony dzień.")
    @CfgBooleanDefault(true)
    public boolean infinityDay;

    @CfgComment("Konfiguracja listy zwierzaków w oknie wyboru")
    @CfgDelegateDefault("getDefaultPets")
    public List<PetConfig> pets;

    public static class PetConfig
    {
        @CfgComment("Nazwa wewnętrzna")
        public String       systemName;
        @CfgComment("Nazwa wyświetlana graczowi")
        public String       displayName;
        @CfgComment("Base64 z properties textures")
        public String       headOwnerData;
        @CfgComment("Opis zwierzaka")
        public List<String> description;
        @CfgComment("Czy wymagane są uprawnienia do ustawienia tego zwierzaka")
        public boolean      requirePermission;
        @CfgComment("Nazwa uprawnienie, jesli requirePermission = true")
        public String       permission;
    }
}
