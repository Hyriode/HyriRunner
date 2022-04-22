package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.HyriGameType;

import java.util.Arrays;
import java.util.Optional;

public enum RunnerGameType implements HyriGameType {

    SOLO("solo", 1),
    DOUBLES("doubles", 2),
    ;

    private final String name;
    private final int teamSize;
    private static RunnerGameType currentType;

    RunnerGameType(String name, int teamSize) {
        this.name = name;
        this.teamSize = teamSize;
    }

    public static void setCurrentType(RunnerGameType type) {
        currentType = type;
    }

    public static RunnerGameType getCurrentType() {
        return currentType;
    }

    public static Optional<RunnerGameType> getByName(String name) {
        return Arrays.stream(values()).filter(hyriRunnerGameType -> hyriRunnerGameType.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static void setWithName(String name) {
        Optional<RunnerGameType> gameType = getByName(name);
        gameType.ifPresent(RunnerGameType::setCurrentType);
    }

    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
