package tetris.leaderboard;

import javafx.scene.control.TableCell;

/**
 * Reprezentuje buňku v {@link javafx.scene.control.TableView}, pro zobrazení pořadí prvků
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
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