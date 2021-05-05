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

//Router de sesion
var routerUsuarioSession = express.Router();
routerUsuarioSession.use(function (req, res, next) {
    console.log("routerUsuarioSession");
    if (req.session.usuario) {
        logger.info('Usuario logeado accede a '+ req.originalUrl);
        next();
    } else {
        logger.warn("Usuario no identificado intenta aceeder a" + req.originalUrl);
        res.redirect("/identificarse?mensaje=El usuario actual no esta en sesión" +
            "&tipoMensaje=alert-danger");
    }
});

//Aplicamos el router
app.use("/home", routerUsuarioSession);
//DESCONECTARSE TB
app.use("/offer/*", routerUsuarioSession);
app.use("/user/*", routerUsuarioSession);

//Roter de usuario de no identificado
var routerUsuarioNoSession = express.Router();
routerUsuarioNoSession.use(function (req, res, next) {
    console.log("routerUsuarioNoSession");
    if (!req.session.usuario) {
        logger.info('Usuario no logeado intenta acceder '+ req.originalUrl);
        next();
    } else {
        logger.warn('User logged in tries to access '+ req.originalUrl);
        res.redirect("/home?mensaje=El usuario actual ya esta en sesión" +
            "&tipoMensaje=alert-danger");
    }
});
//Aplicamos el router
app.use("/signup", routerUsuarioNoSession);
//IDENTIFICARSE TB

//Roter de usuario de estandar

var routerEstandar = express.Router();
routerEstandar.use(function (req, res, next) {
    if (req.session.usuario.rol === 'estandar') {
        logger.info('El usuario'+ req.session.usuario.email +'va al siguiente enlace '+ req.originalUrl);
        next();
    } else {
        logger.warn(' El usuario'+req.session.usuario.email +' es no es válido ya que no es estandar ');
        res.redirect("/home?mensaje=Usted no es un usuario estandar"+
            "&tipoMensaje=alert-danger");
    }
});
//Aplicamos el router
app.use("/offer/*", routerUsuarioEstandar);
//FALTA CHAT

var routerAdmin = express.Router();
routerAdmin.use(function (req, res, next) {
    if (eq.session.usuario.rol == "admin") {
        logger.info('El administrador va al siguiente enlace '+ req.originalUrl);
        next();
    } else {
        logger.warn("El usuario" + req.session.usuario.email + "que es estandar intenta entrar a una opcion"
            + req.originalUrl);
        res.redirect("/home?mensaje=El usuario" + req.session.usuario.email + "que es estandar intenta entrar a una opcion"
            + req.originalUrl+"&tipoMensaje=alert-danger");
    }
});
//Aplicamos el router
app.use("/user/*", routerUsuarioAdmin);

//Rutas controladores
require("./routes/rusuarios.js")(app, swig, gestorBD);
require("./routes/rofertas.js")(app, swig, gestorBD);

//Puerto
app.set('port',8081);
app.listen(app.get('port'), function () {
    console.log("Servidor activo");
});

app.get('/', function (req, res) {
    res.redirect('/login');
});