package tetris.shapeeditor;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import tetris.Shape;

import javax.xml.namespace.QName;
import java.awt.event.TextEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Editor "tvarů" tetrisu. Vytvoří novou složku a do složky je možné vkládat vytvořené tvary,
 * se kterými jde potom hrát tetris.
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
public class ShapeEditor {

    private static final String NAME_LABEL_TEXT = "name";
    private static final String NAME_TF_TEXT = "type name";
    private static final String COLOR_LABEL_TEXT = "color";
    private static final String SUBMIT_BTN_TEXT = "SUBMIT";
    private static final String REMOVE_BTN_TEXT = "REMOVE";
    private static final String ADD_DIR_BTN_TEXT = "ADD";
    private static final String ADD_DIR_TEXT = "add new directory";

    private final int[] selectedButtons;
    private final Button[] gridButtons;

    private ColorPicker colorPicker;
    private Color defaultColor = Color.GRAY;

    TreeView<TreeViewItem> treeView;

    private final String rootPath;

    /**
     * Vytvoří nový edidor tvarů
     *
     * @param x strana čtverce tvarů
     * @param rootPath cesta ke složce s tvary
     */
    public ShapeEditor(int x, String rootPath) {
        selectedButtons = new int[x * x];
        gridButtons = getGridBtns();

        colorPicker = new ColorPicker();

        this.rootPath = rootPath;
        treeView = new TreeView<>();
    }

    /**
     * Vytvoří novou komponentu, reprezentující grafické rozhraní editoru
     *
     * @return editor
     */
    public Parent create() {
        return createParent();
    }

    /**
     * Vytvoří border pane, do kterého vloží {@link TreeView} (reprezentuje adresář s tvary)
     * a prvky pro vytváření nových tvarů.
     *
     * @return {@link BorderPane} s ovládacími prvky editoru
     */
    private Parent createParent() {
        BorderPane root = new BorderPane();

        root.setCenter(getListView());

        VBox form = new VBox();
        GridPane editor = getEditorBtns(gridButtons);
        editor.setAlignment(Pos.TOP_CENTER);
        form.getChildren().addAll(editor, getForm());

        root.setRight(form);

        return root;
    }

    // List

    /**
     * Vrátí {@link BorderPane} s {@link TreeView} reprezentující adresář tvarů
     * + tlačítko na smazání/přidání složky/tvaru.
     *
     * @return adresář tvarů
     */
    public BorderPane getListView() {
        BorderPane root = new BorderPane();

        /*
        treeView = new TreeView<>();

        treeView.setCellFactory(x -> new ShapeCell());
        treeView.setEditable(true);

        //treeView.setRoot(new TreeItem<>(new TreeViewItem("shapes")));
        //createDefaultData(treeView.getRoot());
        initData();
         */

        root.setCenter(getTreeView());

        Button removeBtn = new Button(REMOVE_BTN_TEXT);
        removeBtn.setOnAction(action -> removeItem());

        TextField dirText = new TextField(ADD_DIR_TEXT);

        Button addDir = new Button(ADD_DIR_BTN_TEXT);
        addDir.setOnAction(x -> addNewItem(dirText.getText()));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(removeBtn, dirText, addDir);
        root.setBottom(hBox);

        return root;
    }

    public TreeView<TreeViewItem> getTreeView() {
        treeView = new TreeView<>();

        treeView.setCellFactory(x -> new ShapeCell());
        treeView.setEditable(true);

        //treeView.setRoot(new TreeItem<>(new TreeViewItem("shapes")));
        //createDefaultData(treeView.getRoot());
        initData();

        return treeView;
    }
    // Form

    /**
     * Vrátí {@link GridPane} s prvky potřebnými pro vytváření nových tvarů
     *
     * @return {@link GridPane} s prvky pro vytváření nových tvarů
     */
    private GridPane getForm() {
        GridPane root = new GridPane();

        Label nameLabel = new Label(NAME_LABEL_TEXT);
        TextField nameFW = new TextField(NAME_TF_TEXT);

        Label colorLabel = new Label(COLOR_LABEL_TEXT);
        colorPicker.setOnAction(actionEvent -> {
            changeGridBtnsColor();
        });
        changeGridBtnsColor();

        root.add(nameLabel, 0, 0);
        root.add(nameFW, 0, 1);

        root.add(colorLabel, 1, 0);
        root.add(colorPicker, 1, 1);

        Button submitBtn = new Button(SUBMIT_BTN_TEXT);
        submitBtn.setOnAction(action -> submit(nameFW.getText(), selectedButtons, colorPicker.getValue()));

        root.add(submitBtn, 2, 1);

        return root;
    }

