package inkball;

public class Broken_Wall extends Sprite {
    private int hitCount;
    private boolean isDestroyed;

    public Broken_Wall(int x, int y, char type) {
        super(x, y, type);
        this.hitCount = 0;
        this.isDestroyed = false;
    }

    public void hit() {
        hitCount++;
        if (hitCount >= 3) {
            isDestroyed = true;
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public int getHitCount() {
        return hitCount;
    }

    public char get_broken_wall_type() {
        return type;
    }

    public void set_broken_wall_type(char type) {
        this.type = type;
    }

    public int getGridX() {
        return X_pixel_to_index(x);
    }

    public int getGridY() {
        return Y_pixel_to_index(y);
    }
}