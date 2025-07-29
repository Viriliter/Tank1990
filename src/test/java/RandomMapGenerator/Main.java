package RandomMapGenerator;

import tank1990.core.LevelInfo;
import tank1990.core.MapGenerator;

public class Main {
    public static void main(String[] args) {
        LevelInfo levelInfo = MapGenerator.generateRandomLevelInfo();
        MapGenerator.printGrid(levelInfo.levelGrid);
        
        for (TankType tt: levelInfo.enemyTankCount.keySet()) {
            System.out.printf("%s: %d\n", tt.name(), levelInfo.enemyTankCount.get(tt));
        }
    }
}
