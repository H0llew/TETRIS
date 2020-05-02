import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MM {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 450;

    private static final String gameStartText = "PLAY";
    private static final String shapeEditorText = "Editor";
    private static final String leaderboardText = "leaderboard.Leaderboard";
    private static final String optionsText = "Options";
    private static final String exitText = "Exit Game";

    private Stage stage;

    public MM(Stage stage) {
        this.stage = stage;
    }

    // Main menu scenes

    public void setMainMenuScene() {
        stage.setScene(new Scene(getMainMenuPane(), WIDTH, HEIGHT));
    }

    public void setLeaderboardScene() {
        stage.setScene(new Scene(getLeaderboardPane(), WIDTH, HEIGHT));
    }

    public void setOptionsScene() {
        stage.setScene(new Scene(getOptionsPane(), WIDTH, HEIGHT));
    }

    // Main menu panels

    private Parent getMainMenuPane() {
        AnchorPane root = new AnchorPane();

        VBox buttonsPane = new VBox();
        root.getChildren().add(buttonsPane);

        buttonsPane.getChildren().addAll(getMainMenuBTNS());

        return root;
    }

    private Parent getShapeEditorPane() {
        return null;
    }

    private Parent getTetrisPane() {
        return null;
    }

    private Parent getLeaderboardPane() {
        return testPane("leaderboard.Leaderboard");
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

        return new Button[] {gameStartBTN, shapeEditorBTN, leaderboardBTN, optionsBTN, exitBTN};
    }

    // Buttons

    private void createGameStage() {

    }

    private void createEditorStage() {

    }

    private void getOnExit() {
        System.exit(0);
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
}
