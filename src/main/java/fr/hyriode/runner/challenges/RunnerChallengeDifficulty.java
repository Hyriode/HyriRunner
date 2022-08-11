package fr.hyriode.runner.challenges;

import fr.hyriode.api.color.HyriChatColor;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyriode
 * Created by Akkashi
 * on 30/06/2022 at 13:25
 */
public enum RunnerChallengeDifficulty {

    EASY(1, DyeColor.GREEN),
    MODERATE( 2, DyeColor.YELLOW),
    NORMAL(3, DyeColor.ORANGE),
    HARD(4, DyeColor.RED),
    EXTREME(5, DyeColor.PURPLE),

    ;

    private final int stars;
    private final DyeColor color;

    RunnerChallengeDifficulty(int stars, DyeColor color) {
        this.stars = stars;
        this.color = color;
    }

    public String getId() {
        return this.name().toLowerCase();
    }

    public int getStars() {
        return this.stars;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public String getAsString(final Player player) {
        switch (this) {
            case EASY:
                return ChatColor.YELLOW + "✮" + ChatColor.GRAY + "✮✮✮✮";
            case MODERATE:
                return ChatColor.YELLOW + "✮✮" + ChatColor.GRAY + "✮✮✮";
            case NORMAL:
                return ChatColor.YELLOW + "✮✮✮" + ChatColor.GRAY + "✮✮";
            case HARD:
                return ChatColor.YELLOW + "✮✮✮✮" + ChatColor.GRAY + "✮";
            case EXTREME:
                return ChatColor.YELLOW + "✮✮✮✮✮";
        }
        return ChatColor.GRAY + "✮✮✮✮✮";
    }
}
