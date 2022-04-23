package fr.hyriode.runner.game.team;

import fr.hyriode.hyrame.game.team.HyriGameTeam;
import fr.hyriode.hyrame.scoreboard.team.HyriScoreboardTeam;
import fr.hyriode.runner.HyriRunner;
import fr.hyriode.runner.game.RunnerGameType;

public class RunnerGameTeam extends HyriGameTeam {

    private final HyriRunner plugin;

    public RunnerGameTeam(HyriRunner plugin, RunnerGameTeams gameTeam, int size) {
        super(plugin.getGame(), gameTeam.getName(), gameTeam.getDisplayName(), gameTeam.getColor(), false, HyriScoreboardTeam.NameTagVisibility.ALWAYS, size);
        this.plugin = plugin;
    }

}