    /**
     * Přidá nově vytvořený tvar do {@link TreeView} a uloží ho.
     *
     * @param name jmnéno tvaru
     * @param blocks bloky tvaru
     * @param color barva tvaru
     */
    private void submit(String name, int[] blocks, Color color) {
        //printRectArray(blocks);
        addNewItem(name, blocks, color);
    }

    /**
     * Vyvolá změnu barvy tlačítek určených pro volbu bloků tvarů
     */
    private void changeGridBtnsColor() {
        for (int i = 0; i < gridButtons.length; i++) {
            if (selectedButtons[i] != 0) {
                Background background = new Background(new BackgroundFill(colorPicker.getValue(), null, null), null, null, null);
                gridButtons[i].setBackground(background);
            } else {
                Background background = new Background(new BackgroundFill(defaultColor, null, null), null, null, null);
                gridButtons[i].setBackground(background);
            }
        }
    }

    // Buttons

    /**
     * Vrátí {@link GridPane} s tlačítky pro editaci bloků tvaru
     *
     * @param buttons tlačítka
     *
     * @return {@link GridPane} s tlačítky pro editaci bloků tvaru
     */
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

    /**
     * Vytvoří tlačítka pro editaci bloků tvarů
     *
     * @return tlačítka pro editaci bloků tvarů
     */
    private Button[] getGridBtns() {
        final Button[] btns = new Button[selectedButtons.length];
        for (int i = 0; i < btns.length; i++) {
            Button btn = new Button();
            btn.setPrefSize(50, 50);
            btn.setOnAction(actionEvent -> gridBtnsAction(btn));
            btn.setId("" + i);
            btns[i] = btn;
        }
        return btns;
    }

    /**
     * Event při kliku tlačítka pro editaci bloku tvaru.
     * Nejdříve změní index, na kterém se nachází tlačítko v poli inegerů v selectedButtons na 1.
     * A poté zavolá changeGridBtnsColor()
     *
     * @param button tlačítko akce
     */
    private void gridBtnsAction(Button button) {
        int id = Integer.parseInt(button.getId());
        selectedButtons[id] = selectedButtons[id] == 0 ? 1 : 0;
        changeGridBtnsColor();
    }

    // ADD

