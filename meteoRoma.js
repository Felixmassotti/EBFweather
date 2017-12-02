const id = '3169070' // id di Roma
const appid = 'b011edc116685d89dfdc35517d9ce205' // la nostra API key 

var request = require("request");

/*
weather.main può assumere i valori:
- Thunderstorm
- Drizzle
- Rain
- Snow
- Atmosphere
- Clear
- Clouds
- Extreme
- Additional
Maggiori dettagli ---> https://openweathermap.org/weather-conditions 
*/


var options = {
	url : 'http://api.openweathermap.org/data/2.5/weather?id=' + id + '&units=metric&appid=' + appid
}

function callback(error, response, body) {
	if (!error && response.statusCode == 200) {
		var info = JSON.parse(body);
		var sunrise = timeConversion(info.sys.sunrise);
		var sunset = timeConversion(info.sys.sunset);
		var dt = timeConversion(info.dt);
		console.log(info);
		console.log("\n" + "Sunrise: " + sunrise);
		console.log("Sunset: " + sunset);
		console.log("Time of data calculation: " + dt);
	}		
	else
		console.log(response.statusCode);
}

request(options, callback);


function timeConversion(unix_timestamp) {
	var date = new Date(unix_timestamp*1000);
	var hours = date.getHours();
	var minutes = "0" + date.getMinutes();
	var seconds = "0" + date.getSeconds();
	// Mostra il tempo nel formato h:m:s
	var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
	return formattedTime;
}	
