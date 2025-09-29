package it.unipi.memorizer.app;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class OpenWordController
{
    @FXML private Text word;
    @FXML private Text description;
    @FXML private Text translation;
    
    public void setWord(Word w)
    {
        word.setText(w.getWord());
        description.setText(w.getDescription());
        translation.setText(w.getTranslation());
    }
}
