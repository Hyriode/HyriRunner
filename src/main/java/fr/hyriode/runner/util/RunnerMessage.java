package fr.hyriode.runner.util;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum RunnerMessage {

    PREPARATION("message.preparation"),
    INVINCIBILITY("message.invincibility"),
    BORDER_SHRINK("message.border-shrink"),
    DAMAGE_ON("message.damage-on"),
    BORDER_END("message.border-end"),
    PVP_INCOMING("message.pvp-incoming"),
    PVP_ON("message.pvp-on"),
    BONUS_SELECTED("message.bonus-selected"),
    ARRIVED_TITLE("title.arrived"),
    ARRIVED_SUB("subtitle.arrived"),
    CHALLENGE_FAILED("message.challenge-failed"),
    CHALLENGE_COMPLETED("message.challenge-completed"),
    LAST_CHALLENGE_USED("message.last-challenge-used"),
    INIT_TELEPORTATION("message.teleportation-init"),

    TELEPORTATION_BAR("teleportation.action-bar"),
    ARROW_BAR("arrow.action-bar"),
    PLAYER_TRACKER_BAR("player-tracker.action-bar"),

    CHALLENGE_GUI_NAME("gui.challenge.name"),
    CHALLENGE_ITEM_LORE("challenge.item.lore"),
    CHALLENGE_NONE_ITEM_NAME("challenge.none.item.name"),
    CHALLENGE_DIFFICULTY_ITEM_NAME("challenge.difficulty.item.name"),
    CHALLENGE_RANDOM_ITEM_NAME("challenge.random.item.name"),
    CHALLENGE_FAVORITES_ITEM_NAME("challenge.favorites.item.name"),
    CHALLENGE_SELECTED_LINE("challenge.selected.line"),
    CHALLENGE_SELECT_LINE("challenge.select.line"),
    CHALLENGE_ADD_FAVORITE_LINE("challenge.add-favorite.line"),
    CHALLENGE_REMOVE_FAVORITE_LINE("challenge.remove-favorite.line"),

    ;

    private final String key;
    private HyriLanguageMessage languageMessage;


    RunnerMessage(String key) {
        this.key = key;
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return this.asLang().getValue(account);
    }

    public String asString(Player player) {
        return this.asLang().getValue(player);
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return this.asList(IHyriPlayer.get(player.getUniqueId()));
    }

}
