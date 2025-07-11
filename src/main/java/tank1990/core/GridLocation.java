package tank1990.core;

public record GridLocation(int x, int y) {

    @Override
    public String toString() {
        return String.format("GL(x:%d, y:%d)", x,y);
    }
}
