package tetris.leaderboard;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Představuje datový model pro položku v {@link javax.swing.text.TableView}
 * reprezentující dosažené skóre hráče
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
public class LeaderboardData implements Serializable {

    public StringProperty playerName;
    public IntegerProperty score;

    /**
     * Vytvoří novou položku pro {@link javax.swing.text.TableView}
     *
     * @param playerName jméno hráče
     * @param score dosažené skóre
     */
    public LeaderboardData(String playerName, int score) {
        this.playerName = new SimpleStringProperty(playerName);
        this.score = new SimpleIntegerProperty(score);
    }

    /**
     * Vrátí {@link StringProperty} se jménem hráče
     *
     * @return {@link StringProperty} se jménem hráče
     */
    public StringProperty playerNameProperty() {
        return playerName;
    }

    /**
     * Vrátí {@link IntegerProperty} se skórem hráče
     *
     * @return {@link IntegerProperty} se skórem hráče
     */
    public IntegerProperty scoreProperty() {
        return score;
    }
}