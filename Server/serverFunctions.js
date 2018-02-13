var amqp = require('amqplib/callback_api');

function timeConversion(unix_timestamp) {
	var date = new Date(unix_timestamp*1000);
	var hours = date.getHours();
	var minutes = "0" + date.getMinutes();
	var seconds = "0" + date.getSeconds();
	var formattedTime = hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
	return formattedTime;
}

function sendMsgToQueue(msg) {
	amqp.connect('amqp://localhost', function(err, conn) {
		conn.createChannel(function(err, ch) {
			var q = 'log_queue';
			ch.assertQueue(q, {durable: true});
			ch.sendToQueue(q, new Buffer(msg), {persistent: true});
		});
	});
}

function sendThroughWS(ws, data, description) {
	var message = { 'data' : data, 'description' : description };
	ws.send(JSON.stringify(message));
}

function getTime() {
	var today = new Date();
	var hours = today.getUTCHours() + 1;
	var minutes = today.getUTCMinutes();
	var seconds = today.getUTCSeconds();
	var time = hours+":"+minutes+":"+seconds;

	return time;
}

module.exports.timeConversion = timeConversion;
module.exports.sendMsgToQueue = sendMsgToQueue;
module.exports.sendThroughWS = sendThroughWS;
module.exports.getTime = getTime;
