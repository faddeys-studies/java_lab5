package tictactoe.game;


public enum Player {
    CROSSES, ZEROS;

    public Player opponent() {
        return (this == CROSSES) ? ZEROS : CROSSES;
    }

    @Override
    public String toString() {
        return (this == CROSSES) ? "X": "0";
    }

    public static Player parse(String str) {
        if ("X".equals(str)) {
            return Player.CROSSES;
        }
        if ("0".equals(str)) {
            return Player.ZEROS;
        }
        throw new RuntimeException();
    }
}
