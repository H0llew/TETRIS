import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainMenu {

    private static final MainMenu INSTANCE = new MainMenu();

    private static final String gameStartText = "PLAY";
    private static final String shapeEditorText = "Editor";
    private static final String leaderboardText = "leaderboard.Leaderboard";
    private static final String optionsText = "Options";
    private static final String exitText = "Exit Game";

    private Button gameStartBTN;
    private Button shapeEditorBTN;
    private Button leaderboardBTN;
    private Button optionsBTN;
    private Button exitBTN;

    private Image background = new Image("/tetrisMenu.png");

    private MainMenu() {
        initBTNS();
    }

    public Scene createScene() {
        return new Scene(createRoot(), 600, 450);
    }

    private Parent createRoot() {
        StackPane root = new StackPane();

        BackgroundImage img = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        VBox btns = getButtons();
        btns.setAlignment(Pos.CENTER);
        root.getChildren().add(btns);

        root.setBackground(new Background(img));

        root.setAlignment(Pos.CENTER);

        return root;
    }

    private Parent getLeaderboard() {
        Pane root = new Pane();
        Label text = new Label("leaderboard.Leaderboard");
        Button exitBTN = new Button("Return to main menu");

        root.getChildren().addAll(text, exitBTN);

        exitBTN.setOnAction(actionEvent -> setMMScene());

        return root;
    }

    private Parent getOptions() {
        Pane root = new Pane();
        Label text = new Label("Options");
        Button exitBTN = new Button("Return to main menu");

        root.getChildren().addAll(text, exitBTN);

        exitBTN.setOnAction(actionEvent -> setMMScene());

        return root;
    }

    private void setMMScene() {

    }

    private void setLeaderboardScene() {

    }

    private void setOptionsScene() {

    }

    private VBox getButtons() {
        VBox vBox = new VBox();

        vBox.getChildren().addAll(gameStartBTN, shapeEditorBTN, leaderboardBTN, optionsBTN, exitBTN);

        return vBox;
    }

    private void initBTNS() {
        gameStartBTN = new Button(gameStartText);
        shapeEditorBTN = new Button(shapeEditorText);
        leaderboardBTN = new Button(leaderboardText);
        optionsBTN = new Button(optionsText);
        exitBTN = new Button(exitText);
    }

    public static MainMenu getMainMenu() {
        return INSTANCE;
    }
}
