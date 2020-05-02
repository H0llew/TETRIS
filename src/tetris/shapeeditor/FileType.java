package tetris.shapeeditor;

public enum FileType {
    DIRECTORY, SHAPE;

    public String getSymbol() {
        if (this == DIRECTORY) {
            return "\uD83D\uDDC0";
        }
        return "";
    }

}
