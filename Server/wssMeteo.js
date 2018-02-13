const id = '3169070' // id of Rome
const appid = 'b011edc116685d89dfdc35517d9ce205' // our API key 

const request = require('request');
const http = require('http');
const url = require('url');
const fs = require('fs');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: false }));
const WebSocket = require('ws');

const serverFunctions = require('./serverFunctions');

/*
Weather.main can be:
'Drizzle';
'Thunderstorm';
'Rain';
'Snow';
'Mist';
'Clear';
'Clouds';
'Extreme';
'Additional';
More details on ---> https://openweathermap.org/weather-conditions
*/

var a_t = ''; // access_token
var todayWeather;
var nextHours = new Array(); // next hours weather forecast
const pageId = '1839290216363075'; // pageId of 'Meteoretidicalcolatori1718'
var filename = 'welcome.html'; // welcome HTML page

var wssReady = false; // this will be set true after first GET request has completed

/* Upgrade from HTTP protocol to WebSocket */
const server = http.createServer(app);
const wss = new WebSocket.Server({server});

server.listen(3000, function listening(){
	console.log('Listening on %d', server.address().port);
});

/* GET request to OpenWeatherMap, manage response, save it in todayWeather, set wssReady */ 
getTodayWeather();

/* Refresh todayWeather repeatedly after 10 minutes*/
setInterval(function(){
	wssReady = false;
	getTodayWeather();
	serverFunctions.sendMsgToQueue(serverFunctions.getTime() + ': update has completed');
}, 600000);
		
wss.on('connection', function connection(ws){
	if (wssReady) {
		if (a_t == '')
			serverFunctions.sendThroughWS(ws, 'Login first to Facebook at localhost:3000/login', 'authentication');
		ws.on('message', function incoming(message){
			if (a_t != '' && message == 'On') {
				serverFunctions.sendMsgToQueue(serverFunctions.getTime() + ': received new connection');
				serverFunctions.sendThroughWS(ws, todayWeather, 'weather');
				getPhotoFromFB(ws, todayWeather.weather[0].main);
				getNextDaysWeather(ws);
			}
			else if (a_t != '' && message == 'post') {
				postTodayWeatherOnFB(ws, todayWeather);
			}
		});
	}
	ws.on('close', function(){
		a_t = ''; // It deletes the auth code at the end of connection
		serverFunctions.sendMsgToQueue(serverFunctions.getTime() + ': client disconnected');
	});	
	ws.on('error', function(){
		a_t = ''; // It deletes the auth code at the end of connection
		serverFunctions.sendMsgToQueue(serverFunctions.getTime() + ': client disconnected');
	});
});

app.get('/login', function(req, res){
	res.redirect('https://www.facebook.com/v2.11/dialog/oauth?client_id=639398073115710&redirect_uri=http%3A%2F%2Flocalhost:3000%2Fsuccess&scope=public_profile,pages_show_list,user_posts,publish_actions,manage_pages');
});

app.get('/success', function(req, res){
	var code = req.query.code;
	var options = { url : 'https://graph.facebook.com/v2.11/oauth/access_token?client_id=639398073115710&redirect_uri=http%3A%2F%2Flocalhost:3000%2Fsuccess&client_secret=7aa285d12c5b562e188b76431f31c2aa&code=' + code };

	request(options, function optionalCallback(err, httpResponse, body){
		if (err) {
			return console.error('upload failed:', err);
		}
		a_t = JSON.parse(body).access_token;
		serverFunctions.sendMsgToQueue(serverFunctions.getTime() + ': got access token');
		fs.readFile(filename, function(err, data) {
			if (err) {
				res.writeHead(404, {'Content-Type': 'text/html'});
				return res.end("404 Not Found");
			}
			res.writeHead(200, {'Content-Type': 'text/html'});
			res.write(data);
			return res.end();
		});
	});
});

/* Handling 404 errors */
app.get('*', function(req, res){
	res.status(404).send('This is not the web page you are looking for\n');
});

