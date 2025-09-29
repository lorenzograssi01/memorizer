package it.unipi.memorizer.app;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceRequest
{
    private static final Logger Log = LogManager.getLogger(ServiceRequest.class);
    private HttpRequest r;
    private static final String BASEURL = "http://localhost:9696";
    private static final HttpClient client = HttpClient.newHttpClient();
    
    public enum HttpMethod
    {
        GET, POST, DELETE, PATCH
    }
    
    static private String getFullUrl(String endpoint, Parameter[] parameters)
    {
        StringBuilder urlBuilder = new StringBuilder(BASEURL).append("/").append(endpoint);
        for (int i = 0; parameters != null && i < parameters.length; i++)
        {
            if (i == 0)
                urlBuilder.append("?");
            else
                urlBuilder.append("&");
            urlBuilder.append(parameters[i].toQueryString());
        }
        return urlBuilder.toString();
    }
    
    static private HttpRequest.BodyPublisher getBody(Parameter[] parameters)
    {
        JsonObject json = new JsonObject();
        for (int i = 0; parameters != null && i < parameters.length; i++)
            json.addProperty(parameters[i].name, parameters[i].value);
        Log.debug("Request Body = " + json.toString());
        return HttpRequest.BodyPublishers.ofString(json.toString());
    }
    
    static private HttpRequest.BodyPublisher getBody(List<?> array)
    {
        StringBuilder s = new StringBuilder("[");
        for (int i = 0; i < array.size(); i++)
        {
            if(i != 0)
                s.append(",");
            s.append("\"").append(array.get(i)).append("\"");
        }
        s.append("]");
        return HttpRequest.BodyPublishers.ofString(s.toString());
    }
    
    private void generateRequest(String url, HttpMethod method, HttpRequest.BodyPublisher body)
    {   
        Log.info("Generating request");
        Log.debug("Full Url = " + url);
        try
        {
            switch (method)
            {
                case GET ->
                {
                    r = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
                }
                case POST ->
                {
                    r = HttpRequest.newBuilder().uri(new URI(url)).header("Content-Type", "application/json").POST(body).build();
                }
                case PATCH ->
                {
                    r = HttpRequest.newBuilder().uri(new URI(url)).header("Content-Type", "application/json").method("PATCH", body).build();
                }
                case DELETE ->
                {
                    r = HttpRequest.newBuilder().uri(new URI(url)).DELETE().build();
                }
                default -> throw new IllegalArgumentException();
            }
        }
        catch (URISyntaxException | IllegalArgumentException ex)
        {
            Log.error(ex.getMessage());
        }
    }
    
    public ServiceRequest(String endpoint, HttpMethod method, Parameter[] parameters, List<?> array)
    {
        generateRequest(getFullUrl(endpoint, parameters), method, getBody(array));
    }
    
    public ServiceRequest(String endpoint, HttpMethod method, Parameter[] parameters, Parameter[] bodyParameters)
    {
        generateRequest(getFullUrl(endpoint, parameters), method, getBody(bodyParameters));
    }
    
    public ServiceRequest(String endpoint, HttpMethod method, Parameter[] parameters)
    {
        generateRequest(getFullUrl(endpoint, parameters), method, HttpRequest.BodyPublishers.ofString(""));
    }
    
    public ServiceRequest(String endpoint, HttpMethod method)
    {
        generateRequest(getFullUrl(endpoint, null), method, HttpRequest.BodyPublishers.ofString(""));
    }
    
    public String send() throws DisconnectedException
    {
        try
        {
            HttpResponse<String> response = client.send(r, HttpResponse.BodyHandlers.ofString());
            Log.info("Response = " + response.body());
            return response.body();
        }
        catch (IOException | InterruptedException ex)
        {
            Log.warn("Error:" + ex);
            throw new DisconnectedException("No connection");
        }
    }
}
