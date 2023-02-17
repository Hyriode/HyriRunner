package fr.hyriode.runner.util;

import fr.hyriode.hyrame.item.ItemHead;

/**
 * Created by AstFaster
 * on 21/08/2022 at 18:41
 */
public enum RunnerHead implements ItemHead {

    DICE("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg4MWNjMjc0N2JhNzJjYmNiMDZjM2NjMzMxNzQyY2Q5ZGUyNzFhNWJiZmZkMGVjYjE0ZjFjNmE4YjY5YmM5ZSJ9fX0=")

    ;

    private final String texture;

    RunnerHead(String texture) {
        this.texture = texture;
    }

    @Override
    public String getTexture() {
        return this.texture;
    }

}
