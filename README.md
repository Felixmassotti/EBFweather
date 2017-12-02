# Server
**Descrizione**

Il server è in ascolto sulla porta 3000.
All'avvio esegue una GET per richiedere a OpenWeatherMap il meteo corrente.
```javascript
var options = {
	url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&appid=' + appid
}
```
L'oggetto JSON viene salvato nella variabile info e rielaborato. I campi sunrise, sunset e dt sono infatti convertiti nel formato h:m:s tramite la funzione `timeConversion(unix_timestamp)` definita in serverFunctions.js.  

Il server è ora in attesa di connessioni tramite WebSocket.


