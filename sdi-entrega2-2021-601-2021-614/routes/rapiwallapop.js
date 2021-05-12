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
        if (req.body.email == "" || req.body.password == "") {
            res.status(401);
            app.get("logger").error("API: Campos vacios");
            res.json({
                autenticado: false,
                error: "No puede haber campos vacios"
            })
            return;
        }
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
            autor: {$ne: res.usuario}
        };
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length == 0) {
                app.get("logger").error(" API:Error a la hora de listas las ofertas ");
                res.status(500);
                res.json({
                    error: "API:Error a la hora de listas las ofertas "

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
                                res.send(JSON.stringify(idNuevoChat));
                            }
                        });
                    } else {
                        app.get("logger").info("API: chat cargado correctamente.");
                        res.status(200);
                        res.send(JSON.stringify(chats[0]._id));
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
                let criterio_mensajes = {"chat": chatId};
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
     * Muestra la conversación cuyo id se recibe como parámetro.
     */
    app.post("/api/offer/chat/:id", function (req, res) {
        let chatId = gestorBD.mongo.ObjectID(req.params.id);
        let emisor = res.usuario;
        let receptor = res.usuario;

        let criterio_chat = {"_id": chatId};
        gestorBD.obtenerChats(criterio_chat, function (chats) {
            if (chats == null || chats.length === 0) {
                app.get("logger").info("API: error al obtener el chat seleccionado.");
                res.status(500);
                res.json({
                    error: "Se ha producido un error"
                });
            } else {
                if (chats[0].interesado === res.usuario) {
                    receptor = chats[0].vendedor;
                } else {
                    emisor = chats[0].vendedor;
                }
            }
        });

        let mensaje = {
            "chat": chatId,
            "oferta": req.body.ofertaId,
            "emisor": emisor,
            "receptor": receptor,
            "texto": req.body.texto,
            "fecha": fechaHoraActuales(),
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

    /**
     * Metodo que lsita todos los chats que tiene
     * iniciados el usuario como vendedor.
     */
    app.get("/api/offer/chat/list/vendedor", function (req, res) {
        let criterio = {
            vendedor: res.usuario
        };
        gestorBD.obtenerChats(criterio, function (chats) {
            if (chats == null) {
                app.get("logger").error(" API:Error a la hora de listar los chats como vendedor.");
                res.status(500);
                res.json({
                    error: "API:Error a la hora de listar los chats como vendedor."
                });
            } else {
                app.get("logger").info("API: usuario ha listado los chats como vendedor.");
                res.status(200);
                res.send(JSON.stringify(chats));
            }
        });
    });

    /**
     * Metodo que lsita todos los chats que tiene
     * iniciados el usuario como interesado.
     */
    app.get("/api/offer/chat/list/interesado", function (req, res) {
        let criterio = {
            interesado: res.usuario
        };
        gestorBD.obtenerChats(criterio, function (chats) {
            if (chats == null) {
                app.get("logger").error(" API:Error a la hora de listar los chats como interesado.");
                res.status(500);
                res.json({
                    error: "API:Error a la hora de listar los chats como interesado."
                });
            } else {
                app.get("logger").info("API: usuario ha listado los chats como interesado.");
                res.status(200);
                res.send(JSON.stringify(chats));
            }
        });
    });

    /**
     * Metodo que elimina el chat cuyo ID se pasa
     * como parámetro y sus mensajes asociados.
     */
    app.delete("/api/offer/chat/:id", function (req, res) {
        let chatId = gestorBD.mongo.ObjectID(req.params.id);
        let criterio_chat = { $and: [
                {"_id": chatId},
                {$or: [
                    {"interesado": res.usuario},
                    {"vendedor": res.usuario}
                ]}
            ]
        };

        gestorBD.obtenerChats(criterio_chat, function (chats) {
            if (chats == null || chats.length === 0) {
                app.get("logger").error("API:Error a la hora de obtener los chats seleccionados.");
                res.status(500);
                res.json({
                    error: "API:Error a la hora de obtener los chats seleccionados."
                });
            } else {
                let criterio_mensaje = {"chatId": chatId};
                gestorBD.eliminarMensaje(criterio_mensaje, function (result) {
                    if (result == null) {
                        app.get("logger").error("API:Error a la hora de eliminar los mensajes seleccionados.");
                        res.status(500);
                        res.json({
                            error: "API:Error a la hora de eliminar los mensajes seleccionados."
                        });
                    } else {
                        gestorBD.eliminarChat(criterio_chat, function (reuslt) {
                            if (result == null) {
                                app.get("logger").error("API:Error a la hora de eliminar el chat seleccionado.");
                                res.status(500);
                                res.json({
                                    error: "API:Error a la hora de eliminar el chat seleccionado."
                                });
                            } else {
                                app.get("logger").info("API: usuario ha eliminado el chat seleccionado.");
                                res.status(200);
                                res.send(JSON.stringify(result));
                            }
                        });
                    }
                });
            }
        });
    });

    function fechaHoraActuales() {
        let hoy = new Date();
        let fecha = hoy.getDate() + "-" + (hoy.getMonth() + 1) + "-" + hoy.getFullYear();
        let hora = hoy.getHours() + ":" + hoy.getMinutes();
        let fechaHora = fecha + " " + hora;
        return fechaHora;
    }

}