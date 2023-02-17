package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.HyriGameType;

public enum RunnerGameType implements HyriGameType {

    SOLO("Solo", 1, 6, 12),
    DOUBLES("Doubles", 2, 8, 24),
    ;

    private final String displayName;
    private final int teamSize;
    private final int minPlayers;
    private final int maxPlayers;

    RunnerGameType(String displayName, int teamSize, int minPlayers, int maxPlayers) {
        this.displayName = displayName;
        this.teamSize = teamSize;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public int getMinPlayers() {
        return this.minPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getTeamSize() {
        return this.teamSize;
    }

}
