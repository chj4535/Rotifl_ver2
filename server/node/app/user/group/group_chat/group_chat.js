module.exports = function (param) {
    const express = require('express');
    const fs = require('fs');
    const group_chat = express.Router();

    group_chat.use("/", (req, res, next) => {
        console.log("group_chat 진입");
        next();
    });

    group_chat.use("/:chatid", (req, res, next) => {
        param['chatid']=req.params.chatid;
        next();
    });

    group_chat.get("/", (req, res) => {
        var html = "'get'/" + param['userid'] + "/group/" + param['groupid'] + "/chat 페이지 입니다.";
        res.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});//한글 깨짐 방지용
        res.end(html);
    });

    group_chat.post("/", (req, res) => {
        var html = "'post'/" + param['userid'] + "/group/" + param['groupid'] + "/chat 페이지 입니다.";
        res.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});//한글 깨짐 방지용
        res.end(html);
    });

    group_chat.delete("/:chatid", (req, res) => {
        var html = "'delete'/" + param['userid'] + "/group/" + param['groupid'] + "/chat/"+param['chatid']+" 페이지 입니다.";
        res.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});//한글 깨짐 방지용
        res.end(html);
    });

    return group_chat;
}