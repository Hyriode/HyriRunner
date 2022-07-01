package fr.hyriode.runner.game;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;
import org.bukkit.entity.Player;

public enum RunnerMessage {

    PREPARATION("message.preparation"),
    BORDER_SHRINK("message.border-shrink"),
    DAMAGE_ON("message.damage-on"),
    BORDER_END("message.border-end"),
    PVP_ON("message.pvp-on"),
    FIRST_PLACE_SWORD("message.first-place-sword"),
    FIRST_PLACE_ARMOR("message.first-place-armor"),
    ARRIVED_TITLE("title.arrived"),
    ARRIVED_SUB("subtitle.arrived"),
    CHALLENGE_FAILED("message.challenge-failed"),
    LAST_CHALLENGE_USED("message.last-challenge-used"),
    INIT_TELEPORTATION("message.teleportation-init"),

    GUI_CHALLENGE_DIFFICULTY("gui.challenge.difficulty"),
    GUI_CHALLENGE_SELECTED("gui.challenge.selected"),
    GUI_CHALLENGE_SELECT("gui.challenge.select"),

    CHALLENGE_CHOSEN_MESSAGE("message.challenge.selected"),
    ;

    private final String key;
    private HyriLanguageMessage languageMessage;


    RunnerMessage(String key) {
        this.key = key;
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(Player player) {
        return this.asLang().getForPlayer(player);
    }
}
