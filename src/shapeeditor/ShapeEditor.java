package shapeeditor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ShapeEditor {

    private static final String NAME_LABEL_TEXT = "name";
    private static final String NAME_TF_TEXT = "type name";
    private static final String COLOR_LABEL_TEXT = "color";
    private static final String SUBMIT_BTN_TEXT = "SUBMIT";

    private final int[] selectedButtons;
    private final Button[] gridButtons;

    private ColorPicker colorPicker;
    private Color defaultColor = Color.GHOSTWHITE;

    public ShapeEditor(int x) {
        selectedButtons = new int[x*x];
        gridButtons = getGridBtns();

        colorPicker = new ColorPicker();
    }

    public Parent create() {
        return createParent();
    }

    private Parent createParent() {
        BorderPane root = new BorderPane();

        root.setCenter(getListView());

        VBox form = new VBox();
        form.getChildren().addAll(getEditorBtns(gridButtons), getForm());

        root.setRight(form);

        return root;
    }

    // List

    private BorderPane getListView() {
        BorderPane root = new BorderPane();

        ListView<Object> listView = new ListView<>();

        return root;
    }

    // Form

    private GridPane getForm() {
        GridPane root = new GridPane();

        Label nameLabel = new Label(NAME_LABEL_TEXT);
        TextField nameFW = new TextField(NAME_TF_TEXT);

        Label colorLabel = new Label(COLOR_LABEL_TEXT);
        colorPicker.setOnAction(actionEvent -> {
            System.out.println("Hoj");
            System.out.println(colorPicker.getValue());
            changeGridBtnsColor();
        });
        changeGridBtnsColor();

        root.add(nameLabel, 0, 0);
        root.add(nameFW, 0, 1);

        root.add(colorLabel, 1, 0);
        root.add(colorPicker, 1,1);

        Button submitBtn = new Button(SUBMIT_BTN_TEXT);
        submitBtn.setOnAction(action -> submit(nameFW.getText(), selectedButtons, colorPicker.getValue()));

        root.add(submitBtn, 2,1);

        return root;
    }

    private void submit(String name, int[] blocks, Color color) {
        System.out.println(name + ", " + color.toString());
        printRectArray(blocks);
    }

    private void changeGridBtnsColor() {
        for (int i = 0; i < gridButtons.length; i++) {
            if (selectedButtons[i] != 0) {
                Background background = new Background(new BackgroundFill(colorPicker.getValue(), null, null), null, null, null);
                gridButtons[i].setBackground(background);
            }
            else {
                Background background = new Background(new BackgroundFill(defaultColor, null, null), null, null, null);
                gridButtons[i].setBackground(background);
            }
        }
    }
    // Buttons

    private GridPane getEditorBtns(Button[] buttons) {
        GridPane btns = new GridPane();

        int width = (int) (Math.sqrt(buttons.length));
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                Button button = buttons[y * width + x];
                btns.add(button, x, y);
            }
        }

        return btns;
    }

    private Button[] getGridBtns() {
        final Button[] btns = new Button[selectedButtons.length];
        for (int i = 0; i < btns.length; i++) {
            Button btn = new Button();
            btn.setOnAction(actionEvent -> gridBtnsAction(btn));
            btn.setId("" + i);
            btns[i] = btn;
        }
        return btns;
    }

    private void gridBtnsAction(Button button) {
        int id = Integer.parseInt(button.getId());
        selectedButtons[id] = selectedButtons[id] == 0 ? 1 : 0;
        changeGridBtnsColor();
    }

    // TEST methods

    private void printRectArray(int[] array) {
        StringBuilder s = new StringBuilder();
        int width = (int) Math.sqrt(array.length);
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                s.append("").append(array[y * width + x]);
            }
            s.append("\n");
        }
        System.out.println(s.toString());
    }

}


