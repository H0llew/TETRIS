import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public class LeaderboardData implements Serializable {

    public StringProperty playerName;
    public IntegerProperty score;

    public LeaderboardData(String playerName, int score) {
        this.playerName = new SimpleStringProperty(playerName);
        this.score = new SimpleIntegerProperty(score);
    }

    public StringProperty playerNameProperty() {
        return playerName;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}