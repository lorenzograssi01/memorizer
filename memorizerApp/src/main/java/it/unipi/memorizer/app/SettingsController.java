package it.unipi.memorizer.app;

import it.unipi.memorizer.app.ServiceRequest.HttpMethod;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsController
{
    private static final Logger Log = LogManager.getLogger(SettingsController.class);
    @FXML private VBox root;
    @FXML private ProgressIndicator loader;
    @FXML private TextField quizLength;
    @FXML private CheckBox confirmationOnDelete;
    @FXML private CheckBox dontShowRandomWords;
    private final EventHandler<KeyEvent> keyEventHandler = event ->
    {
        if(event.getCode() == KeyCode.ENTER)
        {
            save();
        }
    };
    private final EventHandler<KeyEvent> checkboxEventHandlerConf = event ->
    {
        if (event.getCode() == KeyCode.Y)
        {
            confirmationOnDelete.setSelected(true);
            event.consume();
        }
        if (event.getCode() == KeyCode.N)
        {
            confirmationOnDelete.setSelected(false);
            event.consume();
        }
        if (event.getCode() == KeyCode.ENTER)
        {
            event.consume();
            save();
        }
    };
    private final EventHandler<KeyEvent> checkboxEventHandlerRW = event ->
    {
        if (event.getCode() == KeyCode.Y)
        {
            dontShowRandomWords.setSelected(true);
            event.consume();
        }
        if (event.getCode() == KeyCode.N)
        {
            dontShowRandomWords.setSelected(false);
            event.consume();
        }
        if (event.getCode() == KeyCode.ENTER)
        {
            event.consume();
            save();
        }
    };
    
    @FXML private void initialize()
    {
        loader.setVisible(false);
        confirmationOnDelete.addEventFilter(KeyEvent.KEY_PRESSED, checkboxEventHandlerConf);
        dontShowRandomWords.addEventFilter(KeyEvent.KEY_PRESSED, checkboxEventHandlerRW);
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        confirmationOnDelete.setSelected(Settings.getConfirmationOnDelete());
        dontShowRandomWords.setSelected(!Settings.getGetRandomWords());
        quizLength.setText(Integer.toString(Settings.getNTrainingWords()));
    }
    
    @FXML private void save()
    {
        Settings.setConfirmationOnDelete(confirmationOnDelete.isSelected());
        Settings.setGetRandomWords(!dontShowRandomWords.isSelected());
        Settings.setNTrainingWords(Integer.valueOf(quizLength.getText()));
        Settings.save();
        Window.createAlertBox("Settings saved");
        goBack();
    }
    
    @FXML private void loadData()
    {
        showLoader();
        Task<Void> task = new Task()
        {
            @Override public Void call()
            {
                ServiceRequest r = new ServiceRequest("initialize-database", HttpMethod.POST);
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
                    });
                    return null;
                }
                if(response.equals("ok"))
                {
                    Log.info("Data imported from file");
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox("Database initialized!");
                    });
                }
                else
                {
                    Log.error("Error importing data");
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox("Error initializing database");
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML private void goBack()
    {
        App.getMainWindow().setRoot("home", null);
    }
    
    void showLoader()
    {
        loader.setVisible(true);
        root.setOpacity(0.3);
        root.mouseTransparentProperty().setValue(true);
        root.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        confirmationOnDelete.removeEventFilter(KeyEvent.KEY_PRESSED, checkboxEventHandlerConf);
        dontShowRandomWords.removeEventFilter(KeyEvent.KEY_PRESSED, checkboxEventHandlerRW);
    }
    
    void hideLoader()
    {
        loader.setVisible(false);
        root.setOpacity(1);
        root.mouseTransparentProperty().setValue(false);
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        confirmationOnDelete.addEventFilter(KeyEvent.KEY_PRESSED, checkboxEventHandlerConf);
        dontShowRandomWords.addEventFilter(KeyEvent.KEY_PRESSED, checkboxEventHandlerRW);
    }
}
