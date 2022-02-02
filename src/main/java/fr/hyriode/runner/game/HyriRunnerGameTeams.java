package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;

import java.util.function.Supplier;

public enum HyriRunnerGameTeams {

    BLUE("blue", HyriGameTeamColor.BLUE),
    RED("red", HyriGameTeamColor.RED),
    GREEN("green", HyriGameTeamColor.GREEN),
    YELLOW("yellow", HyriGameTeamColor.YELLOW),
    AQUA("aqua", HyriGameTeamColor.CYAN),
    PINK("pink", HyriGameTeamColor.PINK),
    WHITE("white", HyriGameTeamColor.WHITE),
    GRAY("gray", HyriGameTeamColor.GRAY),
    DARK_GREEN("darkgreen", HyriGameTeamColor.DARK_GREEN),
    ORANGE("orange", HyriGameTeamColor.ORANGE),
    BLACK("black", HyriGameTeamColor.BLACK),
    PURPLE("purple", HyriGameTeamColor.PURPLE),
    ;

    private String teamName;
    private HyriGameTeamColor teamColor;
    private final Supplier<HyriLanguageMessage> displayName;

    HyriRunnerGameTeams(String teamName, HyriGameTeamColor teamColor) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.displayName = () -> HyriRunner.getLanguageManager().getMessage("team." + this.teamName + ".display");
    }

    public String getName() {
        return this.teamName;
    }

    public HyriGameTeamColor getColor() {
        return this.teamColor;
    }

    public HyriLanguageMessage getDisplayName() {
        return this.displayName.get();
    }
}
