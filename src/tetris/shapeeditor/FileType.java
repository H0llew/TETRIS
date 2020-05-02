package tetris.shapeeditor;

/**
 * Reprezentuje typ položky (soubor/tvar)
 */
public enum FileType {
    DIRECTORY, SHAPE;

    /**
     * Vrátí symbol souboru, pokud se jedná o soubor
     *
     * @return symbol souboru
     */
    public String getSymbol() {
        if (this == DIRECTORY) {
            return "\uD83D\uDDC0";
        }
        return "";
    }

}
