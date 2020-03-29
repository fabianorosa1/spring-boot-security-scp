/*eslint no-console: 0*/
"use strict";

// https://brianflove.com/2014/08/14/one-day-with-expressjs/
// https://alligator.io/nodejs/building-rest-api/
// https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/3a8e4372f8e74d05b4ed03a484865e08.html

const log = require('cf-nodejs-logging-support'); //kibana
const express = require("express");
const passport = require('passport');
const xsenv = require('@sap/xsenv');
const cds = require('@sap/cds');
const JWTStrategy = require('@sap/xssec').JWTStrategy //strategy of authorization
var bodyParser = require('body-parser');

const app = express();

//----JSON properties in the payload, we expect to receive in json format
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.urlencoded({
  extended: false,
  limit: '10mb'
}));

app.use(bodyParser.json({ limit: '10mb' }));
//----

//----Kibana logs, metrics and performance values
log.setLoggingLevel("info");
app.use(log.logNetwork);
//----

//----autenticate into UAA service
const services = xsenv.getServices({ uaa: 'spring-boot-security-scp-uaa' });
passport.use(new JWTStrategy(services.uaa));
app.use(passport.initialize());
app.use(passport.authenticate('JWT', { session: false }));
//----

app.get("/api", (req, res) => {
    res.send("Hello World, from express");
});

app.get("/api/users", (req, res) => {
  var isAuthorized = req.authInfo.checkScope('$XSAPPNAME.Display');
  if (isAuthorized) {
    res.status(200).json(["Tony","Lisa","Michael","Ginger","Food"]);
  } else {
    res.status(403).send('Forbidden');
  }
});

app.get("/api/crash", (req, res) => {
  setTimeout(function () {
        throw new Error('We crashed!!!!!');
  }, 10);
});

app.get("/healthcheck", (req, res) => {
 res.json({"status": "UP"});
});

const port = process.env.PORT || 3000;
app.listen(port, function () {
  console.log('NodeAPI listening on port ' + port);
});
