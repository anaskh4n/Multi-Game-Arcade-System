
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MemoryMatchPane {

    private final Stage stage;
    private final Scene scene;
    private final StackPane root = new StackPane();
    private final MemoryMatchGame game = new MemoryMatchGame();
    private final Button[][] btns = new Button[4][4];

    private int firstRow = -1, firstCol = -1;
    private boolean waiting = false;
    private Label movesLbl;

    public MemoryMatchPane(Stage stage) {
        this.stage = stage;

        BorderPane bp = new BorderPane();
        bp.setStyle(ArcadeStyle.BG);

        // header using helper
        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER.replace("#e94560","#f5a623"));
        h.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("← MENU");
        back.setStyle(ArcadeStyle.BTN_OUTLINE.replace("#e94560","#f5a623") + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label t = new Label("🃏  MEMORY MATCH");
        t.setStyle("-fx-text-fill:#f5a623;-fx-font-size:20;-fx-font-weight:bold;");
        h.getChildren().addAll(back, sp, t);
        bp.setTop(h);
        bp.setCenter(buildCenter());

        root.getChildren().add(bp);
        game.startGame();
        scene = new Scene(root, 880, 650);
    }

    private VBox buildCenter() {
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding:20;");

        movesLbl = new Label("Moves: 0");
        movesLbl.setStyle("-fx-text-fill:#f5a623;-fx-font-size:17;-fx-font-weight:bold;");

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        for (int r = 0; r < 4; r++) for (int c = 0; c < 4; c++) {
            final int row = r, col = c;
            Button btn = new Button("?");
            btn.setPrefSize(90, 90);
            btn.setStyle(cardBack());
            btn.setFont(javafx.scene.text.Font.font("Monospace", javafx.scene.text.FontWeight.BOLD, 22));
            btn.setOnAction(e -> handleFlip(row, col, btn));
            btns[r][c] = btn;
            grid.add(btn, c, r);
        }

        Button reset = new Button("↺  NEW GAME");
        reset.setStyle(ArcadeStyle.BTN_GHOST.replace("#aaaacc","#f5a623"));
        reset.setOnAction(e -> resetGame());

        box.getChildren().addAll(movesLbl, grid, reset);
        return box;
    }

    private void handleFlip(int row, int col, Button btn) {
        if (waiting || btns[row][col].isDisabled() || !btn.getText().equals("?")) return;

        game.flipCard(row, col);
        btn.setText(String.valueOf(game.getValue(row, col)));
        btn.setStyle(cardFront());

        if (firstRow == -1) {
            firstRow = row; firstCol = col;
        } else {
            waiting = true;
            int r1 = firstRow, c1 = firstCol, r2 = row, c2 = col;
            movesLbl.setText("Moves: " + (game.currentScore + 1)); 

            PauseTransition pause = new PauseTransition(Duration.millis(850));
            pause.setOnFinished(e -> {
                if (game.isMatch(r1, c1, r2, c2)) {
                    btns[r1][c1].setDisable(true); btns[r2][c2].setDisable(true);
                    btns[r1][c1].setStyle(cardMatched()); btns[r2][c2].setStyle(cardMatched());
                    if (game.allMatched()) {
                        PauseTransition d2 = new PauseTransition(Duration.millis(500));
                        d2.setOnFinished(ev -> root.getChildren().add(new GameOverPane(true, game.currentScore, stage, this::resetGame, "MemoryMatch")));
                        d2.play();
                    }
                } else {
                    game.unflipCard(r1, c1); game.unflipCard(r2, c2);
                    btns[r1][c1].setText("?"); btns[r1][c1].setStyle(cardBack());
                    btns[r2][c2].setText("?"); btns[r2][c2].setStyle(cardBack());
                }
                movesLbl.setText("Moves: " + game.currentScore);
                firstRow = -1; firstCol = -1; waiting = false;
            });
            pause.play();
        }
    }

    private void resetGame() {
        root.getChildren().removeIf(n -> n instanceof GameOverPane);
        game.startGame();
        firstRow = -1; firstCol = -1; waiting = false;
        movesLbl.setText("Moves: 0");
        for (Button[] row : btns) for (Button b : row) {
            b.setText("?"); b.setStyle(cardBack()); b.setDisable(false);
        }
    }

    private String cardBack()    { return "-fx-background-color:#0f3460;-fx-background-radius:8;-fx-border-color:#f5a623;-fx-border-radius:8;-fx-border-width:2;-fx-text-fill:#f5a623;-fx-cursor:hand;"; }
    private String cardFront()   { return "-fx-background-color:#1e2d50;-fx-background-radius:8;-fx-border-color:#eaeaea;-fx-border-radius:8;-fx-border-width:2;-fx-text-fill:white;-fx-cursor:hand;"; }
    private String cardMatched() { return "-fx-background-color:#1a3a1a;-fx-background-radius:8;-fx-border-color:#50fa7b;-fx-border-radius:8;-fx-border-width:2;-fx-text-fill:#50fa7b;-fx-cursor:hand;"; }

    public Scene getScene() { return scene; }
}
