package it.unipi.memorizer.app;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.LinkedList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class MemorizeController
{
    private static final Logger Log = LogManager.getLogger(MemorizeController.class);
    @FXML private VBox root;
    @FXML private Text description;
    @FXML private Text translation;
    @FXML private Text feedback;
    @FXML private TextField word;
    @FXML private Button guessBtn;
    private LinkedList<Word> words;
    private final LinkedList<String> correctGuesses = new LinkedList<>();
    private final LinkedList<String> wrongGuesses = new LinkedList<>();
    
    public void setWordList(LinkedList<Word> words)
    {
        this.words = words;
        showNextWord();
    }
    
    @FXML private void leave()
    {
        endQuiz();
        goBack();
    }
    
    @FXML private void goBack()
    {
        App.getMainWindow().setRoot("home", null);
    }
    
    @FXML private void initialize()
    {
        root.addEventHandler(KeyEvent.KEY_PRESSED, event ->
        {
            if(event.getCode() == KeyCode.ENTER)
            {
                guess();
            }
        });
    }

    @FXML private void guess()
    {
        String userGuess = word.getText().trim();
        Word currentWord = words.removeFirst();
        int sleepTime;

        if (userGuess.trim().equalsIgnoreCase(currentWord.getWord()))
        {
            correctGuesses.add(currentWord.getWord());
            feedback.setText("Correct!");
            feedback.setStyle("-fx-fill: #009900; -fx-font-size: 16px;");
            sleepTime = 1000;
            Log.info("Correct guess: " + currentWord.getWord());
        }
        else                                   
        {
            wrongGuesses.add(currentWord.getWord());
            feedback.setText("Incorrect! The correct word was: " + currentWord.getWord());
            feedback.setStyle("-fx-fill: #ee0000; -fx-font-size: 16px;");
            sleepTime = 2700;
            Log.info("Incorrect guess. User guessed: " + userGuess + ", Correct: " + currentWord.getWord());
        }
        guessBtn.setDisable(true);
        description.requestFocus();
        
        Task task = new Task<Void>()
        {
            @Override public Void call()
            {
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch(InterruptedException e)
                {
                    Log.error("Thread interrupted");
                }
                Platform.runLater(() -> 
                {
                    if (!words.isEmpty())
                    {
                        showNextWord();
                        guessBtn.setDisable(false);
                    }
                    else
                    {
                        String msg = "You guessed " + correctGuesses.size() + " out of " + (correctGuesses.size() + wrongGuesses.size() + " words correctly");
                        msg += "\nYou got a" + getGrade(correctGuesses.size(), (correctGuesses.size() + wrongGuesses.size()));
                        Window.createAlertBox(msg);
                        leave();
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private static String getGrade(int guessed, int quizzed)
    {
        double rate = guessed/(double)quizzed;
        if(quizzed < 10)
        {
            if(rate > .9)
                return "n A";
            if(rate > .75)
                return " B";
            if(rate > .6)
                return " C";
            if(rate > .5)
                return " D";
            return "n F";
        }
        else
        {
            if(quizzed == guessed)
                return "n A+";
            if(rate > .933)
                return "n A";
            if(rate > .9)
                return "n A-";
            if(rate > .85)
                return " B+";
            if(rate > .8)
                return " B";
            if(rate > .75)
                return " B-";
            if(rate > .7)
                return " C+";
            if(rate > .65)
                return " C";
            if(rate > .6)
                return " C-";
            if(rate > .567)
                return " D+";
            if(rate > .533)
                return " D";
            if(rate > .5)
                return " D-";
            return "n F";
        }
    }

    private void showNextWord()
    {
        word.clear();
        Word currentWord = words.getFirst();
        description.setText(currentWord.getDescription());
        translation.setText(currentWord.getTranslation());
        word.requestFocus();
        feedback.setText("");
    }
    
    private void endQuiz()
    {
        Task task = new Task<Void>()
        {
            @Override public Void call()
            {
                try
                {
                    if(!correctGuesses.isEmpty())
                        new ServiceRequest("quiz-result", ServiceRequest.HttpMethod.POST, new Parameter[]{new Parameter("guessed", "true")}, correctGuesses).send();
                    if(!wrongGuesses.isEmpty())
                        new ServiceRequest("quiz-result", ServiceRequest.HttpMethod.POST, new Parameter[]{new Parameter("guessed", "false")}, wrongGuesses).send();
                    Log.info("Saved quiz data");
                }
                catch (DisconnectedException e)
                {
                    Log.warn("It looks like you're disconnected!");
                    Platform.runLater(() ->
                    {
                        Window.createAlertBox("Your quiz data wasn't saved!\nIt looks like you're disconnected!");
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