/* Server calls this function in order to post today's weather on your Facebook profile */
function postTodayWeatherOnFB(ws, info){
	var time = serverFunctions.getTime().toString();

	var postFB = 'Orario pubblicazione: ' + time + '\nAlle ore ' + info.dt + ' a ' + info.name + ' si registra una temperatura di ' + info.main.temp + '°C.';
	var options = {
		url : 'https://graph.facebook.com/v2.11/me/feed',
		qs : { message : postFB, access_token : a_t }
	}
	request.post(options, function callback(error, response, body){
		if (!error && response.statusCode == 200) {
			serverFunctions.sendThroughWS(ws, 'Post has been published on https://www.facebook.com/me', 'post');
			serverFunctions.sendMsgToQueue(serverFunctions.getTime() + ': post has been published on https://www.facebook.com/me');
		}
		else
			console.log(error);	
	});
}

/* Server calls this function in order to get the photo's URL according to today's weather */
function getPhotoFromFB(ws, description){
	/* GET request to take albumId of Meteo */
	var options = {
		url : 'https://graph.facebook.com/v2.11/' + pageId + '/albums',
		qs : { access_token : a_t }
	}
	request(options, function callback(error, response, body){
		if (!error && response.statusCode == 200) {
			var info = JSON.parse(body);
			var albumId = '';
			var albums = info.data;
			for (var i=0; i<albums.length; i++) {
				if (albums[i].name == 'Meteo') {
					albumId = albums[i].id;
					break;
				}
			}
			
			/* GET request to take photos of Meteo */
			var options = {
				url : 'https://graph.facebook.com/v2.11/' + albumId + '/photos',
				qs : {access_token : a_t}    
			}
			request(options, function(error, response, body){
				if (!error && response.statusCode == 200){
					var info = JSON.parse(body);
					
					/* Get the photoId according to today's weather */
					var photoId = '';
					var photos = info.data;
					for (var i=0; i<photos.length; i++){
						if (photos[i].name == description){
							photoId = photos[i].id;
							break;
						}
					}
                    
					/* GET request to take photoURL */        
					var options = {
						url : 'https://graph.facebook.com/v2.11/' + photoId + '?fields=images',
						qs : {access_token  : a_t }    
					}
					request(options, function(error, response, body){
						if (!error && response.statusCode == 200){
							var info = JSON.parse(body);
							var photoURL = info.images[0].source;
							serverFunctions.sendThroughWS(ws, photoURL, 'photo');  // sending photoURL through WebSocket
						}
						else console.log(error);
					});
				}
				else console.log(error);
			});
		}
		else console.log(error);
	});
}

/* Server calls this function in order to get today weather */
function getTodayWeather(){
	var options = {
		url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&lang=it&appid=' + appid
	}
	function callback(error, response, body){
		if (!error && response.statusCode == 200) {
			todayWeather = JSON.parse(body);
			var main = todayWeather.weather[0].main;
			console.log('Today: ' + main);
			var sunrise = serverFunctions.timeConversion(todayWeather.sys.sunrise);
			var sunset = serverFunctions.timeConversion(todayWeather.sys.sunset);
			var dt = serverFunctions.timeConversion(todayWeather.dt);
			todayWeather.sys.sunrise = sunrise;
			todayWeather.sys.sunset = sunset;
			todayWeather.dt = dt;
			wssReady = true; // server now ready to receive WebSocket request
		}
		else
			console.log(response.statusCode);
	}
	request(options, callback);
}

/* Server calls this function in order to get next 5 days weather forecast and sends only today next hours through WebSocket */
function getNextDaysWeather(ws){
	var options = {
		url : 'http://api.openweathermap.org/data/2.5/forecast?id=' + id + '&units=metric&lang=it&appid=' + appid
	}
	function callback(error, response, body){
		if (!error && response.statusCode == 200) {
			var info = JSON.parse(body);
			var list = info.list;
			for (var i=0; i<list.length; i++) {
				var time = serverFunctions.timeConversion(list[i].dt);
				if (time == '1:00:00')
					break;
				nextHours[i] = list[i];
				nextHours[i].dt = time;
			}
			console.log('GET request to OpenWeatherMap completed: acquired all infos about next hours weather forecast');
			serverFunctions.sendThroughWS(ws, nextHours, 'forecast'); // sending forecast through WebSocket
		}
		else
			console.log(response.statusCode);
	}
	request(options, callback);
}
