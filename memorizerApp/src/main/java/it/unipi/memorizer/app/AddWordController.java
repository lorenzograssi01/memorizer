package it.unipi.memorizer.app;

import it.unipi.memorizer.app.ServiceRequest.HttpMethod;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddWordController
{
    private static final Logger Log = LogManager.getLogger(AddWordController.class);
    @FXML private HBox topBar;
    @FXML private VBox root;
    @FXML private TextField word;
    @FXML private TextArea description;
    @FXML private TextField translation;
    @FXML private ProgressIndicator loader;
    private ObservableList ol;
    private boolean external = false;
    private final EventHandler<KeyEvent> keyEventHandler = event ->
    {
        if(event.getCode() == KeyCode.ENTER)
        {
            addWord();
        }
    };
    
    public void setExternal(ObservableList ol)
    {
        external = true;
        this.ol = ol;
        root.getChildren().remove(topBar);
    }
    
    @FXML private void initialize()
    {
        loader.setVisible(false);
        description.addEventFilter(KeyEvent.KEY_PRESSED, event ->
        {
            if (event.getCode() == KeyCode.ENTER)
            {
                event.consume();
                root.fireEvent(event);
            }
            if (event.getCode() == KeyCode.TAB)
            {
                event.consume();
                translation.requestFocus();
            }
        });
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }
    
    @FXML private void goBack()
    {
        App.getMainWindow().setRoot("home", null);
    }
    
    @FXML private void addWord()
    {
        if(word.getText().trim().equals("") || (description.getText().trim().equals("") && translation.getText().trim().equals("")))
        {
            Window.createAlertBox("Fill out the form first!");
            return;
        }
        Parameter[] parameters = new Parameter[]{(new Parameter("word", word.getText())), (new Parameter("description", description.getText())), (new Parameter("translation", translation.getText()))};
        showLoader();
        Task<Void> task = new Task()
        {
            @Override public Void call()
            {
                ServiceRequest r = new ServiceRequest("word", HttpMethod.POST, null, parameters);
                String response;
                try
                {
                    response = r.send();
                }
                catch (DisconnectedException ex)
                {
                    Log.warn("It looks like you're disconnected!");
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox("It looks like you're disconnected!");
                        if(external)
                            ((Stage)(word.getScene().getWindow())).close();
                    });
                    return null;
                }
                if(response.equals("ok"))
                {
                    Log.info("Word added");
                    if(external)
                    {
                        Platform.runLater(() -> 
                        {
                            ol.add(new WordWithInfo(word.getText().trim(), description.getText().trim(), translation.getText().trim()));
                            ((Stage)(word.getScene().getWindow())).close();
                        });
                        return null;
                    }
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        word.clear();
                        description.clear();
                        translation.clear();
                        word.requestFocus();
                    });
                }
                else
                {
                    final String msg;
                    if(response.equals("err: duplicate"))
                        msg = "The word already exists";
                    else
                        msg = "Unknown error adding the word";
                    Log.error(msg);
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox(msg);
                        word.requestFocus();
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    void showLoader()
    {
        loader.setVisible(true);
        root.setOpacity(0.3);
        root.mouseTransparentProperty().setValue(true);
        root.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }
    
    void hideLoader()
    {
        loader.setVisible(false);
        root.setOpacity(1);
        root.mouseTransparentProperty().setValue(false);
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
    }
}
