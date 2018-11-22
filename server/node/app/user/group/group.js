const mongodb =require('mongodb');
const MongoClient = mongodb.MongoClient;
const dbName = 'db';
var bodyParser = require('body-parser')

module.exports = function (param) {

    const express = require('express');
    const group = express.Router();
    const fs = require('fs');

    group.use("/", (req, res, next) => {
        param['groupid'] = req.params.groupid;
        console.log("group 진입");
        next();
    })

    group.use("/:groupid", (req, res, next) => {
        param['groupid'] = req.params.groupid;
        next();
    })

    group.get("/", (req, res) => {
        var html = "'get'/" + param['userid'] + "/group 페이지 입니다.";
        res.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});//한글 깨짐 방지용
        res.end(html);
    });

    group.post("/", (req, res) => {//그룹 생성
        console.log('그룹 생성')
        var nowgroupnum; //현재 몽고 그룹 숫자
        var json = Object.keys(req.body);
        var jspar = (JSON.parse(json));
        var data;

        console.log(req.body);
        console.log('0번방 :' + json[0]);
        console.log(jspar.groupname)

        MongoClient.connect('mongodb://localhost:27017/',  { useNewUrlParser: true }, function (error, client) {
            if (error) console.log(error);
            else {
                const db = client.db(dbName);
                db.collection('numcheck').findOne(function(err,doc){
                    if (err) console.log(err);
                    else {
                        nowgroupnum = doc["groupnum"];
                        console.log(nowgroupnum);
                        Object.assign(jspar,{groupid:nowgroupnum+1}); // 그룹 아이디 증가
                        db.collection('groupmember').insert({groupid:nowgroupnum+1,groupuserid:param['userid']});
                        db.collection('groups').insert(jspar);
                        db.collection('numcheck').updateOne({groupnum:nowgroupnum},{$set:{groupnum:nowgroupnum+1}});
                        data=String(nowgroupnum+1)
                        res.send(data)
                    }
                });
            }
        });

    });


    group.delete("/:groupid", (req, res) => {//그룹 삭제 ( 자기가 나가는거임)
        console.log("그룹 삭제")
        console.log(param['userid'], param['groupid'])
        //console.log(jspar.groupname)

        MongoClient.connect('mongodb://localhost:27017/',  { useNewUrlParser: true }, function (error, client) {
            if (error) console.log(error);
            else {
                const db = client.db(dbName);
                db.collection('groupmember').remove({groupuserid:param['userid'], groupid:Number(param['groupid'])})
                res.send("ok");
            }
        });
    });

    const group_chat = require('./group_chat/group_chat')(param);
    group.use("/:groupid/chat", group_chat);

    return group;
};