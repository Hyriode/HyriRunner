package fr.hyriode.runner.challenge;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 30/06/2022 at 13:25
 */
public enum RunnerChallengeDifficulty {

    EASY(1, DyeColor.LIME),
    NORMAL( 2, DyeColor.YELLOW),
    MEDIUM(3, DyeColor.ORANGE),
    HARD(4, DyeColor.RED),
    EXTREME(5, DyeColor.PURPLE)

    ;

    private final int stars;
    private final DyeColor color;

    RunnerChallengeDifficulty(int stars, DyeColor color) {
        this.stars = stars;
        this.color = color;
    }

    public int getStars() {
        return this.stars;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public String asString() {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            builder.append(i + 1 <= this.stars ? ChatColor.YELLOW : ChatColor.GRAY).append("✮");

        }
        return builder.toString();
    }

}
