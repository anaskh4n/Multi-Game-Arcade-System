
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("🎮 Multi-Game Arcade System");
        stage.setWidth(880);
        stage.setHeight(650);
        stage.setResizable(false);
        stage.setScene(new LoginScene(stage).getScene());
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}