package tetris.shapeeditor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import tetris.Shape;

/**
 * Reprezentuje datový model tvaru, položky v {@link javafx.scene.control.TreeView}
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
public class TreeViewItem implements Comparable<TreeViewItem>{
    private StringProperty name;
    private ObjectProperty<FileType> type;
    private ObjectProperty<Shape> shape;

    /**
     * Vytvoří novou položku pro {@link javafx.scene.control.TreeView};
     *
     * @param shape reprezentovaný tvar
     */
    public TreeViewItem(Shape shape) {
        this.name = new SimpleStringProperty(shape.getName());
        this.type = new SimpleObjectProperty<>(FileType.SHAPE);
        this.shape = new SimpleObjectProperty<>(shape);
    }

    /**
     * Vytvoří novou složku pro {@link javafx.scene.control.TreeView};
     *
     * @param dirName jméno složky
     */
    public TreeViewItem(String dirName) {
        this.name = new SimpleStringProperty(dirName);
        this.type = new SimpleObjectProperty<>(FileType.DIRECTORY);
        this.shape = new SimpleObjectProperty<>(null);
    }

    /**
     * Jméno souboru/tvaru
     *
     * @return jméno souboru/tvaru
     */
    public String getName() {
        return name.get();
    }

    /**
     * Nastaví jméno souboru/složky
     *
     * @param name jméno souboru/složky
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Vrátí typ položky (soubor/tvar)
     *
     * @return typ položky
     */
    public FileType getType() {
        return type.get();
    }

    @Override
    public int compareTo(TreeViewItem item) {
        return this.getName().compareTo(item.getName());
    }
}
