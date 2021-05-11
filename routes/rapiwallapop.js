module.exports = function(app,swig, gestorBD) {
    /**
     * Metodo que autentifica al usuario:
     * Si los campos son vacios
     * o el usuario no coincide con ninguno de la  base sale el error correspondiente
     */
    app.post("/api/autentication/", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        };
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length === 0) {
                res.status(401);
                app.get("logger").error("API: Usted no esta logeado con credendiales malas: " + criterio.email);
                res.json({
                    autenticado: false,
                    error: "Las credenciales introducidas no son correctas"
                })
            } else {
                let ttoken = app.get('jwt').sign(
                    {usuario: criterio.email, id: usuarios[0]._id, tiempo: Date.now() / 1000},
                    "secreto");
                app.get("logger").info("API;"+criterio.email + " se loguea en la apliación");
                res.status(200);
                res.json({
                    autenticado: true,
                    token: ttoken
                });
            }
        });
    });

    /**
     * Metodo que lsita todas las ofertas menos
     * las del propio usuario que esta en sesion
     */
    app.get("/api/offer/list", function (req, res) {
        let criterio = {
            vendedor: {$ne: res.usuario}
        };
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length === 0) {
                app.get("logger").error(" API:Error a la hora de listas las ofertas ");
                res.status(500);
                res.json({
                    error: "se ha producido un error"
                });
            } else {
                app.get("logger").info("API: usuario ha listado las ofertas");
                res.status(200);
                res.send(JSON.stringify(ofertas));

            }
        });
    });

    /**
     * Muestra el chat cuyo id se recibe como parámtetro.
     * En caso de no existir ninguna, crea un chat nuevo.
     */
    app.get("/api/offer/chat/:id", function (req, res) {
        let ofertaId = gestorBD.mongo.ObjectID(req.params.id);
        let criterio_oferta = {"_id" : ofertaId};

        gestorBD.obtenerOfertas(criterio_oferta, function (ofertas) {
            if (ofertas == null || ofertas.length === 0) {
                app.get("logger").info("API: error al obtener la oferta seleccionada.");
                res.status(500);
                res.json({
                    error: "Se ha producido un error"
                });
            } else {
                let criterio_chat = {
                    "oferta": ofertaId,
                    "interesado": res.usuario
                };
                gestorBD.obtenerChats(criterio_chat, function (chats) {
                    if (chats == null || chats.length === 0) {
                        app.get("logger").info("API: no existe el chat solicitado, se crea uno nuevo.");

                        let chat = {
                            "oferta": ofertaId,
                            "titulo": ofertas[0].titulo,
                            "vendedor": ofertas[0].autor,
                            "interesado": res.usuario,
                            "mensajes": {

                            }
                        }

                        gestorBD.insertarChat(chat, function (idNuevoChat) {
                            if (idNuevoChat == null) {
                                app.get("logger").info("API: error a la hora de crear el nuevo chat.");
                                res.status(500);
                                res.json({
                                    error: "Se ha producido un error"
                                });
                            } else {
                                app.get("logger").info("API: chat creado correctamente.");
                                res.status(200);
                                res.send(JSON.stringify(chat));
                            }
                        });
                    } else {
                        app.get("logger").info("API: chat cargado correctamente.");
                        res.status(200);
                        res.send(JSON.stringify(chats[0]));
                    }
                });
            }
        });
    });

    /**
     * Muestra el chat cuyo id se recibe como parámtetro y devuelve sus mensajes.
     */
    app.get("/api/offer/chat/load/:id", function (req, res) {
        let chatId = gestorBD.mongo.ObjectID(req.params.id);
        let criterio_chat = {"_id" : chatId};

        gestorBD.obtenerChats(criterio_chat, function (chats) {
            if (chats == null || chats.length === 0) {
                app.get("logger").info("API: error al obtener el chat seleccionado.");
                res.status(500);
                res.json({
                    error: "Se ha producido un error"
                });
            } else {
                let criterio_mensajes = {"chatId": chatId};
                gestorBD.obtenerMensajes(criterio_mensajes, function (mensajes) {
                    if (mensajes == null) {
                        app.get("logger").info("API: error a la hora de obtener los mensajes.");
                        res.status(500);
                        res.json({
                            error: "Se ha producido un error"
                        });
                    } else {
                        app.get("logger").info("API: mensajes cargados correctamente.");
                        res.status(200);
                        res.send(JSON.stringify(mensajes));
                    }
                });
            }
        });
    });


    /**
     * Muestra la conversación cuyo id se recibe como parámtetro.
     */
    app.post("/api/offer/chat/:id", function (req, res) {
        let chatId = gestorBD.mongo.ObjectID(req.params.id);
        let chat = req.body.chat;
        let emisor = res.usuario;
        let receptor = res.usuario;

        if (chat.interesado === res.usuario) {
            receptor = chat.vendedor;
        } else {
            emisor = chat.vendedor;
        }

        let fecha = new Date();

        let mensaje = {
            "chat": chatId,
            "oferta": req.body.ofertaId,
            "emisor": emisor,
            "receptor": receptor,
            "texto": req.body.texto,
            "fecha": fecha,
            "leido": false
        }

        gestorBD.insertarMensaje(mensaje, function (mensaje) {
            if (mensaje == null) {
                app.get("logger").info("API: error al crear el mensaje.");
                res.status(500);
                res.json({
                    error: "Se ha producido un error"
                });
            } else {
                app.get("logger").info("API: mensaje creado correctamente.");
                res.status(200);
                res.send(JSON.stringify(mensaje));
            }
        });
    });
}