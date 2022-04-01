package fr.hyriode.runner.game.team;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.HyriRunnerGameType;

public class HyriRunnerGameTeam extends HyriGameTeam {

    private final HyriRunner plugin;

    public HyriRunnerGameTeam(HyriRunner plugin, HyriRunnerGameTeams gameTeam) {
        super(plugin.getGame(), gameTeam.getName(), gameTeam.getDisplayName(), gameTeam.getColor(), false, HyriScoreboardTeam.NameTagVisibility.ALWAYS, HyriRunnerGameType.getCurrentType().getTeamSize());
        this.plugin = plugin;
    }

}