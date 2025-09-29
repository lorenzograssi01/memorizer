package it.unipi.memorizer.app;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Parameter
{
    final String name;
    final String value;
    
    public Parameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }
    
    public String toQueryString()
    {
        return URLEncoder.encode(name, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
