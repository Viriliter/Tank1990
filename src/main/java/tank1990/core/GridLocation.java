package tank1990.core;

import java.io.Serializable;

public class GridLocation implements Serializable {
    private final int rowIndex;
    private final int colIndex;

    public GridLocation(int rowIndex, int colIndex) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public int rowIndex() {
        return rowIndex;
    }

    public int colIndex() {
        return colIndex;
    }

    @Override
    public String toString() {
        return String.format("GL(r:%d, c:%d)", rowIndex, colIndex);
    }
}
