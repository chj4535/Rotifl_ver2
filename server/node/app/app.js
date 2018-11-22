const express = require('express');
const fs = require('fs');
const bodyparser = require('body-parser');
const mysql = require('mysql');
const app = express();
// app.js의 본문내에 삽입하시면 된다.
const mongodb =require('mongodb');
const MongoClient = mongodb.MongoClient;
var io = require('socket.io').listen(6000);
var channel;
const dbName = 'db';

io.on('connection', function (socket) {
    console.log('connect');
    var instanceId = socket.id;

    socket.on('joinRoom',function (data) {
        console.log(data);
        socket.join(data.channel);
        channel = data.channel;
        console.log(channel);
        MongoClient.connect('mongodb://localhost:27017/', function (error, client) {
            console.log(channel);
            if (error) console.log(error);
            else {
                // const db = client.db(dbName);
                // db.collection('log').find({channel:channel}).sort({data:1}).toArray(function(err,doc){
                //     if (err) console.log(err);
                //     doc.forEach(function(item){
                //         console.log(item);
                //         io.sockets.in(channel).emit('recMsg', {comment:item});
                //     });
                //     console.log("소켓입장확인");
                //     let msg={msg:socket.handshake.address+"님이 "+channel+" 채널에 입장하셨습니다.",userid:data.userid};
                //     console.log(msg);
                //     io.sockets.in(channel).emit('recMsg', {comment:msg});
                //     client.close();
                // });
            }
        });
    });

    socket.on('send', function (data) {
        let dataAddinfo = {ip: socket.handshake.address, msg: data.comment, date: Date.now(), userid:data.userid};
        console.log(dataAddinfo)
        MongoClient.connect('mongodb://localhost:27017/', function (error, client) {
            if (error) console.log(error);
            else {
                const db = client.db(dbName);
                db.collection('log').insert({
                    ip: dataAddinfo.ip,
                    msg: dataAddinfo.msg,
                    date: dataAddinfo.date,
                    channel: data.channel,
                    userid: dataAddinfo.userid
                }, function (err, doc) {
                    if (err) console.log(err);
                    client.close();
                });
            }
        });
        io.sockets.in(data.channel).emit('recMsg', {comment: dataAddinfo});
    });

    socket.on('reqMsg', function (data) {
        console.log(data);
        io.sockets.in(channel).emit('recMsg', {comment : data.comment+'\n'});
    })
});

//mysql과 연결
const connection = mysql.createConnection(
    { host:'localhost',
        user:'zoqtmxhs',
        password:'zoqtmxhs!',
        database:'db'
});

app.use(bodyparser.urlencoded({extended : true}));
app.use(bodyparser.json())

const login_out = require('./login_out/login_out');
app.use("/",login_out);

const user = require('./user/user');
app.use("/user",user);

app.listen(50000,()=>{});