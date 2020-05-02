package tetris;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import tetris.leaderboard.Leaderboard;
import tetris.leaderboard.LeaderboardData;
import tetris.shapeeditor.FileType;
import tetris.shapeeditor.ShapeEditor;
import tetris.shapeeditor.TreeViewItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class TetrisGame extends Application {

    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 450;

    private static final String gameStartText = "PLAY";
    private static final String shapeEditorText = "Editor";
    private static final String leaderboardText = "Leaderboard";
    private static final String optionsText = "Options";
    private static final String exitText = "Exit Game";

    private static final String SHAPE_PATH = "shapes";
    private static final String SCORE_PATH = "scores";

    private Image background = new Image("/tetrisMenu.png");
    private Image hint = new Image("/Hint.png");

    private Stage mainMenuStage;
    private Stage tetrisStage;
    private Stage editorStage;

    private ShapeEditor shapeEditor;

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
     * Creates a new stage with tetris game.
     */
    public void setTetrisStage(ArrayList<Shape> playSet) {
        tetrisStage = new Stage();

        BorderPane root = new BorderPane();

        Pane gamePane = getTetrisPane();
        TetrisManager tetris = new TetrisManager(gamePane, 20, playSet, SCORE_PATH);

        root.setCenter(gamePane);

        Label label1 = new Label("");
        label1.textProperty().bind(tetris.score.asString());
        Label label2 = new Label("");
        ImageView imageView = new ImageView(hint);

        VBox box = new VBox();
        box.getChildren().addAll(label1, label2, imageView);

        root.setRight(box);

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
     * Creates a new stage with shape editor
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
     * Changes the main menu stage scene to main menu scene
     */
    public void setMainMenuScene() {
        mainMenuStage.setScene(new Scene(getMainMenuPane(), SCENE_WIDTH, SCENE_HEIGHT));
    }

    /**
     * Changes the main menu stage scene to leaderboard scene
     */
    public void setLeaderboardScene() {
        //mainMenuStage.setScene(new Scene(getLeaderboardPane(), SCENE_WIDTH, SCENE_HEIGHT));
        mainMenuStage.setScene(new Scene(Leaderboard.getLeaderboard().create(SCORE_PATH, returnToMMBTN()), SCENE_WIDTH, SCENE_HEIGHT));
    }

    /**
     * Changes the main menu stage scene to options scene
     */
    public void setOptionsScene() {
        mainMenuStage.setScene(new Scene(getOptionsPane(), SCENE_WIDTH, SCENE_HEIGHT));
    }

    // PANES

    /**
     * Creates a main menu pane
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

    private Pane getTetrisPane() {
        return new Pane();
    }

    private Parent getShapeEditorPane() {
        HBox root = new HBox();

        BorderPane treePane = new BorderPane();
        TreeView<Object> treeView = new TreeView<>();
        treePane.setCenter(treeView);
        Button remove = new Button("REMOVE");
        treePane.setBottom(remove);

        VBox vBox = new VBox();
        vBox.getChildren().add(getEditorBtns());

        Label name = new Label("name");
        TextField nameField = new TextField("set name");

        Label color = new Label("color");
        TextField colorPicker = new TextField("pick color");

        Button add = new Button("ADD");

        HBox hBox = new HBox();
        hBox.getChildren().addAll(name, nameField, color, colorPicker, add);

        vBox.getChildren().add(hBox);

        root.getChildren().addAll(treePane, vBox);

        return root;
    }

    private Parent getLeaderboardPane() {
        BorderPane root = new BorderPane();

        TableView<LeaderboardData> tableView = new TableView<>();
        TableColumn<LeaderboardData, String> nameColumn = new TableColumn<>("Player Name");
        TableColumn<LeaderboardData, Integer> scoreColumn = new TableColumn<>("Score");

        tableView.getColumns().addAll(nameColumn, scoreColumn);

        tableView.setItems(initData());

        nameColumn.setCellValueFactory(new PropertyValueFactory<LeaderboardData, String>("playerName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<LeaderboardData, Integer>("score"));

        root.setCenter(tableView);

        Button button = new Button("Exit to main menu");
        button.setOnAction(actionEvent -> setMainMenuScene());

        root.setBottom(button);

        //leaderboard.Leaderboard leaderboard = leaderboard.Leaderboard.getLeaderboard();
        //leaderboard.create();

        return root;
    }

    private Parent getOptionsPane() {
        return testPane("Options");
    }

    // Main menu pane buttons

    private Button[] getMainMenuBTNS() {
        Button gameStartBTN = new Button(gameStartText);
        Button shapeEditorBTN = new Button(shapeEditorText);
        Button leaderboardBTN = new Button(leaderboardText);
        Button optionsBTN = new Button(optionsText);
        Button exitBTN = new Button(exitText);

        gameStartBTN.setOnAction(actionEvent -> createGameStage());
        shapeEditorBTN.setOnAction(actionEvent -> createEditorStage());

        leaderboardBTN.setOnAction(actionEvent -> setLeaderboardScene());
        optionsBTN.setOnAction(actionEvent -> setOptionsScene());

        exitBTN.setOnAction(actionEvent -> getOnExit());

        gameStartBTN.setPrefWidth(175);
        gameStartBTN.setFont(Font.font(30));

        shapeEditorBTN.setPrefWidth(150);
        shapeEditorBTN.setFont(Font.font(20));

        leaderboardBTN.setPrefWidth(100);
        optionsBTN.setPrefWidth(100);
        exitBTN.setPrefWidth(70);

        return new Button[]{gameStartBTN, shapeEditorBTN, leaderboardBTN, optionsBTN, exitBTN};
    }

    // Buttons

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

    private void createEditorStage() {
        setShapeEditorStage();
    }

    private void getOnExit() {
        System.exit(0);
    }

    private Button returnToMMBTN() {
        Button button = new Button("");
        button.setOnAction(actionEvent -> setMainMenuScene());
        return button;
    }

    // INPUT HANDLER

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
                    tetris.move(MoveDirection.DOWN,10);
                    break;
                case R:
                    //System.out.println("CURRENT SHAPE ROTATED");
                    tetris.rotateActualShape();
                    break;
            }
        });
    }

    // EDITOR

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

    // DEBUG METHODS

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

        options.setOnAction(actionEvent -> setOptionsScene());
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
