package fr.hyriode.runner.game;

import fr.hyriode.hyrame.game.HyriGameType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public enum HyriRunnerGameType implements HyriGameType {

    SOLO("solo", 1),
    DOUBLES("doubles", 2),
    ;

    private final String name;
    private final int teamSize;
    private static HyriRunnerGameType currentType;

    HyriRunnerGameType(String name, int teamSize) {
        this.name = name;
        this.teamSize = teamSize;
    }

    public static void setCurrentType(HyriRunnerGameType type) {
        currentType = type;
    }

    public static HyriRunnerGameType getCurrentType() {
        return currentType;
    }

    public static Optional<HyriRunnerGameType> getByName(String name) {
        return Arrays.stream(values()).filter(hyriRunnerGameType -> hyriRunnerGameType.getName().equalsIgnoreCase(name)).findFirst();
    }

    public static void setWithName(String name) {
        Optional<HyriRunnerGameType> gameType = getByName(name);
        gameType.ifPresent(HyriRunnerGameType::setCurrentType);
    }

    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