    /**
     * Přidá nový tvar do {@link TreeView} a následně ho uloží metodou save(...)
     *
     * @param name jméno tvaru
     * @param blocks bloky tvaru
     * @param color barva tvaru
     */
    private void addNewItem(String name, int[] blocks, Color color) {
        TreeItem<TreeViewItem> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nothing is selected!");
            alert.showAndWait();
        } else {
            if (name.length() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No name or file type.");
                alert.setContentText("Please provide a name");
                alert.showAndWait();
            } else {
                TreeItem<TreeViewItem> parent = selected.getParent();
                if (selected.getValue().getType() == FileType.DIRECTORY && parent != null) {
                    Shape shape = new Shape(blocks, name, color);
                    selected.getChildren().add(new TreeItem<>(new TreeViewItem(shape)));
                    selected.setExpanded(true);
                    save(selected.getParent().getValue().getName() + "/" + selected.getValue().getName() + "/" + name,
                            FileType.SHAPE, shape);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cannot add shape to a shape or root directory");
                    alert.setContentText("Only directories(excluding root) are allowed to have shapes");

                    alert.showAndWait();
                }
            }
        }
    }

    /**
     * Přidá novou složku do {@link TreeView} a následně ji uloží metodou save(...)
     *
     * @param name jméno složky
     */
    private void addNewItem(String name) {
        TreeItem<TreeViewItem> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nothing is selected!");
            alert.showAndWait();
        } else {
            if (name.length() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No name or file type.");
                alert.setContentText("Please provide a name");
                alert.showAndWait();
            } else {
                TreeItem<TreeViewItem> parent = selected.getParent();
                if (parent == null) {
                    selected.getChildren().add(new TreeItem<>(new TreeViewItem(name)));
                    selected.setExpanded(true);
                    String path = selected.getValue().getName() + "/" + name;
                    save(path, FileType.DIRECTORY, null);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("New directory can be only added to root");
                    alert.setContentText("Only root ais allowed to have directories");

                    alert.showAndWait();
                }
            }
        }
    }

    /**
     * Odebere položku z {@link TreeView} a smaže jí pomocí metody remove(...)
     */
    private void removeItem() {
        TreeItem<TreeViewItem> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Nothing is selected!");
            alert.setContentText("Please select node.");
            alert.showAndWait();
        } else {
            TreeItem<TreeViewItem> parent = selected.getParent();
            if (parent == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Selected item is Root!");
                alert.setContentText("It is not possible to delete root directory");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Deleting");
                alert.setHeaderText("Do you want to delete selected " + selected.getValue().getType().toString().toLowerCase() + "?");
                alert.setContentText("Selected: " + selected.getValue().getName());
                alert.showAndWait()
                        .filter(response -> response == ButtonType.OK)
                        .ifPresent(response -> {
                            System.out.println("path: " + createPath());
                            remove(createPath(), selected.getValue().getType());
                            parent.getChildren().remove(selected);
                            treeView.getSelectionModel().clearSelection();
                        });
            }
        }
    }

    /**
     * Vytvoří cestu k souboru/tvaru v {@link TreeView}
     *
     * @return cesta k souboru/tvaru
     */
    private String createPath() {
        TreeItem<TreeViewItem> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return "";
        }

        List<String> names = new ArrayList<>();

        names.add(selected.getValue().getName());
        TreeItem<TreeViewItem> parent = selected.getParent();
        while (parent != null) {
            names.add(parent.getValue().getName());
            parent = parent.getParent();
        }

        Collections.reverse(names);
        StringBuilder path = new StringBuilder();
        names.forEach(x -> path.append("/" + x));

        return path.substring(1);
    }

    // initData

    /**
     * Načte data pro {@link TreeView}
     */
    private void initData() {
        File rootDir = new File(rootPath);
        if (rootDir.exists()) {
            treeView.setRoot(new TreeItem<>(new TreeViewItem(rootPath)));
            File[] listOfFiles = rootDir.listFiles();
            if (listOfFiles != null) {
                for (File dir : listOfFiles) {
                    if (dir.isDirectory()) {
                        TreeItem<TreeViewItem> directory = new TreeItem<>(new TreeViewItem(dir.getName()));
                        treeView.getRoot().getChildren().add(directory);
                        File[] shapes = dir.listFiles();
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
                                            TreeItem<TreeViewItem> shapeItem = new TreeItem<>(new TreeViewItem(newShape));

                                            directory.getChildren().add(shapeItem);
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
                }
            }
        } else {
            if (rootDir.mkdir()) {
                treeView.setRoot(new TreeItem<>(new TreeViewItem(rootPath)));
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot create " + rootPath + "file, cannot load shapes");
                alert.showAndWait();
            }
        }
    }

    // save shape/dir

    /**
     * Uloží tvar/složku na disk
     *
     * @param path cesta kam
     * @param type typ (složka/tvar)
     * @param shape pokud byl vybrán tvar, tak {@link Shape}
     */
    private void save(String path, FileType type, Shape shape) {
        if (type == FileType.SHAPE) {
            try (PrintWriter write = new PrintWriter(path + ".txt")) {
                write.println(shape.getName());

                StringBuilder blocks = new StringBuilder();
                for (int i = 0; i < shape.getBlocks().length; i++) {
                    blocks.append(shape.getBlocks()[i]);
                    if (i != shape.getBlocks().length - 1) {
                        blocks.append(" ");
                    }
                }
                write.println(blocks.toString());

                write.println(shape.getColor());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            File file = new File(path);
            file.mkdir();
        }
    }

    /**
     * Odstraní tvar/složku z disku
     *
     * @param path cesta k tvaru/složce
     * @param type typ (složka/soubor)
     */
    private void remove(String path, FileType type) {
        if (type == FileType.SHAPE)
            path += ".txt";
        File file = new File(path);
        if (file.exists()) {
            System.out.println(file.delete());
        }
    }

    // DEBUG

    private void printRectArray(int[] array) {
        StringBuilder s = new StringBuilder();
        int width = (int) Math.sqrt(array.length);
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < width; x++) {
                s.append("").append(array[y * width + x]);
            }
            s.append("\n");
        }
        System.out.println(s);
    }

    private void createDefaultData(TreeItem<TreeViewItem> root) {
        TreeItem<TreeViewItem> testItem1 = new TreeItem<>(new TreeViewItem(new Shape(new int[]{0}, "testShape1", Color.BLUE)));
        TreeItem<TreeViewItem> testItem2 = new TreeItem<>(new TreeViewItem(new Shape(new int[]{0}, "testShape2", Color.BLUE)));
        TreeItem<TreeViewItem> testItem3 = new TreeItem<>(new TreeViewItem(new Shape(new int[]{0}, "testShape3", Color.BLUE)));

        TreeItem<TreeViewItem> testDir1 = new TreeItem<>(new TreeViewItem("testDir1"));
        TreeItem<TreeViewItem> testDir2 = new TreeItem<>(new TreeViewItem("testDir2"));

        root.getChildren().addAll(testDir1, testDir2);

        testDir1.getChildren().add(testItem1);
        testDir2.getChildren().addAll(testItem2, testItem3);
    }

}


