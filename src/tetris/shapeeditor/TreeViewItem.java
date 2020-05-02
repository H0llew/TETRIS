package tetris.shapeeditor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import tetris.Shape;

public class TreeViewItem implements Comparable<TreeViewItem>{
    private StringProperty name;
    private ObjectProperty<FileType> type;
    private ObjectProperty<Shape> shape;

    public TreeViewItem(Shape shape) {
        this.name = new SimpleStringProperty(shape.getName());
        this.type = new SimpleObjectProperty<>(FileType.SHAPE);
        this.shape = new SimpleObjectProperty<>(shape);
    }

    public TreeViewItem(String dirName) {
        this.name = new SimpleStringProperty(dirName);
        this.type = new SimpleObjectProperty<>(FileType.DIRECTORY);
        this.shape = new SimpleObjectProperty<>(null);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public FileType getType() {
        return type.get();
    }

    @Override
    public int compareTo(TreeViewItem item) {
        return this.getName().compareTo(item.getName());
    }
}
