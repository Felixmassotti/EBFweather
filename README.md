# Server
## Avvio ##

Il server è in ascolto sulla porta 3000.
All'avvio esegue una GET per richiedere a OpenWeatherMap il [meteo corrente.](https://openweathermap.org/current)
```javascript
var options = {
	url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&appid=' + appid
}
```
L'oggetto JSON viene salvato nella variabile info e rielaborato. I campi sunrise, sunset e dt sono infatti convertiti nel formato HH:MM:SS tramite la funzione `timeConversion(unix_timestamp)` definita in serverFunctions.js.  

Il server è ora in attesa di connessioni tramite WebSocket.


## Autenticazione e autorizzazione tramite Oauth ##
```javascript
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

Il server quando riceve una richiesta GET all'indirizzo `localhost:3000/login` reindirizza il client su Facebook.
Ottenuto il consenso il client viene reindirizzato verso `localhost:3000/success`. Il server tramite una richiesta GET all'authorization server (Facebook) scambia così il code con l'access token, il quale viene salvato nella variabile a_t. Un timeout è avviato in questo momento tramite la funzione `a_tTimeout(a_t, expires_in)` affinché l'access token sia risettato al valore '' al termine del periodo di validità (circa 60 giorni). Qui [ulteriori dettagli.](https://developers.facebook.com/docs/facebook-login/access-tokens/expiration-and-extension) 
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
		var expires_in = info.expires_in; // access_token's validity time
		res.send('Got the token ' + a_t + '\nIt expires in ' + expires_in + ' seconds');
		serverFunctions.a_tTimeout(a_t, expires_in); // value in a_t will be deleted at the end of this timeout
	});
});
``` 

## Gestione delle connessioni tramite WebSocket ## 
Una volta ottenuto il consenso, alla ricezione di un nuova connessione, viene eseguita la funzione `getPhotoFromFB(ws, description)`. Al suo interno sono 'innestate' tre richieste GET per ottenere l'URL della foto in base al meteo di oggi. La stringa salvata nella variabile photoURL è passata come parametro nella funzione `serverFunctions.sendThroughWS(ws, photoURL, 'photo')`:

```javascript
function sendThroughWS(ws, data, description) {
	var message = {'data' : data, 'description' : description };
	ws.send(JSON.stringify(message));
}
```

Questa si occupa di incapsulare il dato in ingresso nel campo `message.data` e di aggiungere una descrizione nel campo `message.description`, permettendo così al client di riconoscere subito il contenuto. I valori che il server può assegnare sono:
- authentication: per richiedere l'autenticazione su Facebook;
- photo: indica che il contenuto in `data` è l'URL della foto;
- weather: informazioni meteo di oggi.

## Gestione della coda tramite RabbitMQ ##
Ad ogni connessione in ingresso il server esegue anche la funzione `getNextDaysWeather()`.
```javascript
function getNextDaysWeather() {
	var options = {
		url : 'http://api.openweathermap.org/data/2.5/forecast?id=' + id + '&units=metric&appid=' + appid
	}
	function callback(error, response, body) {
		if (!error && response.statusCode == 200) {
			var info = JSON.parse(body);
			var list = info.list;
			var j = 0;
			for (var i=0; i<list.length; i++) {
				var time = serverFunctions.timeConversion(list[i].dt);
				if (time == '7:00:00') {
					nextDays[j] = list[i];
					nextDays[j].dt = time;
					j++;
				}
			}
			console.log('GET requests to OpenWeatherMap completed: acquired all infos about today and next days weather forecast');
			setTimeout(function(){
				serverFunctions.sendMsgToExchange(nextDays);
			}, 2000);  // this delays the message's publishing
		}
		else
			console.log(response.statusCode);
	}
	request(options, callback);
}
```
OpenWeatherMap risponde alla richiesta GET con il [meteo dei 4-5 giorni successivi](https://openweathermap.org/forecast5) calcolati ogni 3 ore. Nella variabile Array `nextDays` sono salvate solo le previsioni dei prossimi giorni alle ore 7:00:00. Dopo un intervallo di 2 secondi è invocata la funzione `serverFunctions.sendMsgToExchange(nextDays)`.

```javascript
function sendMsgToExchange(data) {
	amqp.connect('amqp://localhost', function(err, conn) {
		conn.createChannel(function(err, ch) {
			var ex = 'weather_exchange';
			var msg = JSON.stringify(data);
			ch.assertExchange(ex, 'fanout', {durable: false});
			ch.publish(ex, '', new Buffer(msg));
			console.log("The message containing weather forecast has been sent to the exchange");
		});
	});
}
```
Si utilizza lo scambio di messaggi basato sul protocollo AMQP: il server crea o si assicura che esista un exchange di nome 'weather_exchange' e di tipo 'fanout', su cui andrà a pubblicare il messaggio contenente le previsioni dei giorni seguenti.
È stato scelto il meccanismo del Publish/Subscribe con coda temporanea ({durable: false}) affinché ogni client abbia a disposizione una propria coda vuota quando si connette. Nella documentazione del client si vedrà che, una volta ricevuto nextDays, il client eseguirà l'unbinding della coda: in questo modo non riceverà nuovamente il messaggio nel caso in cui si connettesse un altro client, essendo la coda di tipo 'fanout'.
Alla terminazione del client la coda associata sarà eliminata, non possibile invece con una named queue.
