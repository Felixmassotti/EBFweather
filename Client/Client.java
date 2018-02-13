import java.awt.HeadlessException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.websocket.*;
import org.json.*;

@ClientEndpoint
public class Client {
	private Session session=null;	//WebSocket
	private int i=0;	//Counts the hour where I am 
	private LinkedList<Meteo> meteoList;
	private Meteo today;
	private final Grafica g;
	private final Listener listener;
	
	public Client(){
		g=new Grafica();
		listener=new Listener(this,g);
		g.addListener(listener);
		meteoList=new LinkedList<Meteo>();
	}
	
	//When client receives a message from WebSocketServer
	@OnMessage
	public void onMessage(String messaggio) throws JSONException, IndexOutOfBoundsException, HeadlessException, MalformedURLException{
		JSONObject mess=new JSONObject(messaggio);
		
		// Authentication message case
		if(mess.getString("description").equals("authentication")){
			JSONObject messType1=new JSONObject(messaggio);
			String s=messType1.getString("data");
			JOptionPane.showMessageDialog(null,s);
			g.setOn(true);
			g.setOFF(false);
			g.setSucc(false);
			g.setPrec(false);
			try{
				session.close();
			}catch(Exception e){JOptionPane.showMessageDialog(null,"Error: server is disconnected");}
		}
		
		// Photo message case
		else if(mess.getString("description").equals("photo")){	 
			JSONObject messType2=new JSONObject(messaggio);
			String url=messType2.getString("data");	
			g.setOut(url);		
		}
		// Weather message case
		else if(mess.getString("description").equals("weather")){  
			JSONObject messType3=new JSONObject(messaggio);
			JSONObject data=messType3.getJSONObject("data");
			JsonParse(data);
			today=meteoList.get(listener.getMax());
			meteoList.get(listener.getMax()).stampa(g);	
		}
		
		// Forecast message case
		else if(mess.getString("description").equals("forecast")){
			JSONObject messType4=new JSONObject(messaggio);
			JSONArray data=messType4.getJSONArray("data");
			int iTmp;
		    for(iTmp=0; iTmp<data.length(); iTmp++){
				JsonParse(data.get(iTmp));
		    }
		    g.setSucc(true);
		    g.setShare(true);
		    listener.setMax(meteoList.size());
		}
			
		// Post message case
		else if(mess.getString("description").equals("post")){
			JSONObject messType5=new JSONObject(messaggio);
			String data=messType5.getString("data");
			JOptionPane.showMessageDialog(null,data);
		}
	}
	
	//After the pression of button ON, the client will both connect to WebSocketServer
	public void on(){
		try{
			//WebSocket
		    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			session= container.connectToServer(this, new URI("ws://localhost:3000"));
			session.getAsyncRemote().sendText("On");
			
		}catch(Exception e){
			JOptionPane.showMessageDialog(null,"Cannot connect to WebSocketServer");
			g.setOn(true);
			g.setOFF(false);
			g.setSucc(false);
			g.setPrec(false);
			g.setShare(false);
		}
	}
	
	protected void JsonParse(Object data) throws JSONException{
		Meteo meteoTmp=new Meteo();
		
		//time
		String time=((JSONObject) data).getString("dt");
		meteoTmp.setTime(time);
		
		//clouds
		JSONObject clouds =((JSONObject) data).getJSONObject("clouds");
		String cloudsS=clouds.getString("all");
		meteoTmp.setClouds(cloudsS);
		
		//description
		JSONArray weatherA=((JSONObject) data).getJSONArray("weather");
		JSONObject weather=(JSONObject) weatherA.get(0);
		String description=weather.getString("description");
		meteoTmp.setDescription(description);
		
		//humidity
		JSONObject main =((JSONObject) data).getJSONObject("main");
		String humidity=main.getString("humidity");
		meteoTmp.setHumidity(humidity);
		
		//pressure
		String pressure=main.getString("pressure");
		meteoTmp.setPressure(pressure);
		
		//temperature
		String temp=main.getString("temp");
		meteoTmp.setTemp(temp);
		
		//wind
		JSONObject wind =((JSONObject) data).getJSONObject("wind");
		String windS=wind.getString("speed");
		meteoTmp.setWind(windS);
		
		meteoList.add(meteoTmp);		
	}

	public void off(){
		try{
			session.close();
			System.exit(0);
		}catch(Exception e){JOptionPane.showMessageDialog(null,"Error: server is disconnected");}
	}
	
	//Commands functions to slide the informations about next hours weather forecast
	public void precedente(){
		try{
			i--;
			if(i==0){
				today.stampa(g);
			}
			else{
				meteoList.get(i).stampa(g);
			}
		}catch(Exception e){JOptionPane.showMessageDialog(null,"Error in Prec");}
	}
	
	public void successivo(){
		try{
			i++;
			meteoList.get(i).stampa(g);
		}catch(Exception e){JOptionPane.showMessageDialog(null,"Error in Succ");}
	}

	public void share() {
		try{
			session.getAsyncRemote().sendText("post");
		}catch(Exception e){JOptionPane.showMessageDialog(null,"Error in Share");}
	}
}
