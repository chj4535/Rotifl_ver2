const express = require('express');
const fs = require('fs');
const login_out = express();

login_out.post("/login",(req,res)=>{
    console.log(req.body.param1);
    var html=req.body.param1+"'post'login 페이지 입니다.";
    res.writeHead(200,{'Content-Type':'text/plain; charset=utf-8'});//한글 깨짐 방지용
    res.end(html);
});

login_out.get("/logout",(req,res)=>{
    var html="'get'logout 페이지 입니다.";
    res.writeHead(200,{'Content-Type':'text/plain; charset=utf-8'});//한글 깨짐 방지용
    res.end(html);
});

module.exports = login_out;