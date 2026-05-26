
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOverPane extends VBox {

    public GameOverPane(boolean won, int score, Stage stage, Runnable onPlayAgain, String gameName) {
        super(22);
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color:rgba(10,10,22,0.94);-fx-padding:50;");

        new ScoreFileManager().saveScore(SessionManager.getInstance().getUsername(), gameName, score);
        Text result = new Text(won ? "🏆  YOU WIN!" : "💀  GAME OVER");
        result.setStyle("-fx-font-size:44;-fx-font-weight:bold;-fx-fill:" + (won ? "#50fa7b" : "#e94560") + ";");

        Label scoreLbl = new Label("Score: " + score);
        scoreLbl.setStyle("-fx-text-fill:#f5a623;-fx-font-size:24;-fx-font-weight:bold;");

        Button again = new Button("▶  PLAY AGAIN");
        again.setStyle(ArcadeStyle.BTN_RED);
        again.setOnAction(e -> onPlayAgain.run());

        Button menu = new Button("🏠  BACK TO MENU");
        menu.setStyle(ArcadeStyle.BTN_GHOST);
        menu.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));

        ScaleTransition pulse = new ScaleTransition(Duration.millis(750), result);
        pulse.setFromX(1); pulse.setToX(1.07);
        pulse.setFromY(1); pulse.setToY(1.07);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        getChildren().addAll(result, scoreLbl, again, menu);
    }
}
