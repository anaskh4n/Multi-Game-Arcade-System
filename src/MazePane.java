
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class MazePane {

    private final Stage stage;
    private final Scene scene;
    private final StackPane root = new StackPane();
    private final MazeGame game  = new MazeGame();
    private GridPane mazeGrid    = new GridPane();
    private Label movesLbl;
    private boolean easy = true;

    public MazePane(Stage stage) {
        this.stage = stage;

        BorderPane bp = new BorderPane();
        bp.setStyle(ArcadeStyle.BG);

        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER.replace("#e94560","#50fa7b"));
        h.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("← MENU");
        back.setStyle(ArcadeStyle.BTN_OUTLINE.replace("#e94560","#50fa7b") + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label t = new Label("🌀  MAZE");
        t.setStyle("-fx-text-fill:#50fa7b;-fx-font-size:20;-fx-font-weight:bold;");
        h.getChildren().addAll(back, sp, t);
        bp.setTop(h);
        bp.setCenter(buildCenter());

        root.getChildren().add(bp);
        game.startGame(easy);
        renderMaze();

        scene = new Scene(root, 880, 650);
        scene.setOnKeyPressed(this::handleKey);
        // Ensure the scene can receive key events
        root.setFocusTraversable(true);
        javafx.application.Platform.runLater(root::requestFocus);
    }

    private VBox buildCenter() {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding:16;");

        // ── Controls row ──
        ToggleGroup tg = new ToggleGroup();
        RadioButton easyBtn = rb("Easy", tg, true);
        RadioButton hardBtn = rb("Hard", tg, false);
        tg.selectedToggleProperty().addListener((ob, o, n) -> {
            easy = easyBtn.isSelected();
            resetMaze();
        });

        movesLbl = new Label("Moves: 0");
        movesLbl.setStyle("-fx-text-fill:#50fa7b;-fx-font-size:16;-fx-font-weight:bold;");

        Label hint = new Label("Use  ↑ ↓ ← →  arrow keys to move");
        hint.setStyle(ArcadeStyle.LABEL_DIM);

        HBox controls = new HBox(18, new Label("DIFFICULTY:") {{ setStyle(ArcadeStyle.LABEL_DIM); }}, easyBtn, hardBtn);
        controls.setAlignment(Pos.CENTER);

        Button reset = new Button("↺  RESET");
        reset.setStyle(ArcadeStyle.BTN_GHOST.replace("#aaaacc","#50fa7b"));
        reset.setOnAction(e -> resetMaze());

        mazeGrid = new GridPane();
        mazeGrid.setHgap(2); mazeGrid.setVgap(2);
        mazeGrid.setAlignment(Pos.CENTER);

        box.getChildren().addAll(controls, movesLbl, hint, mazeGrid, reset);
        return box;
    }

    private void renderMaze() {
        mazeGrid.getChildren().clear();
        char[][] board = game.getBoard();
        double cellSize = Math.min(480.0 / board[0].length, 380.0 / board.length);
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                Label cell = new Label();
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setAlignment(Pos.CENTER);
                char cell_ch = board[r][c];
                if (cell_ch == '#') {
                    cell.setStyle("-fx-background-color:#111133;-fx-border-color:#0d0d1a;-fx-border-width:1;");
                } else if (cell_ch == ' ') {
                    cell.setStyle("-fx-background-color:#1e2d50;-fx-border-color:#0d0d1a;-fx-border-width:1;");
                } else if (cell_ch == 'P') {
                    cell.setStyle("-fx-background-color:#3399ff;-fx-border-color:#0d0d1a;-fx-border-width:1;");
                    cell.setText("●"); cell.setTextFill(javafx.scene.paint.Color.WHITE);
                } else if (cell_ch == 'E') {
                    cell.setStyle("-fx-background-color:#1a3a1a;-fx-border-color:#50fa7b;-fx-border-width:1;");
                    cell.setText("★"); cell.setTextFill(javafx.scene.paint.Color.web("#50fa7b"));
                } else if (cell_ch == 'S') {
                    cell.setStyle("-fx-background-color:#1a1a3a;-fx-border-color:#0d0d1a;-fx-border-width:1;");
                    cell.setText("S"); cell.setTextFill(javafx.scene.paint.Color.web("#aaaacc"));
                }
                mazeGrid.add(cell, c, r);
            }
        }
    }

    private void handleKey(KeyEvent e) {
        String dir;
        if      (e.getCode() == javafx.scene.input.KeyCode.UP)    dir = "UP";
        else if (e.getCode() == javafx.scene.input.KeyCode.DOWN)  dir = "DOWN";
        else if (e.getCode() == javafx.scene.input.KeyCode.LEFT)  dir = "LEFT";
        else if (e.getCode() == javafx.scene.input.KeyCode.RIGHT) dir = "RIGHT";
        else dir = null;
        if (dir == null) return;
        game.move(dir);
        movesLbl.setText("Moves: " + game.totalMoves);        
        renderMaze();
        if (game.isExitReached()) {
        root.getChildren().add(new GameOverPane(true, game.calculateScore(), stage, this::resetMaze, "Maze"));  
        }
    }

    private void resetMaze() {
        root.getChildren().removeIf(n -> n instanceof GameOverPane);
        game.startGame(easy);
        movesLbl.setText("Moves: 0");
        renderMaze();
        root.requestFocus();
    }

    private RadioButton rb(String txt, ToggleGroup tg, boolean sel) {
        RadioButton r = new RadioButton(txt);
        r.setToggleGroup(tg); r.setSelected(sel);
        r.setStyle("-fx-text-fill:#aaaacc;");
        return r;
    }

    public Scene getScene() { return scene; }
}