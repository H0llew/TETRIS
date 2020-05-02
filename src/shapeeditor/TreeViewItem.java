package shapeeditor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class TreeViewItem implements Comparable<TreeViewItem>{
    private StringProperty name;
    private ObjectProperty<Type> type;
    //private ObjectProperty<Shape> shape;

    @Override
    public int compareTo(TreeViewItem treeViewItem) {
        //return this.getName().compareTo(o.getName());
        return 0;
    }
}
