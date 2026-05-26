
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainMenuScene {

    private final Scene scene;
    private final Stage stage;

    private static final String[][] GAMES = {
        {"⭕  TIC TAC TOE",  "#e94560", "1P vs AI  /  2-Player"},
        {"🃏  MEMORY MATCH", "#f5a623", "Flip & Match Pairs"},
        {"🌀  MAZE",         "#50fa7b", "Navigate to the Exit"},
        {"🎨  MASTERMIND",   "#bd93f9", "Crack the Secret Code"}
    };

    public MainMenuScene(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setStyle(ArcadeStyle.BG);
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setBottom(buildFooter());

        scene = new Scene(root, 880, 650);
    }

    private HBox buildHeader() {
        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER);
        h.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("🎮 ARCADE");
        logo.setStyle("-fx-text-fill:#e94560;-fx-font-size:22;-fx-font-weight:bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label user = new Label("👤  " + SessionManager.getInstance().getUsername().toUpperCase());
        user.setStyle(ArcadeStyle.LABEL_LIGHT + "-fx-padding:0 20 0 0;");
        Button logout = new Button("LOGOUT");
        logout.setStyle(ArcadeStyle.BTN_OUTLINE + "-fx-font-size:12;-fx-padding:6 14;");
        logout.setOnAction(e -> { SessionManager.getInstance().logout(); stage.setScene(new LoginScene(stage).getScene()); });

        h.getChildren().addAll(logo, sp, user, logout);
        return h;
    }

    private VBox buildCenter() {
        VBox box = new VBox(22);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding:30;");

        Label title = new Label("SELECT A GAME");
        title.setStyle(ArcadeStyle.LABEL_DIM + "-fx-letter-spacing:4;-fx-font-size:13;");

        GridPane grid = new GridPane();
        grid.setHgap(18); grid.setVgap(18);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < GAMES.length; i++) {
            final int idx = i;
            Button btn = gameCard(GAMES[i][0], GAMES[i][1], GAMES[i][2]);
            btn.setOnAction(e -> launchGame(idx));
            grid.add(btn, i % 2, i / 2);
        }
        box.getChildren().addAll(title, grid);
        return box;
    }

    private HBox buildFooter() {
        HBox bar = new HBox(14);
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color:#16213e;-fx-padding:14;-fx-border-color:#e94560;-fx-border-width:2 0 0 0;");

        Button lb = new Button("🏆  LEADERBOARD");
        lb.setStyle(ArcadeStyle.BTN_GHOST);
        lb.setOnAction(e -> stage.setScene(new LeaderboardScene(stage).getScene()));

        Button prof = new Button("👤  PROFILE");
        prof.setStyle(ArcadeStyle.BTN_GHOST);
        prof.setOnAction(e -> showProfile());

        bar.getChildren().addAll(lb, prof);

        if (SessionManager.getInstance().isAdmin()) {
            Button admin = new Button("🛡  ADMIN PANEL");
            admin.setStyle(ArcadeStyle.BTN_OUTLINE + "-fx-font-size:12;");
            admin.setOnAction(e -> stage.setScene(new AdminPanelScene(stage).getScene()));
            bar.getChildren().add(admin);
        }
        return bar;
    }

    private Button gameCard(String name, String color, String desc) {
        String base = "-fx-background-color:#16213e;-fx-background-radius:12;-fx-border-color:" + color + ";-fx-border-radius:12;-fx-border-width:2;";
        String hover = "-fx-background-color:#1e2d50;-fx-background-radius:12;-fx-border-color:" + color + ";-fx-border-radius:12;-fx-border-width:2;";

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(210, 130);
        box.setStyle(base);

        Label n = new Label(name);
        n.setStyle("-fx-text-fill:" + color + ";-fx-font-size:15;-fx-font-weight:bold;");
        n.setTextAlignment(TextAlignment.CENTER);
        n.setWrapText(true);
        Label d = new Label(desc);
        d.setStyle(ArcadeStyle.LABEL_DIM);
        box.getChildren().addAll(n, d);

        box.setOnMouseEntered(e -> box.setStyle(hover));
        box.setOnMouseExited (e -> box.setStyle(base));

        Button btn = new Button();
        btn.setGraphic(box);
        btn.setStyle("-fx-background-color:transparent;-fx-padding:0;-fx-cursor:hand;");
        return btn;
    }

    private void launchGame(int idx) {
        Scene next;
        if      (idx == 0) next = new TicTacToePane(stage).getScene();
        else if (idx == 1) next = new MemoryMatchPane(stage).getScene();
        else if (idx == 2) next = new MazePane(stage).getScene();
        else if (idx == 3) next = new MastermindPane(stage).getScene();
        else               next = scene;
        stage.setScene(next);
    }

    private void showProfile() {
    stage.setScene(new ProfileScene(stage).getScene());
}

    public Scene getScene() { return scene; }
}
