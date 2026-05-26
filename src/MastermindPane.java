
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class MastermindPane {

    private final Stage stage;
    private final Scene scene;
    private final StackPane root   = new StackPane();
    private final MastermindGame game = new MastermindGame();

    // 6 game colors: code → hex
    private static final String[] CODES   = {"R","G","B","Y","W","P"};
    private static final String[] COLORS  = {"#ff5555","#50fa7b","#8be9fd","#f1fa8c","#f8f8f2","#bd93f9"};
    private static final String[] COLOR_NAMES = {"Red","Green","Blue","Yellow","White","Purple"};

    private static final int MAX_ATTEMPTS = 10;
    private static final int CODE_LEN     = 4;

    private final String[] currentGuess = new String[CODE_LEN];
    private int currentSlot = 0;
    private int currentRow  = 0;

    // UI rows: each row has 4 guess circles + 4 feedback circles
    private final Circle[][]   guessCells    = new Circle[MAX_ATTEMPTS][CODE_LEN];
    // Secret code display circles (shown on loss)
    private final Circle[] secretCircles = new Circle[CODE_LEN];

    private Label statusLbl;

    public MastermindPane(Stage stage) {
        this.stage = stage;

        BorderPane bp = new BorderPane();
        bp.setStyle(ArcadeStyle.BG);

        HBox h = new HBox();
        h.setStyle(ArcadeStyle.HEADER.replace("#e94560","#bd93f9"));
        h.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("← MENU");
        back.setStyle(ArcadeStyle.BTN_OUTLINE.replace("#e94560","#bd93f9") + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label t = new Label("🎨  MASTERMIND");
        t.setStyle("-fx-text-fill:#bd93f9;-fx-font-size:20;-fx-font-weight:bold;");
        h.getChildren().addAll(back, sp, t);
        bp.setTop(h);
        bp.setCenter(buildCenter());

        root.getChildren().add(bp);
        game.startGame();
        scene = new Scene(root, 880, 650);
    }

    private HBox buildCenter() {
        HBox outer = new HBox(20);
        outer.setAlignment(Pos.CENTER);
        outer.setStyle("-fx-padding:18;");

        // ── Left: Attempt grid ──
        VBox gridBox = new VBox(5);
        gridBox.setAlignment(Pos.CENTER);

        statusLbl = new Label("Guess the 4-color code! (10 attempts)");
        statusLbl.setStyle("-fx-text-fill:#bd93f9;-fx-font-size:13;-fx-font-weight:bold;");

        // ── Secret code row (hidden until loss) ──
        HBox secretRow = new HBox(6);
        secretRow.setAlignment(Pos.CENTER_LEFT);
        Label secretLbl = new Label("Secret: ");
        secretLbl.setStyle("-fx-text-fill:#ff5555;-fx-font-weight:bold;-fx-font-size:12;");
        for (int i = 0; i < CODE_LEN; i++) {
            Circle c = new Circle(12);
            c.setFill(javafx.scene.paint.Color.web("#222244"));
            c.setStroke(javafx.scene.paint.Color.web("#444466"));
            c.setStrokeWidth(1.5);
            secretCircles[i] = c;
            secretRow.getChildren().add(c);
        }
        secretRow.setVisible(false); // hidden by default
        secretRow.getChildren().add(0, secretLbl);

        GridPane attemptGrid = new GridPane();
        attemptGrid.setHgap(6); attemptGrid.setVgap(5);
        attemptGrid.setAlignment(Pos.CENTER);

        // Row labels + guess circles + feedback circles
        for (int row = 0; row < MAX_ATTEMPTS; row++) {
            Label rowLbl = new Label((MAX_ATTEMPTS - row) + "");
            rowLbl.setStyle(ArcadeStyle.LABEL_DIM + "-fx-min-width:20;");
            attemptGrid.add(rowLbl, 0, row);

            HBox guessRow = new HBox(5);
            for (int col = 0; col < CODE_LEN; col++) {
                Circle c = new Circle(17);
                c.setFill(javafx.scene.paint.Color.web("#16213e"));
                c.setStroke(javafx.scene.paint.Color.web(row == 0 ? "#bd93f9" : "#333355"));
                c.setStrokeWidth(2);
                guessCells[row][col] = c;
                guessRow.getChildren().add(c);
            }
            attemptGrid.add(guessRow, 1, row);
        }

        gridBox.getChildren().addAll(statusLbl, secretRow, attemptGrid);

        // ── Right: Color picker + submit ──
        VBox picker = new VBox(14);
        picker.setAlignment(Pos.CENTER);
        picker.setStyle("-fx-padding:10;");

        Label pickLbl = new Label("SELECT COLOR");
        pickLbl.setStyle(ArcadeStyle.LABEL_DIM + "-fx-font-weight:bold;");

        // Preview of current guess
        HBox previewRow = new HBox(6);
        previewRow.setAlignment(Pos.CENTER);
        Circle[] preview = new Circle[CODE_LEN];
        for (int i = 0; i < CODE_LEN; i++) {
            Circle c = new Circle(14);
            c.setFill(javafx.scene.paint.Color.web("#16213e"));
            c.setStroke(javafx.scene.paint.Color.web("#bd93f9"));
            c.setStrokeWidth(2);
            preview[i] = c;
            previewRow.getChildren().add(c);
        }

        VBox colorBtns = new VBox(8);
        colorBtns.setAlignment(Pos.CENTER);
        for (int i = 0; i < CODES.length; i++) {
            final int ci = i;
            Button cb = new Button(COLOR_NAMES[i]);
            cb.setStyle("-fx-background-color:" + COLORS[i] + ";-fx-text-fill:#111;-fx-font-weight:bold;" +
                    "-fx-background-radius:8;-fx-cursor:hand;-fx-padding:7 20;-fx-min-width:110;");
            cb.setOnAction(e -> {
                if (currentSlot < CODE_LEN && currentRow < MAX_ATTEMPTS) {
                    currentGuess[currentSlot] = CODES[ci];
                    guessCells[currentRow][currentSlot].setFill(javafx.scene.paint.Color.web(COLORS[ci]));
                    preview[currentSlot].setFill(javafx.scene.paint.Color.web(COLORS[ci]));
                    currentSlot++;
                }
            });
            colorBtns.getChildren().add(cb);
        }

        Button clearBtn = new Button("⌫  CLEAR");
        clearBtn.setStyle(ArcadeStyle.BTN_GHOST + "-fx-font-size:12;");
        clearBtn.setOnAction(e -> {
            if (currentSlot > 0) {
                currentSlot--;
                currentGuess[currentSlot] = null;
                guessCells[currentRow][currentSlot].setFill(javafx.scene.paint.Color.web("#16213e"));
                preview[currentSlot].setFill(javafx.scene.paint.Color.web("#16213e"));
            }
        });

        Button submitBtn = new Button("✔  SUBMIT");
        submitBtn.setStyle(ArcadeStyle.BTN_RED.replace("#e94560","#bd93f9").replace("white","#111"));
        submitBtn.setOnAction(e -> handleSubmit(preview, secretRow));

        Button resetBtn = new Button("↺  NEW GAME");
        resetBtn.setStyle(ArcadeStyle.BTN_GHOST);
        resetBtn.setOnAction(e -> resetGame(preview, secretRow));

        picker.getChildren().addAll(pickLbl, previewRow, colorBtns, clearBtn, submitBtn, resetBtn);

        outer.getChildren().addAll(gridBox, picker);
        return outer;
    }

    private void handleSubmit(Circle[] preview, HBox secretRow) {
        if (currentSlot < CODE_LEN) { statusLbl.setText("⚠ Pick all 4 colors first!"); return; }

        int[] result = game.checkGuess(currentGuess.clone());
        int exact = result[0], colorMatch = result[1];

        if (exact == CODE_LEN) {
            statusLbl.setText("🎉 You cracked the code in " + (currentRow + 1) + " attempt(s)!");
            root.getChildren().add(new GameOverPane(true, (MAX_ATTEMPTS - currentRow) * 10, stage, () -> resetGame(preview, secretRow), "Mastermind"));
            return;
        }

        currentRow++;
        if (currentRow >= MAX_ATTEMPTS) {
            // Reveal secret code with actual colors
            String[] secret = game.getSecretCode();
            for (int i = 0; i < CODE_LEN; i++) {
                for (int j = 0; j < CODES.length; j++) {
                    if (CODES[j].equals(secret[i])) {
                        secretCircles[i].setFill(javafx.scene.paint.Color.web(COLORS[j]));
                        break;
                    }
                }
            }
            secretRow.setVisible(true);
            statusLbl.setText("💀 Out of attempts! The secret code is shown above.");
            root.getChildren().add(new GameOverPane(false, 0, stage, () -> resetGame(preview, secretRow), "Mastermind"));
            return;
        }

        // Highlight new row
        for (int c = 0; c < CODE_LEN; c++) {
            guessCells[currentRow][c].setStroke(javafx.scene.paint.Color.web("#bd93f9"));
            if (currentRow > 0) guessCells[currentRow-1][c].setStroke(javafx.scene.paint.Color.web("#333355"));
        }

        statusLbl.setText("Attempt " + (currentRow + 1) + " / " + MAX_ATTEMPTS
                + "  —  ⬛ Exact: " + exact + "   ⬜ Color: " + colorMatch);
        currentSlot = 0;
        for (Circle c : preview) c.setFill(javafx.scene.paint.Color.web("#16213e"));
    }

    private void resetGame(Circle[] preview, HBox secretRow) {
        root.getChildren().removeIf(n -> n instanceof GameOverPane);
        game.startGame();
        currentRow = 0; currentSlot = 0;
        secretRow.setVisible(false);
        for (int r = 0; r < MAX_ATTEMPTS; r++) {
            for (int c = 0; c < CODE_LEN; c++) {
                guessCells[r][c].setFill(javafx.scene.paint.Color.web("#16213e"));
                guessCells[r][c].setStroke(javafx.scene.paint.Color.web(r == 0 ? "#bd93f9" : "#333355"));
            }
        }
        for (Circle c : preview) c.setFill(javafx.scene.paint.Color.web("#16213e"));
        for (Circle c : secretCircles) c.setFill(javafx.scene.paint.Color.web("#222244"));
        statusLbl.setText("Guess the 4-color code! (10 attempts)");
    }

    public Scene getScene() { return scene; }
}
