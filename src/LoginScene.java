import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginScene {

    private final Scene scene;
    private final Stage stage;
    private int failedAttempts = 0;
    private AuthService auth = new AuthService();

    public LoginScene(Stage stage) {
        this.stage = stage;
        StackPane root = new StackPane();
        root.setStyle(ArcadeStyle.BG);

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(370);
        card.setStyle(ArcadeStyle.PANEL + "-fx-padding:42 50;");
        card.setEffect(new DropShadow(35, Color.web("#e94560", 0.45)));

        Label logo = new Label("🎮 ARCADE");
        logo.setStyle("-fx-text-fill:#e94560;-fx-font-size:34;-fx-font-weight:bold;");
        Label sub = new Label("MULTI-GAME SYSTEM");
        sub.setStyle("-fx-text-fill:#555577;-fx-font-size:11;-fx-letter-spacing:3;");
        VBox logoBox = new VBox(4, logo, sub);
        logoBox.setAlignment(Pos.CENTER);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#e94560;-fx-opacity:0.35;");

        Label uLbl = styledLabel("USERNAME");
        TextField userField = new TextField();
        userField.setPromptText("Enter username…");
        userField.setStyle(ArcadeStyle.FIELD);
        userField.setMaxWidth(Double.MAX_VALUE);

        Label pLbl = styledLabel("PASSWORD");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password…");
        passField.setStyle(ArcadeStyle.FIELD);
        passField.setMaxWidth(Double.MAX_VALUE);

        Label errorLbl = new Label(" ");
        errorLbl.setStyle(ArcadeStyle.ERROR);
        errorLbl.setWrapText(true);

        Button loginBtn = new Button("LOGIN");
        loginBtn.setStyle(ArcadeStyle.BTN_RED);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Button signupBtn = new Button("CREATE ACCOUNT");
        signupBtn.setStyle(ArcadeStyle.BTN_OUTLINE);
        signupBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> doLogin(userField.getText(), passField.getText(), errorLbl, loginBtn));
        passField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) doLogin(userField.getText(), passField.getText(), errorLbl, loginBtn); });
        signupBtn.setOnAction(e -> doSignup(userField.getText(), passField.getText(), errorLbl));

        card.getChildren().addAll(logoBox, sep, uLbl, userField, pLbl, passField, errorLbl, loginBtn, signupBtn);
        root.getChildren().add(card);
        scene = new Scene(root, 880, 650);
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle(ArcadeStyle.LABEL_DIM + "-fx-font-weight:bold;-fx-font-size:11;");
        return l;
    }

    private void doLogin(String user, String pass, Label err, Button btn) {
        if (failedAttempts >= 3) { err.setText("🔒 Account locked — too many failed attempts."); btn.setDisable(true); return; }
        if (user.isBlank() || pass.isBlank()) { err.setText("⚠ Please fill in both fields."); return; }

        User u = auth.login(user, pass);
        if (u != null) {
            if (u.isBanned()) { err.setText("🚫 Your account has been banned."); return; }
            SessionManager.getInstance().login(u.getUsername(), u.getRole().equals("admin"));
            if (u.getRole().equals("admin")) {
                stage.setScene(new AdminPanelScene(stage).getScene());
            } else {
                stage.setScene(new MainMenuScene(stage).getScene());
            }
        } else {
            failedAttempts++;
            int left = 3 - failedAttempts;
            if (left > 0) err.setText("❌ Invalid credentials. " + left + " attempt(s) left.");
            else { err.setText("🔒 Account locked."); btn.setDisable(true); }
        }
    }

    private void doSignup(String user, String pass, Label err) {
        if (user.isBlank() || pass.isBlank()) { err.setText("⚠ Fill both fields to register."); return; }
        boolean ok = auth.register(user, pass);
        if (ok) {
            err.setStyle(ArcadeStyle.SUCCESS);
            err.setText("✅ Account created! You can now log in.");
        } else {
            err.setText("❌ Username already taken.");
        }
    }

    public Scene getScene() { return scene; }
}