# Server

## Setup iniziale
Il server è basato sul framework [Node.js](https://nodejs.org/it/download/).

Per il funzionamento su Ubuntu di [RabbitMQ Server](https://www.rabbitmq.com/install-debian.html) occorre installare erlang-solutions:
```
wget https://packages.erlang-solutions.com/erlang-solutions_1.0_all.deb
sudo dpkg -i erlang-solutions_1.0_all.deb
sudo apt-get update
sudo apt-get install erlang
```
Per installare RabbitMQ Server:
```
echo 'deb http://www.rabbitmq.com/debian/ testing main' |
     sudo tee /etc/apt/sources.list.d/rabbitmq.list

wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc |
     sudo apt-key add -

sudo apt-get update
sudo apt-get install rabbitmq-server
```
Di seguito i node modules richiesti:
```

$ npm install express
$ npm install body-parser
$ npm install request
$ npm install ws
$ npm install amqp
$ npm install amqplib
```

Le funzioni ausiliarie sono definite in `serverFunctions.js`.

La **_documentazione delle API REST_** implementate è nel file [API_REST.md](API_REST.md).

## Avvio

Il server è in ascolto sulla porta 3000.
All'avvio chiama la funzione `getTodayWeather()` che esegue una GET per richiedere a OpenWeatherMap il [meteo corrente](https://openweathermap.org/current).
```javascript
var options = {
	url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&lang=it&appid=' + appid
}
```
L'oggetto JSON viene salvato nella variabile todayWeather e rielaborato. I campi sunrise, sunset e dt sono infatti convertiti nel formato HH:MM:SS tramite la funzione `timeConversion(unix_timestamp)` definita in serverFunctions.js.  

Il server si mette in attesa di connessioni tramite WebSocket: il flag wssReady inizialmente settato a false è ora true.

*N:B*: ogni 10 minuti viene richiamata la funzione `getTodayWeather()` per aggiornare il meteo corrente. Nella fase di update il server non accetta nuove connessioni WebSocket (wssReady = false).


## Autenticazione e autorizzazione tramite Oauth
```javascript
wss.on('connection', function connection(ws){
	if (wssReady) {
		if (a_t == '')
			serverFunctions.sendThroughWS(ws, 'Login first to Facebook at localhost:3000/login', 'authentication');
		ws.on('message', function incoming(message){
			console.log('received: %s', message);
			if (a_t != '' && message == 'On') {
				serverFunctions.sendThroughWS(ws, todayWeather, 'weather');
				getPhotoFromFB(ws, todayWeather.weather[0].main);
				getNextDaysWeather(ws);
			}
			else if (a_t != '' && message == 'post')
				postTodayWeatherOnFB(ws, todayWeather);
		});
	}
	ws.on('close', function(){
		console.log('Client closing connection');
		a_t = ''; // It deletes the auth code at the end of connection
	});	
	ws.on('error', function(){
		console.log('Client closing connection');
		a_t = ''; // It deletes the auth code at the end of connection
	});
});
```
Quando il client si connette tramite WebSocket il server invia un messaggio in cui chiede all'utente di autenticarsi su Facebook e di garantire l'accesso all'applicazione (questo se l'access token a_t non è stato ancora settato, come per il primo accesso all'apertura del server).

Il server quando riceve una richiesta GET all'indirizzo `localhost:3000/login` reindirizza il client su Facebook.
Ottenuto il consenso il client viene reindirizzato verso `localhost:3000/success`. Il server tramite una richiesta GET all'authorization server (Facebook) scambia così l'authorization code con l'access token, il quale viene salvato nella variabile a_t. 
```javascript
app.get('/success', function(req, res){
	console.log('code taken');
	var code = req.query.code;
	var options = { url : 'https://graph.facebook.com/v2.11/oauth/access_token?client_id=639398073115710&redirect_uri=http%3A%2F%2Flocalhost:3000%2Fsuccess&client_secret=7aa285d12c5b562e188b76431f31c2aa&code=' + code };


	request(options, function optionalCallback(err, httpResponse, body){
		if (err) {
			return console.error('upload failed:', err);
		}
		console.log('Upload successful!  Server responded with:', body);
		var info = JSON.parse(body);
		a_t = info.access_token;
		res.send('Got the token ' + a_t + '\nIt expires in ' + info.expires_in + ' seconds');
	});
});
``` 

## Gestione delle connessioni tramite WebSocket

```javascript
function sendThroughWS(ws, data, description) {
	var message = { 'data' : data, 'description' : description };
	ws.send(JSON.stringify(message));
}
```

Questa funzione si occupa di incapsulare il dato in ingresso nel campo `message.data` e di aggiungere una descrizione nel campo `message.description`, permettendo così al client di riconoscere subito il contenuto. I valori che il server può assegnare sono:
- **authentication**: per richiedere l'autenticazione su Facebook;
- **photo**: indica che il contenuto in `data` è l'URL della foto;
- **weather**: informazioni meteo di oggi;
- **forecast**: previsioni del tempo per le prossime ore di oggi;
- **post**: indica che il post è stato pubblicato sulla pagina del profilo Facebook (in `data` l'URL del profilo).

Una volta ottenuto il consenso, alla ricezione di una nuova connessione e del messaggio 'On', si eseguono tre funzioni:
* `serverFunctions.sendThroughWS(ws, todayWeather, 'weather')`
* `getPhotoFromFB(ws, todayWeather.weather[0].main)`: al suo interno sono 'innestate' tre richieste GET per ottenere l'URL della foto in base al meteo di oggi (ricerca dell'album Meteo tramite pageId, richiesta delle foto tramite albumId, ricerca dell'URL tramite photoID). La stringa salvata nella variabile photoURL è passata come parametro nella funzione `serverFunctions.sendThroughWS(ws, photoURL, 'photo')`
* `getNextDaysWeather(ws)`: esegue la richiesta GET per le previsioni dei 5 giorni successivi ma memorizza nella variabile nextHours solo le ore odierne, infine invia le informazioni al client tramite `serverFunctions.sendThroughWS(ws, nextHours, 'forecast')`

### Pubblicazione di un post su Facebook
Se il server riceve tramite WebSocket il messaggio 'post', allora invocherà la funzione `postTodayWeatherOnFB(ws, info)` che si occupa di pubblicare sul profilo un messaggio a nome dell'utente (richiesta POST) con la temperatura attuale. Se l'operazione ha successo il server invia al client un messaggio di conferma tramite WebSocket:

```javascript
serverFunctions.sendThroughWS(ws, 'Post has been published on https://www.facebook.com/me', 'post');
```

## Eliminazione dell'access token
Il server elimina l'access token quando il client chiude la connessione (a_t = ''). Un nuovo client dovrà pertanto autenticarsi.
