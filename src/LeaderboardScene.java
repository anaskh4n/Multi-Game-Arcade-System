
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LeaderboardScene {

    private final Scene scene;
    private final Stage stage;

    public LeaderboardScene(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setStyle(ArcadeStyle.BG);
        root.setTop(buildHeader("🏆  LEADERBOARD", "#f5a623"));
        root.setCenter(buildCenter());

        scene = new Scene(root, 880, 650);
    }

    private VBox buildCenter() {
        VBox box = new VBox(18);
        box.setStyle("-fx-padding:28;");

        // ── Filter ──
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("FILTER BY GAME:");
        lbl.setStyle(ArcadeStyle.LABEL_DIM);
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("Overall","TicTacToe","MemoryMatch","Maze","Mastermind");
        combo.setValue("Overall");
        combo.setStyle("-fx-background-color:#16213e;-fx-text-fill:#eaeaea;-fx-border-color:#f5a623;-fx-border-radius:6;");
        row.getChildren().addAll(lbl, combo);

        // ── Table ──
        TableView<LBEntry> table = new TableView<>();
        table.setStyle("-fx-background-color:#16213e;-fx-table-cell-border-color:#0d0d1a;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(440);

        TableColumn<LBEntry, Integer> rankCol = new TableColumn<>("RANK");
        rankCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(table.getItems().indexOf(d.getValue())+1));
        rankCol.setMaxWidth(70);

        TableColumn<LBEntry, String> userCol = new TableColumn<>("USERNAME");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<LBEntry, String> gameCol = new TableColumn<>("GAME");
        gameCol.setCellValueFactory(new PropertyValueFactory<>("game"));

        TableColumn<LBEntry, Integer> scoreCol = new TableColumn<>("SCORE");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setMaxWidth(100);

        table.getColumns().addAll(rankCol, userCol, gameCol, scoreCol);

        // ── Data (replace with LeaderboardManager.getTopScores()) ──
        LeaderboardManager lm = new LeaderboardManager();
        ObservableList<LBEntry> all = FXCollections.observableArrayList();
        for (String[] entry : lm.getTopScores("Overall", 50)) {
        all.add(new LBEntry(entry[0], entry[1], Integer.parseInt(entry[2])));
        }
        table.setItems(all);

        combo.setOnAction(e -> {
        String sel = combo.getValue();
        ObservableList<LBEntry> filtered = FXCollections.observableArrayList();
        for (String[] entry : lm.getTopScores(sel.equals("Overall") ? "Overall" : sel, 50)) {
            filtered.add(new LBEntry(entry[0], entry[1], Integer.parseInt(entry[2])));
        }
        table.setItems(filtered);
    });

        box.getChildren().addAll(row, table);
        return box;
    }

    HBox buildHeader(String titleText, String accentColor) {
        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER.replace("#e94560", accentColor));
        h.setAlignment(Pos.CENTER_LEFT);

        Button back = new Button("← BACK");
        back.setStyle(ArcadeStyle.BTN_OUTLINE.replace("#e94560", accentColor) + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label t = new Label(titleText);
        t.setStyle("-fx-text-fill:" + accentColor + ";-fx-font-size:20;-fx-font-weight:bold;");

        h.getChildren().addAll(back, sp, t);
        return h;
    }

    public static class LBEntry {
        private final StringProperty username, game;
        private final IntegerProperty score;
        public LBEntry(String u, String g, int s) {
            username = new SimpleStringProperty(u);
            game     = new SimpleStringProperty(g);
            score    = new SimpleIntegerProperty(s);
        }
        public String  getUsername()     { return username.get(); }
        public StringProperty usernameProperty() { return username; }
        public String  getGame()         { return game.get(); }
        public StringProperty gameProperty()     { return game; }
        public int     getScore()        { return score.get(); }
        public IntegerProperty scoreProperty()   { return score; }
    }

    public Scene getScene() { return scene; }
}
