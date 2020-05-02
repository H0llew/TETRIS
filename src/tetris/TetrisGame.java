package tetris;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tetris.leaderboard.Leaderboard;
import tetris.leaderboard.LeaderboardData;
import tetris.shapeeditor.FileType;
import tetris.shapeeditor.ShapeEditor;
import tetris.shapeeditor.TreeViewItem;
import tetris.tetrisengine.MoveDirection;
import tetris.tetrisengine.Shape;
import tetris.tetrisengine.TetrisManager;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Hlavní bod aplikace. Stará se o přechod mezi scénami, vytváří nové stage a reprezentuje také hlavní menu
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
public class TetrisGame extends Application {

    // výška menu scény
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 450;

    private static final String GAME_START_TEXT = "PLAY";
    private static final String SHAPE_EDITOR_TEXT = "Editor";
    private static final String LEADERBOARD_TEXT = "Leaderboard";
    private static final String OPTIONS_TEXT = "Options";
    private static final String EXIT_GAME = "Exit Game";

    private static final String SHAPE_PATH = "shapes";
    private static final String SCORE_PATH = "scores";

    private Image background = new Image("/tetrisMenu.png");
    private Image hint = new Image("/Hint.png");

    private Stage mainMenuStage;
    private Stage tetrisStage;
    private Stage editorStage;

    // editor tvarů
    private ShapeEditor shapeEditor;

    /**
     * Vstupní bod programu
     *
     * @param args nevyužité
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.mainMenuStage = stage;
        setMainMenuScene();

        shapeEditor = new ShapeEditor(4, SHAPE_PATH);
        //stage.setScene(new Scene(leaderboard.Leaderboard.getLeaderboard().create()));

        stage.show();
    }

    // STAGES

    /**
     * Vytvoří novou stage s tetrisem
     *
     * @param playSet herní set pro tetris
     */
    public void setTetrisStage(ArrayList<Shape> playSet) {
        tetrisStage = new Stage();

        BorderPane root = new BorderPane();

        Pane gamePane = getTetrisPane();
        TetrisManager tetris = new TetrisManager(gamePane, 20, playSet, SCORE_PATH);

        root.setCenter(gamePane);

        Label label1 = new Label("");
        label1.setFont(Font.font("arials", FontWeight.BOLD, 25));
        label1.textProperty().bind(tetris.score.asString("Score: %05d"));
        Label label2 = new Label("");
        label2.setFont(Font.font("arials", FontWeight.BOLD, 15));
        label2.textProperty().bind(tetris.level.asString("Level: %03d"));

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(label1, label2);

        ImageView imageView = new ImageView(hint);
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.BOTTOM_LEFT);
        stackPane.getChildren().add(imageView);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(vBox, imageView);

        AnchorPane.setBottomAnchor(imageView, 0d);
        AnchorPane.setRightAnchor(imageView, 0d);

        root.setRight(anchorPane);

        Scene scene = new Scene(root);
        createInputHandler(scene, tetris);

        tetrisStage.setScene(scene);

        tetrisStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                tetris.nextStep();
            }
        }.start();
    }

    /**
     * Vytvoří nový stage s editorem tvarů
     */
    public void setShapeEditorStage() {
        editorStage = new Stage();

        //ShapeEditor shapeEditor = new ShapeEditor(4, "shapes");
        Scene scene = new Scene(shapeEditor.create());
        editorStage.setScene(scene);

        editorStage.show();
    }

    // SCENES

    /**
     * Změní aktuální scénu na scénu s hlavním menu
     */
    public void setMainMenuScene() {
        mainMenuStage.setScene(new Scene(getMainMenuPane(), SCENE_WIDTH, SCENE_HEIGHT));
    }

    /**
     * Změní scénu na scénu s tabulkou pořadí
     */
    public void setLeaderboardScene() {
        //mainMenuStage.setScene(new Scene(getLeaderboardPane(), SCENE_WIDTH, SCENE_HEIGHT));
        mainMenuStage.setScene(new Scene(Leaderboard.getLeaderboard().create(SCORE_PATH, returnToMMBTN()), SCENE_WIDTH, SCENE_HEIGHT));
    }

    // PANES

    /**
     * Vytvoří pane s hlavní menu
     *
     * @return main menu pane
     */
    private Parent getMainMenuPane() {
        FlowPane root = new FlowPane();
        root.setAlignment(Pos.CENTER);

        VBox buttonsPane = new VBox();
        buttonsPane.setAlignment(Pos.CENTER);
        buttonsPane.setTranslateY(60);
        buttonsPane.setSpacing(20);

        root.getChildren().add(buttonsPane);

        buttonsPane.getChildren().addAll(getMainMenuBTNS());

        BackgroundImage img = new BackgroundImage(background,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(img));

        return root;
    }

    /**
     * Vytvoří pane s tetrisem
     *
     * @return pane s tetrisem
     */
    private Pane getTetrisPane() {
        return new Pane();
    }

    // Main menu pane buttons

    /**
     * Vrátí tlačítka hlavní menu s přiřazenými eventy a rozměry
     *
     * @return tlačítka hlavního menu
     */
    private Button[] getMainMenuBTNS() {
        Button gameStartBTN = new Button(GAME_START_TEXT);
        Button shapeEditorBTN = new Button(SHAPE_EDITOR_TEXT);
        Button leaderboardBTN = new Button(LEADERBOARD_TEXT);
        Button exitBTN = new Button(EXIT_GAME);

        gameStartBTN.setOnAction(actionEvent -> createGameStage());
        shapeEditorBTN.setOnAction(actionEvent -> createEditorStage());

        leaderboardBTN.setOnAction(actionEvent -> setLeaderboardScene());

        exitBTN.setOnAction(actionEvent -> getOnExit());

        gameStartBTN.setPrefWidth(175);
        gameStartBTN.setFont(Font.font(30));

        shapeEditorBTN.setPrefWidth(150);
        shapeEditorBTN.setFont(Font.font(20));

        leaderboardBTN.setPrefWidth(100);
        exitBTN.setPrefWidth(70);

        return new Button[]{gameStartBTN, shapeEditorBTN, leaderboardBTN, exitBTN};
    }

    // Buttons

    /**
     * Vytvoří stage se hrou
     */
    private void createGameStage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select play set");
        alert.setHeaderText("Choose a play set");
        TreeView<TreeViewItem> treeView = shapeEditor.getTreeView();
        alert.setGraphic(treeView);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    TreeItem<TreeViewItem> selected = treeView.getSelectionModel().getSelectedItem();
                    if (selected == null) {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Nothing is selected!");
                        error.showAndWait();
                    } else {
                        if (selected.getValue().getType() == FileType.SHAPE) {
                            Alert error2 = new Alert(Alert.AlertType.ERROR);
                            error2.setTitle("Error");
                            error2.setHeaderText("Selected item is a shape!");
                            error2.setContentText("Please select a folder with shapes!");
                            error2.showAndWait();
                        } else {
                            if (selected.getParent() == null) {
                                Alert error3 = new Alert(Alert.AlertType.ERROR);
                                error3.setTitle("Error");
                                error3.setHeaderText("Selected item is root file!");
                                error3.setContentText("Please select a folder with shapes!");
                                error3.showAndWait();
                            } else {
                                //System.out.println("hi");
                                ArrayList<Shape> selectedShapes = new ArrayList<>();
                                File file = new File(selected.getParent().getValue().getName() + "/"
                                        + selected.getValue().getName());
                                if (file.exists()) {
                                    File[] shapes = file.listFiles();
                                    if (shapes != null) {
                                        for (File shape : shapes) {
                                            if (shape.isFile() && shape.getName().endsWith(".txt")) {
                                                try {
                                                    BufferedReader br = new BufferedReader(new FileReader(shape));
                                                    try {
                                                        String name = br.readLine();

                                                        String blockSeq = br.readLine();
                                                        String[] split = blockSeq.split(" ");
                                                        int[] blocks = new int[split.length];
                                                        for (int i = 0; i < blocks.length; i++) {
                                                            blocks[i] = Integer.parseInt(split[i]);
                                                        }

                                                        String colorString = br.readLine();
                                                        Color color = Color.web(colorString);

                                                        Shape newShape = new Shape(blocks, name, color);

                                                        selectedShapes.add(newShape);
                                                    }
                                                    catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    br.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }

                                if (selectedShapes.size() != 0) {
                                    setTetrisStage(selectedShapes);
                                }
                                else {
                                    Alert noShapes = new Alert(Alert.AlertType.ERROR);
                                    noShapes.setTitle("Error");
                                    noShapes.setHeaderText("No shapes found in selected folder");
                                    noShapes.showAndWait();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Vytvoří stage s editorem tvarů
     */
    private void createEditorStage() {
        setShapeEditorStage();
    }

    /**
     * event pro tlačítko s exitem
     */
    private void getOnExit() {
        System.exit(0);
    }

    /**
     * tlačítko pro návrat do hl menu
     *
     * @return hlavní menu tlačítko
     */
    private Button returnToMMBTN() {
        Button button = new Button("");
        button.setOnAction(actionEvent -> setMainMenuScene());
        return button;
    }

    // INPUT HANDLER

    /**
     * Input pro tetris stage
     *
     * @param scene scéna s tetrisem
     * @param tetris {@link TetrisManager}
     */
    private void createInputHandler(Scene scene, TetrisManager tetris) {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case LEFT:
                    //System.out.println("LEFT KEY PRESSED");
                    tetris.move(MoveDirection.LEFT,0);
                    break;
                case RIGHT:
                    //System.out.println("RIGHT KEY PRESSED");
                    tetris.move(MoveDirection.RIGHT,0);
                    break;
                case DOWN:
                    //System.out.println("DOWN KEY PRESSED");
                    tetris.move(MoveDirection.DOWN,1);
                    break;
                case R:
                    //System.out.println("CURRENT SHAPE ROTATED");
                    tetris.rotateActualShape();
                    break;
            }
        });
    }

    // DEBUG

    private GridPane getEditorBtns() {
        GridPane btns = new GridPane();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                Button button = new Button("[" + x + ";" + y + "]");
                btns.add(button, x, y);
            }
        }

        return btns;
    }

    private Pane testPane(String text) {
        Label label = new Label(text);
        Button button = new Button("Exit to main menu");

        button.setOnAction(actionEvent -> setMainMenuScene());

        FlowPane pane = new FlowPane();
        pane.getChildren().addAll(label, button);

        return pane;
    }

    private Pane testMainMenuPane() {
        Label label = new Label("Main Menu");
        Button options = new Button("Options");
        Button leaderboard = new Button("leaderboard.Leaderboard");

        leaderboard.setOnAction(actionEvent -> setLeaderboardScene());

        FlowPane pane = new FlowPane();
        pane.getChildren().addAll(label, options, leaderboard);

        return pane;
    }

    private ObservableList<LeaderboardData> initData() {
        ObservableList<LeaderboardData> list = FXCollections.observableArrayList();

        list.add(new LeaderboardData("TEST", 10));
        list.add(new LeaderboardData("XXX", 20));

        return list;
    }
}
