import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TicTacToePane {

    private final Stage stage;
    private final Scene scene;
    private final StackPane root = new StackPane();
    private final TicTacToeGame game = new TicTacToeGame();
    private final Button[][] cells  = new Button[3][3];
    private Label statusLbl;
    private boolean isXTurn   = true;
    private boolean twoPlayer = false;

    public TicTacToePane(Stage stage) {
        this.stage = stage;

        BorderPane bp = new BorderPane();
        bp.setStyle(ArcadeStyle.BG);
        bp.setTop(gameHeader("⭕  TIC TAC TOE", "#e94560"));
        bp.setCenter(buildCenter());

        root.getChildren().add(bp);
        game.startGame();
        scene = new Scene(root, 880, 650);
    }

    private VBox buildCenter() {
        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding:20;");

        // ── Mode toggle ──
        ToggleGroup tg = new ToggleGroup();
        RadioButton oneP = radio("1P  vs AI", tg, true);
        RadioButton twoP = radio("2-Player",  tg, false);
        tg.selectedToggleProperty().addListener((ob, o, n) -> {
            twoPlayer = twoP.isSelected(); resetGame();
        });
        HBox modeRow = new HBox(18, new Label("MODE:") {{ setStyle(ArcadeStyle.LABEL_DIM); }}, oneP, twoP);
        modeRow.setAlignment(Pos.CENTER);

        statusLbl = new Label("Player X's Turn");
        statusLbl.setStyle("-fx-text-fill:#e94560;-fx-font-size:17;-fx-font-weight:bold;");

        // ── Grid ──
        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.setAlignment(Pos.CENTER);

        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) {
            final int row = r, col = c;
            Button btn = new Button();
            btn.setPrefSize(115, 115);
            btn.setStyle(cellStyle(""));
            btn.setFont(Font.font("Monospace", FontWeight.BOLD, 42));
            btn.setOnAction(e -> handleMove(row, col, btn));
            cells[r][c] = btn;
            grid.add(btn, c, r);
        }

        Button reset = new Button("↺  RESET");
        reset.setStyle(ArcadeStyle.BTN_GHOST.replace("#aaaacc","#f5a623"));
        reset.setOnAction(e -> resetGame());

        box.getChildren().addAll(modeRow, statusLbl, grid, reset);
        return box;
    }

    private void handleMove(int row, int col, Button btn) {
        char mark = isXTurn ? 'X' : 'O';
        if (!game.makeMove(row, col, mark)) return;
        btn.setText(String.valueOf(mark));
        btn.setStyle(cellStyle(String.valueOf(mark)));
        btn.setDisable(true);

        char winner = game.checkWinner();
        if (winner != '-') { endGame(winner); return; }

        isXTurn = !isXTurn;
        statusLbl.setText("Player " + (isXTurn ? "X" : "O") + "'s Turn");

        if (!twoPlayer && !isXTurn) {
            PauseTransition ai = new PauseTransition(Duration.millis(400));
            ai.setOnFinished(e -> {
                game.aiMove();
                refreshBoard();
                char w2 = game.checkWinner();
                if (w2 != '-') { endGame(w2); return; }
                isXTurn = true;
                statusLbl.setText("Player X's Turn");
            });
            ai.play();
        }
    }

    private void refreshBoard() {
        char[][] board = game.getBoard();
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) {
            char ch = board[r][c];
            if (ch != '-') {
                cells[r][c].setText(String.valueOf(ch));
                cells[r][c].setStyle(cellStyle(String.valueOf(ch)));
                cells[r][c].setDisable(true);
            }
        }
    }

    private void endGame(char winner) {
        disableAll();
        boolean won  = (winner == 'X') || (twoPlayer && winner == 'O');
        String  msg  = winner == 'D' ? "🤝  Draw!": (winner == 'X' ? "🎉  Player X Wins!" : (twoPlayer ? "🎉  Player O Wins!" : "🤖  AI Wins!"));
        statusLbl.setText(msg);
        PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
        delay.setOnFinished(e -> {
        GameOverPane pane = new GameOverPane(winner != 'D' && won, game.currentScore, stage, this::resetGame, "TicTacToe");            
        root.getChildren().add(pane);});
        delay.play();
    }

    private void disableAll() {
        for (Button[] row : cells) for (Button b : row) b.setDisable(true);
    }

    private void resetGame() {
        root.getChildren().removeIf(n -> n instanceof GameOverPane);
        game.startGame();
        isXTurn = true;
        statusLbl.setText("Player X's Turn");
        for (Button[] row : cells) for (Button b : row) {
            b.setText(""); b.setStyle(cellStyle("")); b.setDisable(false);
        }
    }

    private String cellStyle(String mark) {
        String color = mark.equals("X") ? "#e94560" : mark.equals("O") ? "#50fa7b" : "#555577";
        return "-fx-background-color:#16213e;-fx-background-radius:10;-fx-border-color:#0f3460;-fx-border-radius:10;-fx-border-width:2;-fx-text-fill:" + color + ";-fx-cursor:hand;";
    }

    private RadioButton radio(String txt, ToggleGroup tg, boolean sel) {
        RadioButton r = new RadioButton(txt);
        r.setToggleGroup(tg); r.setSelected(sel);
        r.setStyle("-fx-text-fill:#aaaacc;");
        return r;
    }

    HBox gameHeader(String title, String color) {
        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER.replace("#e94560", color));
        h.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("← MENU");
        back.setStyle(ArcadeStyle.BTN_OUTLINE.replace("#e94560", color) + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label t = new Label(title);
        t.setStyle("-fx-text-fill:" + color + ";-fx-font-size:20;-fx-font-weight:bold;");
        h.getChildren().addAll(back, sp, t);
        return h;
    }

    public Scene getScene() { return scene; }
}
