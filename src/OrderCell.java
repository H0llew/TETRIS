import javafx.scene.control.TableCell;

public class OrderCell extends TableCell<LeaderboardData, Integer> {

    public void updateItem(Integer value, boolean empty) {
        super.updateItem(value, empty);

        if (empty) {
            setText(null);
        } else {
            setText("" + (getIndex() + 1));
        }
    }
}