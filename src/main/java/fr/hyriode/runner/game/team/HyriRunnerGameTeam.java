package fr.hyriode.runner.game.team;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerGameType;

public class HyriRunnerGameTeam extends HyriGameTeam {

    private final HyriRunner plugin;

    public HyriRunnerGameTeam(HyriRunner plugin, HyriRunnerGameTeams gameTeam) {
        super(gameTeam.getName(), gameTeam.getDisplayName(), gameTeam.getColor(), HyriRunnerGameType.getCurrentType().getTeamSize());
        this.plugin = plugin;
    }

}
