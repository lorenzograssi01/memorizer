package it.unipi.memorizer.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="Words")
public class Word extends WordBase
{
    public Word(String word, String description, String translation)
    {
        super(word, description, translation);
    }
    
    public Word()
    {
        
    }
}
