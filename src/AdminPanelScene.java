import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPanelScene {

    private final Scene scene;
    private final Stage stage;

    public AdminPanelScene(Stage stage) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setStyle(ArcadeStyle.BG);

        HBox header = new HBox();
        header.setStyle(ArcadeStyle.HEADER);
        header.setAlignment(Pos.CENTER_LEFT);
        Button back = new Button("← BACK");
        back.setStyle(ArcadeStyle.BTN_OUTLINE + "-fx-font-size:12;-fx-padding:6 14;");
        back.setOnAction(e -> stage.setScene(new MainMenuScene(stage).getScene()));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label t = new Label("🛡  ADMIN CONTROL PANEL");
        t.setStyle("-fx-text-fill:#e94560;-fx-font-size:20;-fx-font-weight:bold;");
        Button logout = new Button("⏻ LOGOUT");
        logout.setStyle(ArcadeStyle.BTN_OUTLINE + "-fx-font-size:12;-fx-padding:6 14;");
        logout.setOnAction(e -> {
            SessionManager.getInstance().logout();
            stage.setScene(new LoginScene(stage).getScene());
        });
        header.getChildren().addAll(back, sp, t, logout);
        root.setTop(header);

        root.setCenter(buildCenter());
        scene = new Scene(root, 880, 650);
    }

    private VBox buildCenter() {
        VBox box = new VBox(16);
        box.setStyle("-fx-padding:28;");

        Label statusLbl = new Label(" ");
        statusLbl.setStyle(ArcadeStyle.SUCCESS);

        TableView<UserRow> table = new TableView<>();
        table.setStyle("-fx-background-color:#16213e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(360);

        TableColumn<UserRow, String> uCol = new TableColumn<>("USERNAME");
        uCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<UserRow, String> rCol = new TableColumn<>("ROLE");
        rCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        rCol.setMaxWidth(120);

        TableColumn<UserRow, String> sCol = new TableColumn<>("STATUS");
        sCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        sCol.setMaxWidth(130);

        table.getColumns().addAll(uCol, rCol, sCol);

        AuthService auth = new AuthService();
        ObservableList<UserRow> users = FXCollections.observableArrayList();
        for (User u : auth.getAllUsers()) {
            users.add(new UserRow(u.getUsername(), u.getRole(), u.isBanned() ? "Banned" : "Active"));
        }
        table.setItems(users);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button banBtn        = aBtn("🚫 BAN","#e94560");
        Button unbanBtn      = aBtn("✅ UNBAN","#50fa7b");
        Button deleteBtn     = aBtn("🗑 DELETE","#ff7070");
        Button resetBtn      = aBtn("🔄 RESET SCORES","#f5a623");
        Button createAdminBtn = aBtn("➕ CREATE ADMIN","#bd93f9");

        banBtn.setOnAction(e -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (sel.getRole().equals("admin")) { status(statusLbl,"❌ Cannot ban admin accounts.", false); return; }
            sel.setStatus("Banned"); table.refresh();
            auth.banUser(sel.getUsername());
            status(statusLbl,"✅ '" + sel.getUsername() + "' has been banned.", true);
        });

        unbanBtn.setOnAction(e -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            auth.unbanUser(sel.getUsername());
            sel.setStatus("Active"); table.refresh();
            status(statusLbl,"✅ '" + sel.getUsername() + "' has been unbanned.", true);
        });

        deleteBtn.setOnAction(e -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (sel.getRole().equals("admin")) { status(statusLbl,"❌ Cannot delete admin accounts.", false); return; }
            auth.deleteUser(sel.getUsername());
            users.remove(sel);
            status(statusLbl,"✅ '" + sel.getUsername() + "' deleted.", true);
        });

        resetBtn.setOnAction(e -> {
            UserRow sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            new ScoreFileManager().resetScores(sel.getUsername());
            status(statusLbl,"✅ Scores reset for '" + sel.getUsername() + "'.", true);
        });

        createAdminBtn.setOnAction(e -> {
            TextInputDialog userDialog = new TextInputDialog();
            userDialog.setTitle("Create Admin");
            userDialog.setHeaderText("New Admin Username:");
            userDialog.showAndWait().ifPresent(username -> {
                TextInputDialog passDialog = new TextInputDialog();
                passDialog.setTitle("Create Admin");
                passDialog.setHeaderText("New Admin Password:");
                passDialog.showAndWait().ifPresent(password -> {
                    boolean ok = auth.registerAdmin(username, password);
                    if (ok) {
                        users.add(new UserRow(username, "admin", "Active"));
                        status(statusLbl,"✅ Admin '" + username + "' created.", true);
                    } else {
                        status(statusLbl,"❌ Username already taken.", false);
                    }
                });
            });
        });

        actions.getChildren().addAll(banBtn, unbanBtn, deleteBtn, resetBtn, createAdminBtn);
        box.getChildren().addAll(table, actions, statusLbl);
        return box;
    }

    private void status(Label lbl, String msg, boolean ok) {
        lbl.setStyle(ok ? ArcadeStyle.SUCCESS : ArcadeStyle.ERROR);
        lbl.setText(msg);
    }

    private Button aBtn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:transparent;-fx-text-fill:" + color + ";-fx-border-color:" + color + ";-fx-border-radius:6;-fx-cursor:hand;-fx-padding:7 14;-fx-font-size:12;-fx-font-weight:bold;");
        return b;
    }

    public static class UserRow {
        private final SimpleStringProperty username, role, status;
        public UserRow(String u, String r, String s) {
            username = new SimpleStringProperty(u);
            role     = new SimpleStringProperty(r);
            status   = new SimpleStringProperty(s);
        }
        public String getUsername() { return username.get(); }
        public StringProperty usernameProperty() { return username; }
        public String getRole()     { return role.get(); }
        public StringProperty roleProperty()     { return role; }
        public String getStatus()   { return status.get(); }
        public void setStatus(String s) { status.set(s); }
        public StringProperty statusProperty()   { return status; }
    }

    public Scene getScene() { return scene; }
}
