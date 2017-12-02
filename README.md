# Server
**Descrizione**

Il server è in ascolto sulla porta 3000.
All'avvio esegue una GET per richiedere a OpenWeatherMap il meteo corrente.
```javascript
var options = {
	url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&appid=' + appid
}
```
