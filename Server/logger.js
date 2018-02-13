#!/usr/bin/env node

var amqp = require('amqplib/callback_api');

amqp.connect('amqp://localhost', function(err, conn) {
	conn.createChannel(function(err, ch) {
		var q = 'log_queue';
		ch.assertQueue(q, {durable: true});
		console.log('Waiting for messages in %s', q);
		ch.consume(q, function(msg) {
			console.log('%s', msg.content.toString());
			ch.ack(msg);
		}, {noAck: false});
	});
});
