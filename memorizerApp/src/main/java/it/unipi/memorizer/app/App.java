package it.unipi.memorizer.app;

import java.io.IOException;
import java.util.function.Consumer;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;

public class App extends Application
{
    private static final Logger Log = LogManager.getLogger(App.class);
    private static Window mainWindow;
    
    public static Window getMainWindow()
    {
        return mainWindow;
    }
    
    public static Parent loadFXML(String fxml, Consumer<FXMLLoader> onLoaderReady)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource(fxml + ".fxml"));
            Parent root = fxmlLoader.load();
            if (onLoaderReady != null)
            {
                onLoaderReady.accept(fxmlLoader);
            }
            return root;
        }
        catch(IOException e)
        {
            Log.fatal("Can't load FXML " + fxml);
            App.getMainWindow().getStage().close();
            return null;
        }
    }

    @Override
    public void start(Stage stage)
    {
        mainWindow = new Window(stage, "home", 720, 480, "Memorizer", null, false);
        mainWindow.setMain();
    }
    
    public static void main(String[] args)
    {
        Log.debug("Launching application");
        Settings.initialize();
        launch();
    }
    
    static void loaderOnLoad(FXMLLoader loader, Consumer<Object> onControllerReady)
    {
        Object controller = loader.getController();
        if (controller != null && onControllerReady != null)
        {
            onControllerReady.accept(controller);
        }
    }
}
