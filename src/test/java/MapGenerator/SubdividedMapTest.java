package MapGenerator;

import tank1990.core.MapGenerator;
import tank1990.tile.Tile;

public class SubdividedMapTest {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Subdivided Map Generation");
            System.out.println("=================================");
            
            // Path to the existing map file
            String mapPath = "src/test/java/MapGenerator/map-stage-01.txt";
            
            // Test the new subdivided map creation
            System.out.println("Creating subdivided map from: " + mapPath);
            Tile[][] subdividedMap = MapGenerator.createSubdividedMap(mapPath);
            
            System.out.println("Map dimensions: " + subdividedMap.length + "x" + subdividedMap[0].length);
            System.out.println("Expected: 52x52 (13x13 with 4x4 subdivisions)");
            
            // Test different grid display styles
            System.out.println("\n1. Full Subdivided Grid (52x52 with single letters):");
            MapGenerator.printGrid(subdividedMap);
            
            System.out.println("\n2. Subdivided Grid with Tile Boundaries:");
            MapGenerator.printGridWithBoundaries(subdividedMap);
            
            System.out.println("\n3. Visual Grid (Unicode characters):");
            MapGenerator.printVisualGrid(subdividedMap);
            
            System.out.println("\n4. Compact Overview (13x13 base tiles):");
            MapGenerator.printCompactGrid(subdividedMap);
            
            // Test coordinate conversion
            System.out.println("\nTesting coordinate conversion:");
            int[] baseCoords = MapGenerator.subdividedToBase(20, 16);
            System.out.println("Subdivided (20, 16) -> Base (" + baseCoords[0] + ", " + baseCoords[1] + ")");
            
            int[] subdivCoords = MapGenerator.baseToSubdivided(5, 4);
            System.out.println("Base (5, 4) -> Subdivided (" + subdivCoords[0] + ", " + subdivCoords[1] + ")");
            
            // Count non-null tiles
            int tileCount = 0;
            for (int i = 0; i < subdividedMap.length; i++) {
                for (int j = 0; j < subdividedMap[i].length; j++) {
                    if (subdividedMap[i][j] != null) {
                        tileCount++;
                    }
                }
            }
            System.out.println("\nTotal tiles in subdivided map: " + tileCount);
            
        } catch (Exception e) {
            System.err.println("Error testing subdivided map: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
