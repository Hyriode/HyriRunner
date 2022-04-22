package fr.hyriode.runner.game.teleport;

public class RunnerMapChunk {

    private int x;
    private int z;

    public RunnerMapChunk(int x, int z) {
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