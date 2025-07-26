package tank1990.powerup;

public enum PowerupType {
    POWERUP_GRENADE,
    POWERUP_HELMET,
    POWERUP_SHOVEL,
    POWERUP_STAR,
    POWERUP_TANK,
    POWERUP_TIMER;

    public static PowerupType valueOf(int val) {
        return switch (val) {
            case (0) -> POWERUP_GRENADE;
            case (1) -> POWERUP_HELMET;
            case (2) -> POWERUP_SHOVEL;
            case (3) -> POWERUP_STAR;
            case (4) -> POWERUP_TANK;
            case (5) -> POWERUP_TIMER;
            default -> throw new RuntimeException("Invalid Value for PowerupType");
        };
    }

}
