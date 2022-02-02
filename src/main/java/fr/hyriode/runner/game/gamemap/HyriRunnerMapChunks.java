package fr.hyriode.runner.game.gamemap;

public class HyriRunnerMapChunks {

    private int x;
    private int z;

    public HyriRunnerMapChunks(int x, int z) {
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