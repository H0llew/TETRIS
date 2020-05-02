package tetris.shapeeditor;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;

/**
 * Vytvoří custom cell pro {@link TreeCell}, pro zobrazení {@link TreeViewItem}
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
public class ShapeCell extends TreeCell<TreeViewItem> {
    private TextField textTF;

    @Override
    public void startEdit() {
        super.startEdit();

        if (textTF == null) {
            createEditor();
        }

        setText(null);
        textTF.setText(createEditorContent());
        setGraphic(textTF);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(createContent());
        setGraphic(null);
    }

    @Override
    public void updateItem(TreeViewItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textTF != null) {
                    textTF.setText(createEditorContent());
                    setText(null);
                    setGraphic(textTF);
                }
            } else {
                setText(createContent());
                setGraphic(null);
            }
        }
    }

    private void createEditor() {
        textTF = new TextField();

        textTF.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (textTF.getText().length() == 0) {
                    cancelEdit();
                } else {
                    TreeViewItem item = getItem();
                    item.setName(textTF.getText());
                    commitEdit(item);
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    private String createEditorContent() {
        return getItem().getName();
    }

    private String createContent() {
        return getItem().getType().getSymbol() + " " + getItem().getName();
    }
}
