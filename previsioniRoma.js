const id = '3169070' // id di Roma
const appid = 'b011edc116685d89dfdc35517d9ce205' // la nostra API key 

var request = require("request");

var nextDays = new Array();

var options = {
	url : 'http://api.openweathermap.org/data/2.5/forecast?id=' + id + '&units=metric&lang=it&APPID=' + appid
}

function callback(error, response, body) {
	if (!error && response.statusCode == 200) {
		var info = JSON.parse(body);
		var list = info.list;
		var j = 0;
		for (var i=0; i<list.length; i++) {
			var time = timeConversion(list[i].dt);
			if (time == "7:00:00") {
				nextDays[j] = list[i];
				//nextDays[j].weather = JSON.stringify(list[i].weather);
				j++;
			}
		}
		console.log(nextDays);
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
