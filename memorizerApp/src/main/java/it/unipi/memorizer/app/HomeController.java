package it.unipi.memorizer.app;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeController
{
    private static final Logger Log = LogManager.getLogger(HomeController.class);
    @FXML private VBox root;
    @FXML private ProgressIndicator loader;
    private final Gson gson = new Gson();
    private final EventHandler<KeyEvent> keyEventHandler = event ->
    {
        if (event.getCode() == KeyCode.ENTER)
        {
            showMemorize();
        }
        if (event.getCode() == KeyCode.PLUS)
        {
            showAddWords();
        }
        if (event.getCode() == KeyCode.S)
        {
            showSettings();
        }
        if (event.getCode() == KeyCode.L)
        {
            showWordList();
        }
    };
    
    @FXML private void initialize()
    {
        loader.setVisible(false);
        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        Platform.runLater(() -> root.requestFocus());
        Task<Void> task = new Task()
        {
            @Override public Void call()
            {
                ServiceRequest r = new ServiceRequest("test", ServiceRequest.HttpMethod.GET);
                try
                {
                    r.send();
                }
                catch (DisconnectedException ex)
                {
                    Log.warn("It looks like you're disconnected!");
                    Platform.runLater(() -> 
                    {
                        Window.createAlertBox("It looks like you're disconnected!");
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML private void showWordList()
    {
        showLoader();
        Task<Void> task = new Task()
        {
            @Override public Void call()
            {
                ServiceRequest r = new ServiceRequest("words", ServiceRequest.HttpMethod.GET);
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
                WordWithInfo[] words;
                try
                {
                    words = gson.fromJson(response, WordWithInfo[].class);
                    Log.debug("Response unserialized");
                }
                catch (JsonSyntaxException e)
                {
                    Log.error(e.getMessage());
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox("Error decoding the server request");
                    });
                    return null;
                }
                Platform.runLater(() -> 
                {
                    App.getMainWindow().setRoot("wordList", controller -> 
                    {
                        ((WordListController)controller).setWordList(words);
                    });
                });
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML private void showAddWords()
    {
        App.getMainWindow().setRoot("addWord", null);
    }
    
    @FXML private void showMemorize()
    {
        showLoader();
        Task<Void> task = new Task()
        {
            @Override public Void call()
            {
                ServiceRequest r = new ServiceRequest("training-data", ServiceRequest.HttpMethod.GET, new Parameter[]{new Parameter("n", Integer.toString(Settings.getNTrainingWords())), new Parameter("random", Boolean.toString(Settings.getGetRandomWords()))});
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
                LinkedList<Word> words;
                try
                {
                    words = gson.fromJson(response, new TypeToken<LinkedList<Word>>(){}.getType());
                    Log.debug("Response unserialized");
                }
                catch (JsonSyntaxException e)
                {
                    Log.error(e.getMessage());
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox("Error decoding the server request");
                    });
                    return null;
                }
                if(!words.isEmpty())
                {
                    if(!Settings.getGetRandomWords())
                        Collections.shuffle(words);
                    Platform.runLater(() -> 
                    {
                        App.getMainWindow().setRoot("memorize", controller -> 
                        {
                            ((MemorizeController)controller).setWordList(words);
                        });
                        if(words.size() < Settings.getNTrainingWords())
                        {
                            Window.createAlertBox("There are only " + words.size() + " words\nThe quiz will be shorter");
                        }
                    });
                }
                else
                {
                    Log.warn("There are no words");
                    Platform.runLater(() -> 
                    {
                        hideLoader();
                        Window.createAlertBox("You have to add some words first");
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @FXML private void showSettings()
    {
        App.getMainWindow().setRoot("settings", null);
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
