package tetris;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.leaderboard.LeaderboardData;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class TetrisManager {

    private static final int ZONE_WIDTH = 14;
    private static final int ZONE_HEIGHT = 24;

    private final int[] playZone = new int[ZONE_WIDTH * ZONE_HEIGHT];
    private Rectangle[] playZoneRects = new Rectangle[playZone.length];

    private final int cellSize;
    private final Pane gamePane;

    private static final Point2D STARTING_POS = new Point2D(4, -2);

    private Shape actualShape;
    private Rectangle[] actualShapeRects;
    private Point2D actualPos;

    private int tick = 0;

    private Shape[] shapes;

    private boolean isOver = false;

    public IntegerProperty score;
    public IntegerProperty level;

    List<Shape> nextShapes = new ArrayList<>();
    List<Rectangle[]> nextShapesRects = new ArrayList<>();

    private boolean scoreSaved = false;

    public TetrisManager(Pane pane, int cellSize) {
        this.cellSize = cellSize;
        this.gamePane = pane;

        shapes = generateBasicShapes();

        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);


        initPlayZone();

        generateNextShapes();
        generateNextShapesRects();
        drawNextShapesRects();
        addNSToPanel();

        initActualShape();
    }

    // UPDATE - DRAW

    public void nextStep() {
        if (!isOver) {
            update();
            draw();
        }
        else {
            removeFromPane();
            if (!scoreSaved) {
                writeToFile(new LeaderboardData("Player", score.getValue()));
                scoreSaved = true;
            }
        }
    }

    private void draw() {
        drawPlayZone();
        drawActualShape();
    }

    private void update() {
        if (tick == 50) {
            move(MoveDirection.DOWN);
            tick = 0;
        }

        tick++;
        score.setValue(score.getValue() + 1);
        //System.out.println(score.getValue());
    }

    // REMOVE FULL ROWS

    private void removeFullRows() {
        int[] indexes = new int[ZONE_HEIGHT];
        for (int y = 0; y < ZONE_HEIGHT; y++) {
            boolean full = true;
            for (int x = 1; x < ZONE_WIDTH - 1; x++) {
                if (playZone[y * ZONE_WIDTH + x] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {
                removeRow(y);
                pushDown(y);

                //printPlayZone();
                //initPlayZone();
                //printPlayZone();

                printPlayZoneRects();
                printPlayZone();


                removeFromPane();

                printPlayZoneRects();
                printPlayZone();

                createPlayZoneRects();

                printPlayZoneRects();
                printPlayZone();

                printPlayZoneRects();

                printPlayZoneRects();
                printPlayZone();

                addToPane();
            }
        }
    }

    private void removeRow(int y) {
        for (int x = 1; x < ZONE_WIDTH - 1; x++) {
            playZone[y * ZONE_WIDTH + x] = 0;
        }
    }

    private void pushDown(int y) {
        for (int y1 = y - 1; y1 >= 0; y1--) {
            for (int x = 1; x < ZONE_WIDTH - 1; x++) {
                int newY = y1 + 1;
                playZone[newY * ZONE_WIDTH + x] = playZone[y1 * ZONE_WIDTH + x];
                playZone[y1 * ZONE_WIDTH + x] = 0;
            }
        }
    }

    // LOCK SHAPE AND GET NEXT ONE

    private void lockCurrentShape() {
        Point2D nextPos = new Point2D(actualPos.getX(), actualPos.getY());
        int length = (int) (Math.sqrt(actualShape.getBlocks().length));

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                int index = y * length + x;
                if (actualShape.getBlocks()[index] != 0) {
                    int realIndex = (int) (((y + nextPos.getY()) * ZONE_WIDTH) + (x + nextPos.getX()));
                    if (!(realIndex < 0)) {
                        playZone[realIndex] = actualShape.getBlocks()[index];
                    }
                    else {
                        // GAME OVER
                        isOver = true;
                    }
                    //System.out.println(realIndex);
                }
            }
        }

        initPlayZone();
        initActualShape();
        removeFullRows();
        initPlayZone();
    }

    // MOVE ACTUAL SHAPE

    public void move(MoveDirection direction) {
        if (canMove(direction)) {
            moveCurrentShape(direction);
        } else {
            if (direction == MoveDirection.DOWN) {
                lockCurrentShape();
                //removeFullRows();
            }
        }
    }

    private void moveCurrentShape(MoveDirection direction) {
        switch (direction) {
            case LEFT:
                // move left
                actualPos = new Point2D(actualPos.getX() - 1, actualPos.getY());
                break;
            case RIGHT:
                // move right
                actualPos = new Point2D(actualPos.getX() + 1, actualPos.getY());
                break;
            case DOWN:
                // move down
                actualPos = new Point2D(actualPos.getX(), actualPos.getY() + 1);
                break;
        }
    }

    private boolean canMove(MoveDirection direction) {
        Point2D nextPos = new Point2D(actualPos.getX(), actualPos.getY());
        int length = (int) (Math.sqrt(actualShape.getBlocks().length));

        switch (direction) {
            case LEFT:
                // move left
                nextPos = new Point2D(actualPos.getX() - 1, actualPos.getY());
                break;
            case RIGHT:
                // move right
                nextPos = new Point2D(actualPos.getX() + 1, actualPos.getY());
                break;
            case DOWN:
                // move down
                nextPos = new Point2D(actualPos.getX(), actualPos.getY() + 1);
                break;
        }

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                int index = y * length + x;
                if (actualShape.getBlocks()[index] != 0) {
                    int realIndex = (int) (((y + nextPos.getY()) * ZONE_WIDTH) + (x + nextPos.getX()));
                    if (realIndex < 0 || realIndex > ZONE_HEIGHT * ZONE_WIDTH) {
                        return false;
                    }
                    if (playZone[realIndex] != 0) {
                        return false;
                    }
                    //System.out.println(realIndex);
                }
            }
        }

        return true;
    }

    public void rotateActualShape() {
        if (canRotate()) {
            actualShape.rotate();
            removeASFromPanel();
            actualShapeRects = createActualShapeRects();
            addASToPanel();
        }
    }

    private boolean canRotate() {
        Point2D nextPos = new Point2D(actualPos.getX(), actualPos.getY());
        int length = (int) (Math.sqrt(actualShape.getBlocks().length));

        int[] blocks = new int[actualShape.getBlocks().length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = actualShape.getBlocks()[i];
        }
        Shape testShape = new Shape(blocks, "", actualShape.getColor());
        testShape.rotate();

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                int index = y * length + x;
                if (testShape.getBlocks()[index] != 0) {
                    int realIndex = (int) (((y + nextPos.getY()) * ZONE_WIDTH) + (x + nextPos.getX()));
                    if (realIndex < 0 || realIndex > ZONE_HEIGHT * ZONE_WIDTH) {
                        return false;
                    }
                    if (playZone[realIndex] != 0) {
                        return false;
                    }
                    //System.out.println(realIndex);
                }
            }
        }

        return true;
    }

    // SHOW ACTUAL SHAPE

    public void drawActualShape() {
        int length = (int) (Math.sqrt(actualShapeRects.length));
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                int index = y * length + x;
                Rectangle rectangle = actualShapeRects[index];
                if (rectangle != null) {
                    int xPos = (int) (x + actualPos.getX()) * cellSize;
                    int yPos = (int) (y + actualPos.getY()) * cellSize;

                    //System.out.println("[" + x + "," + y + "]");

                    rectangle.setX(xPos);
                    rectangle.setY(yPos);
                }
            }
        }
    }

    private void addASToPanel() {
        for (Rectangle rectangle : actualShapeRects) {
            if (rectangle != null)
                gamePane.getChildren().add(rectangle);
        }
    }

    private void removeASFromPanel() {
        if (actualShapeRects != null) {
            for (Rectangle rectangle : actualShapeRects) {
                if (rectangle != null)
                    gamePane.getChildren().remove(rectangle);
            }
        }
    }

    // INIT ACTUAL SHAPE

    private void initActualShape() {
        removeASFromPanel();
        //actualShape = createNewShape();
        Shape ref = nextShapes.get(0);
        int[] blocks = new int[ref.getBlocks().length];
        for (int i = 0; i < ref.getBlocks().length; i++) {
            blocks[i] = ref.getBlocks()[i];
        }
        actualShape = new Shape(blocks, ref.getName(), ref.getColor());

        removeNSFromPanel();
        nextShapes.remove(0);
        nextShapes.add(createNewShape());
        generateNextShapesRects();
        drawNextShapesRects();
        addNSToPanel();

        actualShapeRects = createActualShapeRects();
        actualPos = new Point2D(STARTING_POS.getX(), STARTING_POS.getY());
        addASToPanel();
    }

    private Shape createNewShape() {
        double random = Math.random();
        double step = 1d / shapes.length;
        double stepNext = step;

        int count = 0;

        while (stepNext < random) {
            stepNext += step;
            count++;
        }

        int[] blocks = new int[shapes[count].getBlocks().length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = shapes[count].getBlocks()[i];
        }

        return new Shape(blocks, shapes[count].getName(), shapes[count].getColor());
    }

    private Rectangle[] createActualShapeRects() {
        int[] blocks = actualShape.getBlocks();
        int width = (int) (Math.sqrt(blocks.length));
        Rectangle[] rects = new Rectangle[blocks.length];

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                if (blocks[y * width + x] != 0) {
                    Rectangle rectangle = new Rectangle(cellSize, cellSize);
                    rectangle.setFill(actualShape.getColor());
                    rects[y * width + x] = rectangle;
                } else {
                    rects[y * width + x] = null;
                }
            }
        }

        return rects;
    }

    // INIT NEXT SHAPES

    private void generateNextShapes() {
        for (int i = 0; i < 3; i++) {
            nextShapes.add(createNewShape());
        }
    }

    private void generateNextShapesRects() {
        nextShapesRects = new ArrayList<>();
        for (Shape shape : nextShapes) {
            int[] blocks = shape.getBlocks();
            int width = (int) (Math.sqrt(blocks.length));
            Rectangle[] rects = new Rectangle[blocks.length];

            for (int y = 0; y < width; y++) {
                for (int x = 0; x < width; x++) {
                    if (blocks[y * width + x] != 0) {
                        Rectangle rectangle = new Rectangle(cellSize / 2d, cellSize / 2d);
                        rectangle.setFill(shape.getColor());
                        rects[y * width + x] = rectangle;
                    } else {
                        rects[y * width + x] = null;
                    }
                }
            }

            nextShapesRects.add(rects);
        }
    }

    private void drawNextShapesRects() {
        int count = 0;
        for (Rectangle[] rects : nextShapesRects) {
            int length = (int) (Math.sqrt(rects.length));
            for (int y = 0; y < length; y++) {
                for (int x = 0; x < length; x++) {
                    int index = y * length + x;
                    Rectangle rectangle = rects[index];
                    if (rectangle != null) {
                        int xPos = (int) ((x + (ZONE_WIDTH + 15)) * (cellSize / 2d));
                        int yPos = (int) ((y + (count * 4 + 2)) * (cellSize / 2d));


                        //System.out.println("[" + x + "," + y + "]");

                        rectangle.setX(xPos);
                        rectangle.setY(yPos);
                    }
                }
            }
            count++;
        }
    }

    private void addNSToPanel() {
        for (Rectangle[] rects : nextShapesRects) {
            for (Rectangle rectangle : rects) {
                if (rectangle != null)
                    gamePane.getChildren().add(rectangle);
            }
        }
    }

    private void removeNSFromPanel() {
        if (nextShapesRects != null) {
            for (Rectangle[] rects : nextShapesRects) {
                for (Rectangle rectangle : rects) {
                    if (rectangle != null)
                        gamePane.getChildren().remove(rectangle);
                }
            }
        }
    }

    // SHOW PLAY ZONE

    public void drawPlayZone() {
        for (int y = 0; y < ZONE_HEIGHT; y++) {
            for (int x = 0; x < ZONE_WIDTH; x++) {
                int index = y * ZONE_WIDTH + x;
                Rectangle rectangle = playZoneRects[index];
                if (rectangle != null) {
                    int xPos = x * cellSize;
                    int yPos = y * cellSize;

                    //System.out.println("[" + x + "," + y + "]");

                    rectangle.setX(xPos);
                    rectangle.setY(yPos);
                }
            }
        }
    }

    private void removeFromPane() {
        if (playZoneRects != null) {
            for (Rectangle rectangle : playZoneRects) {
                if (rectangle != null)
                    gamePane.getChildren().remove(rectangle);
            }
        }
    }

    private void addToPane() {
        for (Rectangle rectangle : playZoneRects) {
            if (rectangle != null)
                gamePane.getChildren().add(rectangle);
        }
    }

    // INIT PLAY ZONE

    private void initPlayZone() {
        removeFromPane();
        createBorders();
        createPlayZoneRects();
        drawPlayZone();
        addToPane();
    }

    private void createBorders() {
        for (int y = 0; y < ZONE_HEIGHT; y++) {
            for (int x = 0; x < ZONE_WIDTH; x++) {
                if (x == 0 || x == ZONE_WIDTH - 1)
                    playZone[y * ZONE_WIDTH + x] = 1;
            }
        }
    }

    private void createPlayZoneRects() {
        for (int y = 0; y < ZONE_HEIGHT; y++) {
            for (int x = 0; x < ZONE_WIDTH; x++) {
                if (playZone[y * ZONE_WIDTH + x] != 0) {
                    Rectangle rectangle = new Rectangle(cellSize, cellSize);
                    rectangle.setFill(shapes[playZone[y * ZONE_WIDTH + x] - 1].getColor());
                    playZoneRects[y * ZONE_WIDTH + x] = rectangle;
                } else {
                    playZoneRects[y * ZONE_WIDTH + x] = null;
                }
            }
        }
    }

    // GET-SET

    public Rectangle[] getPlayZoneRects() {
        return playZoneRects;
    }

    // WRITE

    private void writeToFile(LeaderboardData data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("data.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();

            System.out.println("TASK SUCCESFULL");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DEBUG METHODS

    private Shape[] generateBasicShapes() {
        return new Shape[]{
                new Shape(new int[]{0, 0, 0, 0,
                        0, 1, 1, 1,
                        0, 0, 1, 0,
                        0, 0, 0, 0}, "1", Color.BLUE),

                new Shape(new int[]{0, 0, 0, 0,
                        0, 2, 2, 0,
                        0, 2, 0, 0,
                        0, 2, 0, 0}, "2", Color.RED),

                new Shape(new int[]{0, 0, 0, 0,
                        0, 0, 3, 0,
                        0, 3, 3, 0,
                        0, 3, 0, 0}, "3", Color.YELLOW),

                new Shape(new int[]{0, 0, 0, 0,
                        0, 4, 4, 0,
                        0, 4, 4, 0,
                        0, 0, 0, 0}, "4", Color.GREEN),

                new Shape(new int[]{0, 0, 0, 0,
                        0, 5, 5, 5,
                        0, 5, 0, 0,
                        0, 0, 0, 0}, "5", Color.PINK),

                new Shape(new int[]{0, 0, 0, 0,
                        0, 0, 6, 6,
                        0, 6, 6, 0,
                        0, 0, 0, 0}, "6", Color.KHAKI),

                new Shape(new int[]{0, 0, 0, 0,
                        7, 7, 7, 7,
                        0, 0, 0, 0,
                        0, 0, 0, 0}, "7", Color.BROWN),
        };
    }

    public void printPlayZone() {
        StringBuilder print = new StringBuilder();
        for (int y = 0; y < ZONE_HEIGHT; y++) {
            for (int x = 0; x < ZONE_WIDTH; x++) {
                print.append(playZone[y * ZONE_WIDTH + x]);
            }
            print.append("\n");
        }
        System.out.println(print.toString());
    }

    public void printPlayZoneRects() {
        StringBuilder print = new StringBuilder();
        for (int y = 0; y < ZONE_HEIGHT; y++) {
            for (int x = 0; x < ZONE_WIDTH; x++) {
                if (playZoneRects[y * ZONE_WIDTH + x] != null)
                    print.append("1");
                else
                    print.append("0");
            }
            print.append("\n");
        }
        System.out.println(print.toString());
    }
}
