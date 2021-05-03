// Módulos
let express = require('express');
let app = express();
//REST
let rest = require('request');
app.set('rest',rest);

// Log
let log4js = require('log4js');
log4js.configure({
    appenders: {myWallapop: {type: 'file', filename: 'logs/myWallapop.log'}},
    categories: {default: {appenders: ['myWallapop'], level: 'trace'}}
});
let logger = log4js.getLogger('myWallapop');
app.set('logger', logger);

//JQUERY
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
   next();
});

let jwt = require('jsonwebtoken');
app.set('jwt',jwt);

let fs = require('fs');
let https = require('https');

let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}))

let crypto = require('crypto');
let fileUpload = require('express-fileupload');
app.use(fileUpload());
let mongo = require('mongodb');
let swig = require('swig');

app.use(express.static('public'));

let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
let gestorBD = require("./modules/gestorBD.js");
gestorBD.init(app,mongo);

//Variables
app.set('db','mongodb://admin:sdi@tiendamusica-shard-00-00.nq3br.mongodb.net:27017,tiendamusica-shard-00-01.nq3br.mongodb.net:27017,tiendamusica-shard-00-02.nq3br.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-tdx28j-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('clave','abcdefg');
app.set('crypto',crypto);

//Rutas controladores
require("./routes/rusuarios.js")(app, swig, gestorBD);

//Puerto
app.set('port',8081);
app.listen(app.get('port'), function () {
    console.log("Servidor activo");
});

app.get('/', function (req, res) {
    res.redirect('/login');
});