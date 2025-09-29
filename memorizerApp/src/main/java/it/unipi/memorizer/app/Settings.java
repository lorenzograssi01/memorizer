package it.unipi.memorizer.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings
{
    private static final Logger Log = LogManager.getLogger(Settings.class);
    private static Settings s = new Settings();
    public int nTrainingWords;
    public boolean confirmationOnDelete;
    public boolean getRandomWords;
    private static final String FILE_PATH = "settings.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static void setNTrainingWords(int nTrainingWords)
    {
        s.nTrainingWords = nTrainingWords;
    }
    
    public static void setConfirmationOnDelete(boolean confirmationOnDelete)
    {
        s.confirmationOnDelete = confirmationOnDelete;
    }
    
    public static void setGetRandomWords(boolean getRandomWords)
    {
        s.getRandomWords = getRandomWords;
    }
    
    public static int getNTrainingWords()
    {
        return s.nTrainingWords;
    }
    
    public static boolean getConfirmationOnDelete()
    {
        return s.confirmationOnDelete;
    }
    
    public static boolean getGetRandomWords()
    {
        return s.getRandomWords;
    }
    
    private Settings()
    {
        
    }

    public static void initialize()
    {
        try (FileReader reader = new FileReader(FILE_PATH))
        {
            s = gson.fromJson(reader, Settings.class);
            Log.info("Read settings from file");
        }
        catch (IOException e)
        {
            Log.warn("Error reading settings from file: " + e.getMessage());
            s.nTrainingWords = 10;
            s.confirmationOnDelete = true;
            s.getRandomWords = false;
            save();
        }
    }

    public static void save()
    {
        try (FileWriter writer = new FileWriter(FILE_PATH))
        {
            gson.toJson(s, writer);
            Log.info("Wrote settings to file");
        }
        catch (IOException e)
        {
            Log.warn("Error saving settings to file: " + e.getMessage());
        }
    }
}
