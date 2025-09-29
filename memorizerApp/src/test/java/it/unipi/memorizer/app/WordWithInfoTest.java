package it.unipi.memorizer.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WordWithInfoTest
{
    @Test
    public void testEquals()
    {
        System.out.println("test WordWithInfo.equals");
        Word e2 = new Word("TEST", "1", "2");
        Word e3 = new Word("AA", "3", "4");
        WordWithInfo instance = new WordWithInfo("TEST", "3", "4");
        if (!instance.equals(e2) || instance.equals(e3))
        {
            fail("error comparing words");
        }
    }
}
