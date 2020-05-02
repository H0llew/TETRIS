package tetris;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Tetris extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        //setMainMenu(stage);
        //setTetrisGame(stage);
        //MM mainMenu = new MM(stage);
        //mainMenu.setMainMenuScene();

        //stage.show();

        Pane pane = new Pane();

        TetrisManager tetrisManager = new TetrisManager(pane,25);

        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();

        createInputHandler(scene, tetrisManager);

        new AnimationTimer() {
            @Override
            public void handle(long l) {
                tetrisManager.nextStep();
            }
        }.start();
    }

    private void setMainMenu(Stage stage) {
        stage.setScene(MainMenu.getMainMenu().createScene());
        stage.setTitle("TETRIS");
        stage.setResizable(false);
        stage.show();
    }

    public void setTetrisGame(Stage stage) {
        Stage secondStage = new Stage();
        secondStage.show();
    }

    private void createInputHandler(Scene scene, TetrisManager tetris) {
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case LEFT:
                    //System.out.println("LEFT KEY PRESSED");
                    tetris.move(MoveDirection.LEFT);
                    break;
                case RIGHT:
                    //System.out.println("RIGHT KEY PRESSED");
                    tetris.move(MoveDirection.RIGHT);
                    break;
                case DOWN:
                    //System.out.println("DOWN KEY PRESSED");
                    tetris.move(MoveDirection.DOWN);
                    break;
                case R:
                    //System.out.println("CURRENT SHAPE ROTATED");
                    tetris.rotateActualShape();
                    break;
            }
        });
    }
}
