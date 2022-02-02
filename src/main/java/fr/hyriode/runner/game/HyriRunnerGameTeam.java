package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.game.team.HyriGameTeamColor;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.runner.HyriRunner;

public class HyriRunnerGameTeam extends HyriGameTeam {

    private final HyriRunner plugin;

    public HyriRunnerGameTeam(HyriRunner plugin, HyriRunnerGameTeams gameTeam) {
        super(gameTeam.getName(), gameTeam.getDisplayName(), gameTeam.getColor(), HyriRunnerGameType.getCurrentType().getTeamSize());
        this.plugin = plugin;
    }
}
