
public class Meteo {
	//Weather informations
	private String time; 
	private String clouds; 
	private String description;
	private String humidity;
	private String pressure;
	private String temp;
	private String wind;
	
	//Get and Set methods
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getClouds() {
		return clouds;
	}
	public void setClouds(String clouds) {
		this.clouds = clouds;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getPressure() {
		return pressure;
	}
	public void setPressure(String pressure) {
		this.pressure = pressure;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public void stampa(Grafica g) {
		g.setTime(time);
		g.setClouds(clouds);
		g.setDescription(description);
		g.setHumidity(humidity);
		g.setPressure(pressure);
		g.setTemp(temp);
		g.setWind(wind);
	}
}
