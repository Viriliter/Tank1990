package tank1990.core;

import java.io.Serializable;

public class GridSize implements Serializable  {
    private int width;
    private int height;
    
    public GridSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() { return this.width; }
    
    public int height() { return this.height; }

    public void width(int width) { this.width = width; }
    
    public void height(int height) { this.height = height; }
}
