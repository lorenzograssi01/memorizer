package it.unipi.memorizer.service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="Words")
public class WordWithInfo extends WordBase
{
    @Column(name="quizzed")
    private int quizzed;
    @Column(name="guessed")
    private int guessed;

    public int getQuizzed()
    {
        return quizzed;
    }

    public void setQuizzed(int quizzed)
    {
        this.quizzed = quizzed;
    }

    public int getGuessed()
    {
        return guessed;
    }

    public void setGuessed(int guessed)
    {
        this.guessed = guessed;
    }

    public WordWithInfo(String word, String description, String translation, int quizzed, int guessed)
    {
        super(word, description, translation);
        this.quizzed = quizzed;
        this.guessed = guessed;
    }
    
    public WordWithInfo()
    {
        
    }
}
