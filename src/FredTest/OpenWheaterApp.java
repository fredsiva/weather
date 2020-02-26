package FredTest;



import lejos.hardware.Button;
import lejos.hardware.lcd.*;
import lejos.utility.Delay;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class OpenWheaterApp {

	String URLSTRING_DEMO_TEMP = "https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22";

	String MyAPIKey = "3107464ed0mshfdc571c8007b6d3p19e875jsn28cfec578eba";
	String URLSTRING_TEMP = "https://community-open-weather-map.p.rapidapi.com/weather?id=2797714&units=metric&mode=xml&q=Namur,Belgium";

    URL url;
    URLConnection urlConnection = null;
    HttpURLConnection connection = null;
    BufferedReader in = null;
    String urlContent = "";

    public String getWebServerAnswer(String urlString) throws IOException, IllegalArgumentException {
        String urlStringCont = "";
    
        // creating URL object
        url = new URL(urlString);
        urlConnection = url.openConnection();
        connection = null;

        // we can check, if connection is proper type
        if (urlConnection instanceof HttpURLConnection) {
            connection = (HttpURLConnection) urlConnection;
            connection.setRequestProperty("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com");
            connection.setRequestProperty("x-rapidapi-key", MyAPIKey);
            
        } else {
            System.out.println("Please enter an HTTP URL");
            throw new IOException("HTTP URL is not correct");
        }

        LCD.drawString("Wait Server...", 2, 3);
    
        // we can check response code (200 OK is expected)
        // System.out.println("Response Code=" + connection.getResponseCode() + ", MSG=" + connection.getResponseMessage());
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        LCD.drawString("Analyze...    ", 2, 3);
        
        String current;

        while ((current = in.readLine()) != null) {
            urlStringCont += current;
        }
        
        in.close();
        
        return urlStringCont;
    }

    
    public static void main(String[] args) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        float tempF;
        
        LCD.drawString("Hello Fred", 2, 1);
		LCD.drawString("Wheater Test", 2, 2);
  	  	LCD.drawString("Prep Req...", 2, 3);

		OpenWheaterApp demo = new OpenWheaterApp();
        String xmlResponseS = demo.getWebServerAnswer(demo.URLSTRING_TEMP);
        // System.out.println("Response = " + xmlResponseS);

        try 
        {
          final DocumentBuilder builder = factory.newDocumentBuilder();
    	  final Document document= builder.parse(new InputSource(new StringReader(xmlResponseS)));

    	  final Element racine = document.getDocumentElement();
    	  final Element city = (Element) racine.getElementsByTagName("city").item(0);
    	  final Element temp = (Element) racine.getElementsByTagName("temperature").item(0);
    	  
    	  // System.out.println("City : " + city.getAttribute("name"));
    	  // System.out.println("Temp : " + temp.getAttribute("value"));

    	  tempF = Float.parseFloat(temp.getAttribute("value"));

    	  LCD.drawString("City=" + city.getAttribute("name"), 2, 3);
    	  LCD.drawString("Temp=" + tempF, 2, 4);
    	  LCD.drawString("m=" + temp.getAttribute("min") + ",M=" + temp.getAttribute("max"), 2, 5);
    	
    	  
        } catch (Exception e) {
          e.printStackTrace();
        }		
        
		while (Button.ESCAPE.isUp()) {
			Delay.msDelay(100);
		}

    }
}

