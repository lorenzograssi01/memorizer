package it.unipi.memorizer.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest
{
    volatile boolean ok;
    @Test
    public void testMain()
    {
        System.out.println("test Main");
        App a = new App();

        Thread t = new Thread(() ->
        {
            try
            {
                ok = true;
                App.main(null);
            }
            catch (Exception e)
            {
                ok = false;
            }
        });
        t.start();
        try
        {
            t.join(1500);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        }
        if (!ok)
        {
            fail("The application doesn't start correctly");
        }
    }
}
