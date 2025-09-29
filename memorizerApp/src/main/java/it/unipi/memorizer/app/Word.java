package it.unipi.memorizer.app;

import java.util.Objects;

public class Word
{
    protected String word;
    protected String description;
    protected String translation;
    
    public Word()
    {
        
    }
    
    public Word(String word, String description, String translation)
    {
        this.word = word;
        this.description = description;
        this.translation = translation;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTranslation()
    {
        return translation;
    }

    public void setTranslation(String translation)
    {
        this.translation = translation;
    }
    
    @Override
    public boolean equals(Object e2)
    {
        if(e2 == null || e2.getClass() != Word.class)
            return false;
        return ((Word)e2).word.equals(word);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.word);
        return hash;
    }
    
    public WordWithInfo toWordWithInfo()
    {
        return new WordWithInfo(word, description, translation, 0, 0);
    }
}
