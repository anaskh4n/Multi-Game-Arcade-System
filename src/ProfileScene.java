
// ================================================================
// FILE: ProfileScene.java  —  Q2 (Profile Button destination)
// ================================================================
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ProfileScene {

    private final Scene scene;
    private final Stage stage;

    public ProfileScene(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setStyle(ArcadeStyle.BG);
        root.setTop(buildHeader());
        root.setCenter(buildCenter());

        scene = new Scene(root, 880, 650);
    }

    private HBox buildHeader() {
        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER);
        h.setAlignment(Pos.CENTER_LEFT);

        Button back = new Button("← BACK");
        back.setStyle(ArcadeStyle.BTN_OUTLINE + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label t = new Label("👤  MY PROFILE");
        t.setStyle("-fx-text-fill:#e94560;-fx-font-size:20;-fx-font-weight:bold;");

        h.getChildren().addAll(back, sp, t);
        return h;
    }

    private HBox buildCenter() {
        HBox outer = new HBox(24);
        outer.setStyle("-fx-padding:28;");
        outer.setAlignment(Pos.TOP_CENTER);
        outer.getChildren().addAll(buildLeftPanel(), buildRightPanel());
        return outer;
    }

    

    // ── Left: avatar + change password ──
    private VBox buildLeftPanel() {
        VBox box = new VBox(16);
        box.setStyle(ArcadeStyle.PANEL + "-fx-padding:28;-fx-min-width:260;-fx-max-width:260;");
        box.setAlignment(Pos.TOP_CENTER);

        Circle avatar = new Circle(42);
        avatar.setFill(Color.web("#0f3460"));
        avatar.setStroke(Color.web("#e94560"));
        avatar.setStrokeWidth(3);

        Label initials = new Label(
            SessionManager.getInstance().getUsername().substring(0,1).toUpperCase()
        );
        initials.setStyle("-fx-text-fill:#e94560;-fx-font-size:34;-fx-font-weight:bold;");

        StackPane avatarStack = new StackPane(avatar, initials);

        Label usernameLbl = new Label(SessionManager.getInstance().getUsername());
        usernameLbl.setStyle("-fx-text-fill:white;-fx-font-size:18;-fx-font-weight:bold;");

        Label roleLbl = new Label(SessionManager.getInstance().isAdmin() ? "🛡  Admin" : "🎮  Player");
        roleLbl.setStyle(ArcadeStyle.LABEL_DIM);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#e94560;-fx-opacity:0.3;");

        Label cpTitle = new Label("CHANGE PASSWORD");
        cpTitle.setStyle(ArcadeStyle.LABEL_DIM + "-fx-font-weight:bold;-fx-font-size:11;");

        PasswordField currentPass = new PasswordField();
        currentPass.setPromptText("Current password");
        currentPass.setStyle(ArcadeStyle.FIELD);
        currentPass.setMaxWidth(Double.MAX_VALUE);

        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New password");
        newPass.setStyle(ArcadeStyle.FIELD);
        newPass.setMaxWidth(Double.MAX_VALUE);

        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm new password");
        confirmPass.setStyle(ArcadeStyle.FIELD);
        confirmPass.setMaxWidth(Double.MAX_VALUE);

        Label feedbackLbl = new Label(" ");
        feedbackLbl.setStyle(ArcadeStyle.ERROR);
        feedbackLbl.setWrapText(true);

        Button saveBtn = new Button("SAVE PASSWORD");
        saveBtn.setStyle(ArcadeStyle.BTN_RED);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> handlePasswordChange(
            currentPass.getText(), newPass.getText(), confirmPass.getText(),
            feedbackLbl, currentPass, newPass, confirmPass
        ));

        box.getChildren().addAll(
            avatarStack, usernameLbl, roleLbl, sep,
            cpTitle, currentPass, newPass, confirmPass,
            feedbackLbl, saveBtn
        );
        return box;
    }

    // ── Right: match history table + stats ──
    private VBox buildRightPanel() {
        VBox box = new VBox(14);
        box.setStyle(ArcadeStyle.PANEL + "-fx-padding:28;");
        HBox.setHgrow(box, Priority.ALWAYS);

        Label histTitle = new Label("MATCH HISTORY");
        histTitle.setStyle(ArcadeStyle.LABEL_DIM + "-fx-font-weight:bold;-fx-font-size:11;");

        TableView<MatchEntry> table = new TableView<>();
        table.setStyle("-fx-background-color:#0d0d1a;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<MatchEntry, String> gameCol = new TableColumn<>("GAME");
        gameCol.setCellValueFactory(new PropertyValueFactory<>("game"));

        TableColumn<MatchEntry, Integer> scoreCol = new TableColumn<>("SCORE");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setMaxWidth(100);

        TableColumn<MatchEntry, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setMaxWidth(140);

        table.getColumns().addAll(gameCol, scoreCol, dateCol);

        // Replace with: ScoreFileManager.getScoresByUser(SessionManager.getInstance().getUsername())
        ScoreFileManager sfm = new ScoreFileManager();
        String currentUser = SessionManager.getInstance().getUsername();
        ObservableList<MatchEntry> history = FXCollections.observableArrayList();
        for (String[] entry : sfm.getScoresByUser(currentUser)) {
        history.add(new MatchEntry(entry[1], Integer.parseInt(entry[2]), ""));
        }
        table.setItems(history);

        int total = history.stream().mapToInt(MatchEntry::getScore).sum();
        int best  = history.stream().mapToInt(MatchEntry::getScore).max().orElse(0);

        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
            statBox("TOTAL SCORE",  String.valueOf(total),            "#e94560"),
            statBox("BEST SCORE",   String.valueOf(best),             "#f5a623"),
            statBox("GAMES PLAYED", String.valueOf(history.size()),   "#50fa7b")
        );

        box.getChildren().addAll(histTitle, stats, table);
        return box;
    }

    private VBox statBox(String label, String value, String color) {
        VBox b = new VBox(4);
        b.setAlignment(Pos.CENTER);
        b.setStyle("-fx-background-color:#0f3460;-fx-background-radius:8;-fx-padding:10 20;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:" + color + ";-fx-font-size:20;-fx-font-weight:bold;");
        Label lbl = new Label(label);
        lbl.setStyle(ArcadeStyle.LABEL_DIM + "-fx-font-size:10;");
        b.getChildren().addAll(val, lbl);
        return b;
    }

    private void handlePasswordChange(String current, String newP, String confirm,
                                      Label feedback,
                                      PasswordField cf, PasswordField nf, PasswordField cnf) {
        if (current.isBlank() || newP.isBlank() || confirm.isBlank()) {
            feedback.setStyle(ArcadeStyle.ERROR);
            feedback.setText("⚠ Please fill in all fields.");
            return;
        }
        if (!newP.equals(confirm)) {
            feedback.setStyle(ArcadeStyle.ERROR);
            feedback.setText("❌ New passwords do not match.");
            return;
        }
        if (newP.length() < 4) {
            feedback.setStyle(ArcadeStyle.ERROR);
            feedback.setText("❌ Password must be at least 4 characters.");
            return;
        }
        // Stub — replace with: AuthService.getInstance().changePassword(username, current, newP)
        AuthService auth = new AuthService();
        boolean ok = auth.updatePassword(SessionManager.getInstance().getUsername(), current, newP);
        if (!ok) {
        feedback.setStyle(ArcadeStyle.ERROR);
        feedback.setText("❌ Current password is incorrect.");
        return;
        }
        feedback.setStyle(ArcadeStyle.SUCCESS);
        feedback.setText("✅ Password changed successfully!");
        cf.clear(); nf.clear(); cnf.clear();
    }

    // ── Model ──
    public static class MatchEntry {
        private final SimpleStringProperty  game, date;
        private final SimpleIntegerProperty score;

        public MatchEntry(String g, int s, String d) {
            game  = new SimpleStringProperty(g);
            score = new SimpleIntegerProperty(s);
            date  = new SimpleStringProperty(d);
        }

        public String  getGame()  { return game.get(); }
        public StringProperty gameProperty() { return game; }
        public int     getScore() { return score.get(); }
        public IntegerProperty scoreProperty() { return score; }
        public String  getDate()  { return date.get(); }
        public StringProperty dateProperty() { return date; }
    }

    public Scene getScene() { return scene; }
}



