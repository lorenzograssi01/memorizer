package it.unipi.memorizer.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Window
{
    private static final Logger Log = LogManager.getLogger(Window.class);
    private final Stage st;
    
    public Stage getStage()
    {
        return st;
    }
    
    public Scene getScene()
    {
        return st.getScene();
    }
    
    public static void createAlertBox(String message)
    {
        Stage st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle("Memorizer: Alert");
        st.setResizable(false);
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
        label.setTextAlignment(TextAlignment.CENTER);
        Button ok = new Button("OK");
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, ok);
        ok.setOnAction(e -> st.close());
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-padding: 20px;");
        ok.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-background-radius: 5; -fx-cursor: hand;");
        Scene scene = new Scene(layout);
        st.setScene(scene);
        st.getIcons().add(new Image("file:src/main/resources/alert.png"));
        layout.addEventHandler(KeyEvent.KEY_PRESSED, event ->
        {
            if(event.getCode() == KeyCode.ENTER)
            {
                st.close();
            }
        });
        st.showAndWait();
    }
    
    public static boolean createConfirmationBox(String message)
    {
        Stage st = new Stage();
        st.initModality(Modality.APPLICATION_MODAL);
        st.setTitle("Memorizer: Alert");
        st.setResizable(false);
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
        label.setTextAlignment(TextAlignment.CENTER);
        Button yes = new Button("Yes");
        Button no = new Button("No");
        boolean[] input = new boolean[1];
        input[0] = false;
        VBox layout = new VBox(10);
        HBox buttons = new HBox(10);
        layout.getChildren().addAll(label, buttons);
        buttons.getChildren().addAll(yes, no);
        yes.setOnAction(e ->
        {
            input[0] = true;
            st.close();
        });
        no.setOnAction(e -> st.close());
        layout.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-padding: 20px;");
        yes.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-background-radius: 5; -fx-cursor: hand;");
        no.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-background-radius: 5; -fx-cursor: hand;");
        Scene scene = new Scene(layout);
        st.setScene(scene);
        layout.addEventHandler(KeyEvent.KEY_PRESSED, event ->
        {
            if(event.getCode() == KeyCode.ENTER)
            {
                input[0] = true;
                st.close();
            }
        });
        st.getIcons().add(new Image("file:src/main/resources/alert.png"));
        st.showAndWait();
        return input[0];
    }
    
    public Window(Stage st, String fxml, int width, int height, String title, Consumer<Object> onControllerReady, boolean focus)
    {
        this.st = st;
        Parent root = App.loadFXML(fxml, loader ->
        {
            App.loaderOnLoad(loader, onControllerReady);
        });
        Scene scene = new Scene(root, width, height);
        st.setTitle(title);
        st.setScene(scene);
        st.setResizable(false);
        st.getIcons().add(new Image("file:src/main/resources/icon.png"));
        if(focus)
        {
            st.initModality(Modality.APPLICATION_MODAL);
            st.showAndWait();
        }
        else
            st.show();
        Log.debug("Created new window");
    }
    
    public Window(String fxml, int width, int height, String title, Consumer<Object> onControllerReady, boolean focus)
    {
        this(new Stage(), fxml, width, height, title, onControllerReady, focus);
    }
    
    void setMain()
    {
        st.setOnCloseRequest(e -> Platform.exit());
    }

    void setRoot(String fxml, Consumer<Object> onControllerReady)
    {
        st.getScene().setRoot(App.loadFXML(fxml, loader ->
        {
            App.loaderOnLoad(loader, onControllerReady);
        }));
        Log.debug("Changed scene");
    }
}