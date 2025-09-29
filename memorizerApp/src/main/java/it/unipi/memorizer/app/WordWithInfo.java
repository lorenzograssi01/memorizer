package it.unipi.memorizer.app;

import java.util.Objects;

public class WordWithInfo extends Word
{
    private int quizzed;
    private int guessed;

    public int getQuizzed()
    {
        return quizzed;
    }

    public void setQuizzed(int quizzed)
    {
        this.quizzed = quizzed;
    }
    
    public double getConfidence()
    {
        return guessed/((double)quizzed + 1.5);
    }

    public int getGuessed()
    {
        return guessed;
    }

    public void setGuessed(int guessed)
    {
        this.guessed = guessed;
    }

    public WordWithInfo(String word, String description, String translation)
    {
        super(word, description, translation);
        this.quizzed = 0;
        this.guessed = 0;
    }

    public WordWithInfo(String word, String description, String translation, int quizzed, int guessed)
    {
        super(word, description, translation);
        this.quizzed = quizzed;
        this.guessed = guessed;
    }
    
    public WordWithInfo()
    {
        super();
    }
    
    @Override
    public boolean equals(Object e2)
    {
        if(e2 == null || (e2.getClass() != WordWithInfo.class && e2.getClass() != Word.class))
            return false;
        return ((Word)e2).word.equals(this.word);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.word);
        return hash;
    }
}
