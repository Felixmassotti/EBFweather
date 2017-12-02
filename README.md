# Server
### Avvio ###

Il server è in ascolto sulla porta 3000.
All'avvio esegue una GET per richiedere a OpenWeatherMap il meteo corrente.
```javascript
var options = {
	url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&appid=' + appid
}
```
L'oggetto JSON viene salvato nella variabile info e rielaborato. I campi sunrise, sunset e dt sono infatti convertiti nel formato HH:MM:SS tramite la funzione `timeConversion(unix_timestamp)` definita in serverFunctions.js.  

Il server è ora in attesa di connessioni tramite WebSocket.


### Ricezione di connessioni ###
```javascipt
wss.on('connection', function connection(ws) {
			ws.on('message', function incoming(message) {
				console.log('received: %s', message);
			});
			if (a_t == '')
				serverFunctions.sendThroughWS(ws, 'Login first to Facebook at localhost:3000/login', 'authentication');
			else {
				getPhotoFromFB(ws, main);
				serverFunctions.sendThroughWS(ws, info, 'weather');
				getNextDaysWeather();
			}	
		});
```
Quando il primo client si connette tramite WebSocket il server invia un messaggio in cui chiede all'utente di autenticarsi su Facebook e di garantire l'accesso all'applicazione (questo se l'access_token a_t non è stato ancora settato).

