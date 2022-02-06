package fr.hyriode.runner.game;

import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;

public enum HyriRunnerMessages {

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
    INIT_TELEPORTATION("message.teleportation-init")
    ;

    private final String key;

    HyriRunnerMessages(String key) {
        this.key = key;
    }

    public HyriLanguageMessage get() {
        return HyriRunner.getLanguageManager().getMessage(this.key);
    }
}
