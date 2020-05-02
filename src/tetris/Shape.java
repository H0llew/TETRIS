package tetris;

import javafx.scene.paint.Color;

public class Shape {

    private final int[] blocks;
    private final String name;
    private final Color color;

    public Shape(int[] blocks, String name, Color color) {
        this.blocks = blocks;
        this.name = name;
        this.color = color;
    }

    public void rotate() {
        int N = (int) Math.sqrt(blocks.length);
        int[] rotatedShape = new int[blocks.length];

        for (int n = 0; n < N; n++) {
            int row = 0;
            int collum = N - 1 - n;
            for (int x = 0; x < N; x++) {
                rotatedShape[N * row + collum] = blocks[N * n + x];
                row++;
            }
        }

        System.arraycopy(rotatedShape, 0, blocks, 0, blocks.length);
    }

    public int[] getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        int length = (int) (Math.sqrt(blocks.length));
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                toString.append(blocks[y * length + x]);
            }
            toString.append("\n");
        }
        return toString.toString();
    }
}
