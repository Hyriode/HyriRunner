package fr.hyriode.runner.game.team;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;

import java.util.function.Supplier;

public enum RunnerGameTeam {

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

    private final String teamName;
    private final HyriGameTeamColor teamColor;
    private final Supplier<HyriLanguageMessage> displayName;

    RunnerGameTeam(String teamName, HyriGameTeamColor teamColor) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.displayName = () -> HyriLanguageMessage.get("team." + this.teamName + ".display");
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
