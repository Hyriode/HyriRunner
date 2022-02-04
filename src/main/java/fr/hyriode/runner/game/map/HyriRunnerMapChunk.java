package fr.hyriode.runner.game.map;

public class HyriRunnerMapChunk {

    private int x;
    private int z;

    public HyriRunnerMapChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

}