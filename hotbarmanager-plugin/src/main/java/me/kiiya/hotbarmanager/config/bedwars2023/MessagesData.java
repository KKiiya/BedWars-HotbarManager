package me.kiiya.hotbarmanager.config.bedwars2023;

import com.tomkeuper.bedwars.api.language.Language;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Arrays;
import java.util.Collections;

import static me.kiiya.hotbarmanager.config.ConfigPaths.*;

public class MessagesData {
    public MessagesData() {
        setup();
    }

    private void setup() {
        for (Language l : Language.getLanguages()) {
            YamlConfiguration yml = l.getYml();
            switch (l.getIso()) {
                // Brazilian portuguese. Special thanks to zypj.
                case "pt":
                    yml.addDefault(NO_PERMISSION, "&cVocê não tem permissão para isso!");
                    yml.addDefault(INVENTORY_NAME, "HotBar Manager");
                    yml.addDefault(INVENTORY_ITEM_NAME, "&aHotbar Manager");
                    yml.addDefault(INVENTORY_ITEM_LORE, Arrays.asList("&7Editar a preferência de slots", "&7de qualquer item da loja.", "", "&eClique para Editar!"));
                    yml.addDefault(INVENTORY_ITEMS_BLOCKS_NAME, "&aBlocos");
                    yml.addDefault(INVENTORY_ITEMS_MELEE_NAME, "&aCombate");
                    yml.addDefault(INVENTORY_ITEMS_TOOLS_NAME, "&aFerramentas");
                    yml.addDefault(INVENTORY_ITEMS_RANGED_NAME, "&aDistância");
                    yml.addDefault(INVENTORY_ITEMS_POTIONS_NAME, "&aPoções");
                    yml.addDefault(INVENTORY_ITEMS_SPECIALS_NAME, "&aUtilitários");
                    yml.addDefault(INVENTORY_ITEMS_COMPASS_NAME, "&aBússola");
                    yml.addDefault(INVENTORY_ITEMS_COMPASS_LORE, Arrays.asList("&7Coloque esse item onde você", "&7deseja que ela apareça quando você nascer.", "", "&cSe você não deixar esse item", "&cem nenhum lugar, ela não nascerá com você.", "", "&eClique para alterar!"));
                    yml.addDefault(INVENTORY_ITEMS_LORE, Arrays.asList("&7Arraste isso para sua hotbar", "&7para sempre que você comprar", "&7qualquer item dessa categoria", "&7venha automaticamente para esse slot.", "", "&eClique para alterar!"));
                    yml.addDefault(INVENTORY_ITEMS_USED_LORE, Arrays.asList("&7{category} &7items will prioritize this", "&7slot!", "", "&eClique para Remover!"));
                    yml.addDefault(INVENTORY_ITEMS_PLACEHOLDER_NAME, "&7Slot &e{slot}");
                    yml.addDefault(INVENTORY_ITEMS_PLACEHOLDER_LORE, Arrays.asList("&7Arraste um item para cá", "&7para priorizar esse slot."));
                    yml.addDefault(INVENTORY_ITEMS_BACK_LOBBY_NAME, "&aVoltar");
                    yml.addDefault(INVENTORY_ITEMS_BACK_LOBBY_LORE, Collections.singletonList("&7Para Jogar Bed Wars"));
                    yml.addDefault(INVENTORY_ITEMS_BACK_QUICK_BUY_NAME, "&aVoltar");
                    yml.addDefault(INVENTORY_ITEMS_BACK_QUICK_BUY_LORE, Collections.singletonList("&7Para Compra Rápida."));
                    yml.addDefault(INVENTORY_ITEMS_RESET_NAME, "&cVoltar ao Padrão");
                    yml.addDefault(INVENTORY_ITEMS_RESET_LORE, Arrays.asList("&7Voltar sua hotbar ao", "&7padrão."));
                    yml.addDefault(INVENTORY_CURRENT_PAGE_ITEM_NAME, "&aPágina Atual: &e{page}");
                    yml.addDefault(INVENTORY_CURRENT_PAGE_ITEM_LORE, Collections.singletonList("&7Mostra a página que você está."));
                    yml.addDefault(INVENTORY_NEXT_PAGE_ITEM_NAME, "&aPróxima Página: &e{page}");
                    yml.addDefault(INVENTORY_NEXT_PAGE_ITEM_LORE, Collections.singletonList("&7Clique para ir para a próxima página."));
                    yml.addDefault(INVENTORY_PREVIOUS_PAGE_ITEM_NAME, "&aPágina Anterior: &e{page}");
                    yml.addDefault(INVENTORY_PREVIOUS_PAGE_ITEM_LORE, Collections.singletonList("&7Clique para ir para a página anterior."));
                    yml.addDefault(MEANING_BLOCKS, "&7Blocos");
                    yml.addDefault(MEANING_MELEE, "&7Combate");
                    yml.addDefault(MEANING_TOOLS, "&7Ferramentas");
                    yml.addDefault(MEANING_RANGED, "&7Distância");
                    yml.addDefault(MEANING_POTIONS, "&7Poções");
                    yml.addDefault(MEANING_SPECIALS, "&7Utilitários");
                    yml.addDefault(MEANING_COMPASS, "&7Bússola");
                    yml.addDefault(SEPARATOR_NAME, "&7↑ Categoria");
                    yml.addDefault(SEPARATOR_LORE, Collections.singletonList("&7↓ Hotbar"));
                default:
                    yml.addDefault(NO_PERMISSION, "&cYou do not have permission to use this command!");
                    yml.addDefault(INVENTORY_NAME, "HotBar Manager");
                    yml.addDefault(INVENTORY_ITEM_NAME, "&aHotbar Manager");
                    yml.addDefault(INVENTORY_ITEM_LORE, Arrays.asList("&7Edit preferred slots for", "&7any item in the shop.", "", "&eClick to edit!"));
                    yml.addDefault(INVENTORY_ITEMS_BLOCKS_NAME, "&aBlocks");
                    yml.addDefault(INVENTORY_ITEMS_MELEE_NAME, "&aMelee");
                    yml.addDefault(INVENTORY_ITEMS_TOOLS_NAME, "&aTools");
                    yml.addDefault(INVENTORY_ITEMS_RANGED_NAME, "&aRanged");
                    yml.addDefault(INVENTORY_ITEMS_POTIONS_NAME, "&aPotions");
                    yml.addDefault(INVENTORY_ITEMS_SPECIALS_NAME, "&aUtility");
                    yml.addDefault(INVENTORY_ITEMS_COMPASS_NAME, "&aCompass");
                    yml.addDefault(INVENTORY_ITEMS_COMPASS_LORE, Arrays.asList("&7Drag this to the slot your", "&7compass will be set on spawn.", "", "&cIf no slot has a compass, you", "&cwill not be given one.", "", "&eClick to drag!"));
                    yml.addDefault(INVENTORY_ITEMS_LORE, Arrays.asList("&7Drag this to a hotbar slot below", "&7to favor that slot when", "&7purchasing an item in this", "&7category or on spawn.", "", "&eClick to drag!"));
                    yml.addDefault(INVENTORY_ITEMS_USED_LORE, Arrays.asList("&7{category} &7items will prioritize this", "&7slot!", "", "&eClick to remove!"));
                    yml.addDefault(INVENTORY_ITEMS_BACK_LOBBY_NAME, "&aGo Back");
                    yml.addDefault(INVENTORY_ITEMS_BACK_LOBBY_LORE, Collections.singletonList("&7To Play BedWars"));
                    yml.addDefault(INVENTORY_ITEMS_PLACEHOLDER_NAME, "&7Slot &e{slot}");
                    yml.addDefault(INVENTORY_ITEMS_PLACEHOLDER_LORE, Arrays.asList("&7Drag an item here to", "&7prioritize this slot."));
                    yml.addDefault(INVENTORY_ITEMS_BACK_QUICK_BUY_NAME, "&aGo Back");
                    yml.addDefault(INVENTORY_ITEMS_BACK_QUICK_BUY_LORE, Collections.singletonList("&7To Quick Buy."));
                    yml.addDefault(INVENTORY_ITEMS_RESET_NAME, "&cReset to Default");
                    yml.addDefault(INVENTORY_ITEMS_RESET_LORE, Arrays.asList("&7Reset your hotbar to the", "&7default."));
                    yml.addDefault(INVENTORY_CURRENT_PAGE_ITEM_NAME, "&aCurrent Page: &e{page}");
                    yml.addDefault(INVENTORY_CURRENT_PAGE_ITEM_LORE, Collections.singletonList("&7Shows the current page you are on."));
                    yml.addDefault(INVENTORY_NEXT_PAGE_ITEM_NAME, "&aNext Page: &e{page}");
                    yml.addDefault(INVENTORY_NEXT_PAGE_ITEM_LORE, Collections.singletonList("&7Click to go to the next page."));
                    yml.addDefault(INVENTORY_PREVIOUS_PAGE_ITEM_NAME, "&aPrevious Page: &e{page}");
                    yml.addDefault(INVENTORY_PREVIOUS_PAGE_ITEM_LORE, Collections.singletonList("&7Click to go to the previous page."));
                    yml.addDefault(MEANING_BLOCKS, "&7Blocks");
                    yml.addDefault(MEANING_MELEE, "&7Melee");
                    yml.addDefault(MEANING_TOOLS, "&7Tools");
                    yml.addDefault(MEANING_RANGED, "&7Ranged");
                    yml.addDefault(MEANING_POTIONS, "&7Potions");
                    yml.addDefault(MEANING_SPECIALS, "&7Specials");
                    yml.addDefault(MEANING_COMPASS, "&7Compass");
                    yml.addDefault(SEPARATOR_NAME, "&7↑ Categories");
                    yml.addDefault(SEPARATOR_LORE, Collections.singletonList("&7↓ Hotbar"));
                    break;
            }
            yml.options().copyDefaults(true);
            l.save();
        }
    }
}
