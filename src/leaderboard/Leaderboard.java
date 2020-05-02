package leaderboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Slouží k vytvoření tabulky se skórem pomocí metody třídy create()
 *
 * @author Martin Jakubašek
 * @version 1.00.000
 */
public class Leaderboard {

    // odkaz na jedinou instanci třídy
    private static final Leaderboard INSTANCE = new Leaderboard();

    // cesta k složce s výsledky
    private String path = "";

    // pojmenování textů u tlačítek + labelu
    private final String titleText = "LEADERBOARD";

    private final String orderText = "POS";
    private final String nameText = "PLAYER";
    private final String scoreText = "SCORE";

    private final String removeText = "REMOVE";
    private final String returnText = "RETURN";

    // privatní konstruktor zabranuje tvorbě nové instance třídy
    private Leaderboard() {
    }

    /**
     * Vrátí {@link BorderPane} tabulky pořadé
     *
     * @param returnButton tlačítko, které přepne scénu na předchozí/požadovanou
     * @return {@link BorderPane} tabulky pořadí
     */
    public Parent create(String path, Button returnButton) {
        this.path = path;
        return createParent(returnButton);
    }

    /**
     * Vytvoří {@link BorderPane} se všemi potřebnými prvky
     *
     * @param returnButton tlačítko, které přepne scénu na předchozí/požadovanou
     *
     * @return {@link BorderPane} tabulky pořadí
     */
    private Parent createParent(Button returnButton) {
        BorderPane root = new BorderPane();

        TableView<LeaderboardData> tv = createTableView();
        root.setCenter(tv);

        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("arials", FontWeight.BOLD, 35));
        root.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.TOP_CENTER);

        Button removeBTN = new Button(removeText);
        removeBTN.setOnAction(action -> removeFromTable(tv));
        returnButton.setText(returnText);

        AnchorPane buttons = new AnchorPane();
        AnchorPane.setLeftAnchor(returnButton, 10d);
        AnchorPane.setLeftAnchor(removeBTN, 270d);
        buttons.getChildren().addAll(returnButton, removeBTN);
        root.setBottom(buttons);

        return root;
    }

    /**
     * Vytvoří tabulku se skorem hráču/hráče
     *
     * @return tabulka se skorem hráču/hráče
     */
    private TableView<LeaderboardData> createTableView() {
        TableView<LeaderboardData> tv = new TableView<>();
        tv.setItems(initData());

        TableColumn<LeaderboardData, Integer> orderColumn = new TableColumn<>(orderText);
        TableColumn<LeaderboardData, String> nameColumn = new TableColumn<>(nameText);
        TableColumn<LeaderboardData, Integer> scoreColumn = new TableColumn<>(scoreText);

        orderColumn.setEditable(false);
        orderColumn.setSortable(false);
        orderColumn.setPrefWidth(40d);

        nameColumn.setEditable(false);
        nameColumn.setSortable(false);
        nameColumn.setPrefWidth(150d);

        scoreColumn.setEditable(false);
        scoreColumn.setSortable(true);
        scoreColumn.setPrefWidth(400d);

        orderColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setStyle("-fx-alignment: CENTER;");
        scoreColumn.setStyle("-fx-alignment: CENTER;");

        tv.getColumns().addAll(orderColumn, nameColumn, scoreColumn);

        orderColumn.setCellFactory(column -> new OrderCell());
        nameColumn.setCellValueFactory(new PropertyValueFactory<LeaderboardData, String>("playerName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<LeaderboardData, Integer>("score"));

        tv.sort();

        return tv;
    }

    /**
     * Převede data získaná z getFromFile() na ObservableList, který následně vrátí
     *
     * @return observable list dat
     */
    private ObservableList<LeaderboardData> initData() {
        ObservableList<LeaderboardData> list = FXCollections.observableArrayList();

        ArrayList<LeaderboardData> data = getFromFile();
        list.addAll(data);

        return list;
    }

    /**
     * Postupně projde včechny *.txt soubory v path ,a převede je na leaderboard.LeaderboardData, které následně vrátí ve formě listu
     *
     * @return list leaderboard.LeaderboardData ze souboru path
     */
    private ArrayList<LeaderboardData> getFromFile() {
        ArrayList<LeaderboardData> list = new ArrayList<>();

        File scoresFile = new File(path);
        if (scoresFile.exists()) {
            File[] listOfFiles = scoresFile.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            try {
                                String playerName = br.readLine();
                                int score = Integer.parseInt(br.readLine());
                                LeaderboardData data = new LeaderboardData(playerName, score);
                                list.add(data);
                                br.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            try {
                scoresFile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * Vymaže soubor, kterého data se schodují s daty parametru
     *
     * @param data {@link LeaderboardData}
     */
    private void deleteFile(LeaderboardData data) {
        File scoresFile = new File(path);
        if (scoresFile.exists()) {
            File[] listOfFiles = scoresFile.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            try {
                                String playerName = br.readLine();
                                int score = Integer.parseInt(br.readLine());
                                if (score == data.score.getValue() && playerName.equals(data.playerName.getValue())) {
                                    br.close();
                                    System.out.println(file.delete());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            try {
                scoresFile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Odebere prvkek z {@link TableView} a následně vymaže soubor, který se schoduje se smazaným prvkem
     *
     * @param tableView {@link TableView}
     */
    private void removeFromTable(TableView<LeaderboardData> tableView) {
        ObservableList<LeaderboardData> selection = FXCollections.observableArrayList(tableView.getSelectionModel().getSelectedItems());

        if (selection.size() != 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Deleting selection");
            alert.setHeaderText("Do you want to delete selected score?");

            alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> {
                        tableView.getItems().removeAll(selection);
                        tableView.getSelectionModel().clearSelection();
                        deleteFile(selection.get(0));
                    });
        }
    }

    // GET

    /**
     * Vrátí jedinou instanci třídy
     *
     * @return {@link Leaderboard}
     */
    public static Leaderboard getLeaderboard() {
        return INSTANCE;
    }
}
