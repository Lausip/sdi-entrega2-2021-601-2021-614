module.exports = function (app, swig, gestorBD) {

    /**
     * Envía a la vista de agregar oferta.
     */
    app.get('/offer/add', function (req, res) {
        if (req.session.usuario == null) {
            app.get("logger").info('Error: el usuario no está identificado.');
            res.redirect("/login");
            return;
        }
        let respuesta = swig.renderFile('views/offer/add.html', { });
        res.send(respuesta);
    });

    /**
     * Procesa la petición enviada por el formulario de agregar oferta.
     */
    app.post("/offer", function (req, res) {
        let oferta = {
            titulo : req.body.titulo,
            detalle : req.body.detalle,
            precio : req.body.precio,
            autor : req.session.usuario.email,
            fecha : req.body.fecha,
            destacada : req.body.destacada,
            comprada : false
        }
        gestorBD.insertarOferta(oferta, function(id) {
            if (id == null) {
                app.get("logger").info('Error al agregar la oferta');
                res.redirect("/offer/add?mensaje=Error al agregar la oferta&tipoMensaje=alert-danger");
            } else {
                if (oferta.destacada) {
                    modificarSaldoUsuario(req.session.usuario, -20, function (nuevoSaldo) {
                        if (nuevoSaldo == null) {
                            app.get("logger").info('Error al realizar el pago.');
                            res.redirect("/offer/myList?mensaje=Error al realizar el pago.&tipoMensaje=alert-danger");
                        } else if (nuevoSaldo < 0) {
                            app.get("logger").info('Error: el usuario no dispone del suficiente saldo.');
                            res.redirect("/offer/myList?mensaje=Error: no dispone del suficiente saldo.&tipoMensaje=alert-danger");
                        } else {
                            req.session.usuario.dinero = nuevoSaldo;
                            app.get("logger").info('Oferta agregada correctamente.');
                            res.redirect("/offer/myList");
                        }
                    });
                }
            }
        });
    });

    /**
     * Muestra la lista de ofertas del usuario.
     */
    app.get('/offer/myList', function (req, res) {
        let criterio = { autor : req.session.usuario.email };
        gestorBD.obtenerOfertas(criterio, function(ofertas) {
            if (ofertas == null) {
                app.get("logger").info('Error: no se han podido listar las ofertas.');
                res.redirect("/offer/myList?mensaje=Error al mostrar las ofertas&tipoMensaje=alert-danger");
            } else {
                let respuesta = swig.renderFile('views/offer/myList.html',
                    {
                        ofertas : ofertas
                    });
                app.get("logger").info('Las ofrtas del usuario han sido listadas correctamente.');
                res.send(respuesta);
            }
        });
    });

    /**
     * Destaca la oferta con el id seleccionado.
     */
    app.get('/offer/destacar/:id', function(req, res) {
        let criterio = {"_id" : gestorBD.mongo.ObjectID(req.params.id)};
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length !== 1) {
                app.get("logger").info('Error: no se ha encontrado la oferta seleccionada.');
                res.redirect("/offer/myList?mensaje=Error al encontrar la oferta seleccionada&tipoMensaje=alert-danger");
            } else if (ofertas[0].autor !== req.session.usuario.email) {
                app.get("logger").info('Error: la oferta seleccionada no es de la propiedad del usuario.');
                res.redirect("/offer/myList?mensaje=Error: la oferta seleccionada no es de su propiedad&tipoMensaje=alert-danger");
            } else if (ofertas[0].destacada) {
                app.get("logger").info('Error: la oferta seleccionada ya está destacada.');
                res.redirect("/offer/myList?mensaje=Error: la oferta seleccionada ya está destacada.&tipoMensaje=alert-danger");
            } else {
                let oferta = {
                    "destacada": true
                };
                gestorBD.modificarOferta(criterio, oferta, function (result) {
                    if (result == null) {
                        app.get("logger").info('Error al destacar la oferta');
                        res.redirect("/offer/myList?mensaje=Error al destacar la oferta.&tipoMensaje=alert-danger");
                    } else {
                        modificarSaldoUsuario(req.session.usuario, -20, function (nuevoSaldo) {
                            if (nuevoSaldo == null) {
                                app.get("logger").info('Error al realizar el pago.');
                                res.redirect("/offer/myList?mensaje=Error al realizar el pago.&tipoMensaje=alert-danger");
                            } else if (nuevoSaldo < 0) {
                                app.get("logger").info('Error: el usuario no dispone del suficiente saldo.');
                                res.redirect("/offer/myList?mensaje=Error: no dispone del suficiente saldo.&tipoMensaje=alert-danger");
                            } else {
                                req.session.usuario.dinero = nuevoSaldo;
                                app.get("logger").info("La oferta ha sido destacada con éxito.");
                                res.redirect("/offer/myList");
                            }
                        });
                    }
                });
            }
        });

    });



    /**
     * Marca como no destacada la oferta con el id seleccionado.
     */
    app.get('/offer/nodestacar/:id', function(req, res) {
        let criterio = {"_id" : gestorBD.mongo.ObjectID(req.params.id)};
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length !== 1) {
                app.get("logger").info('Error: no se ha encontrado la oferta seleccionada.');
                res.redirect("/offer/myList?mensaje=Error al encontrar la oferta seleccionada&tipoMensaje=alert-danger");
            } else if (ofertas[0].autor !== req.session.usuario.email) {
                app.get("logger").info('Error: la oferta seleccionada no es de la propiedad del usuario.');
                res.redirect("/offer/myList?mensaje=Error: la oferta seleccionada no es de su propiedad&tipoMensaje=alert-danger");
            } else if (!ofertas[0].destacada) {
                app.get("logger").info('Error: la oferta seleccionada ya está no destacada.');
                res.redirect("/offer/myList?mensaje=Error: la oferta seleccionada ya está no destacada.&tipoMensaje=alert-danger");
            } else {
                let oferta = {
                    "destacada": false
                };
                gestorBD.modificarOferta(criterio, oferta, function (result) {
                    if (result == null) {
                        app.get("logger").info('Error al no destacar la oferta');
                        res.redirect("/offer/myList?mensaje=Error al no destacar la oferta.&tipoMensaje=alert-danger");
                    } else {
                        modificarSaldoUsuario(req.session.usuario, 20, function (nuevoSaldo) {
                            if (nuevoSaldo == null) {
                                app.get("logger").info('Error al realizar el pago.');
                                res.redirect("/offer/myList?mensaje=Error al realizar el pago.&tipoMensaje=alert-danger");
                            } else {
                                req.session.usuario.dinero = nuevoSaldo;
                                app.get("logger").info("La oferta ha sido no destacada con éxito.");
                                res.redirect("/offer/myList");
                            }
                        });
                    }
                });
            }
        });
    });

    /**
     * Elimina la oferta con el id seleccionado.
     */
    app.get('/offer/delete/:id', function (req, res) {
        let criterio = {"_id" : gestorBD.mongo.ObjectID(req.params.id)};
        gestorBD.eliminarOferta(criterio, function (ofertas) {
            if (ofertas == null) {
                app.get("logger").info('Error: no se ha podido eliminar la oferta.');
                res.redirect("/offer/myList?mensaje=Error al eliminar la oferta seleccionada&tipoMensaje=alert-danger");
            } else {
                app.get("logger").info('La oferta ha sido eliminada correctamente.');
                res.redirect("/offer/myList");
            }
        });
    });

    /**
     * Lista todas las ofertas del myWallapop
     * Se mira si el usario inicio sesion
     */
    app.get("/offer/list", function (req, res) {
            var criterio = {};
            let busqueda = req.query.busqueda;
            if (busqueda != null && busqueda != "" && busqueda != undefined) {
                app.get("logger").error(req.session.usuario.email+' ha realizado una busqueda de una oferta con busqueda '+busqueda);
                criterio = {"titulo": {$regex: ".*" + busqueda + ".*", $options: 'i'}};
            }
            else{
            app.get("logger").error('Buscar oferta con busqueda');
            }
            var pg = parseInt(req.query.pg);
            if (req.query.pg == null) {
                pg = 1;
            }
            gestorBD.obtenerOfertasPg(criterio, pg, function (ofertas, total) {
                if (ofertas == null) {
                    app.get("logger").error('Error de listado de ofertas');

                } else {

                    var ultimaPg = total / 5;
                    if (total % 5 > 0) { // Sobran decimales
                        ultimaPg = ultimaPg + 1;
                    }
                    var paginas = []; // paginas mostrar
                    for (var i = pg - 2; i <= pg + 2; i++) {
                        if (i > 0 && i <= ultimaPg) {
                            paginas.push(i);
                        }
                    }
                    var respuesta = swig.renderFile('views/offer/list.html', {
                        ofertaList: ofertas,
                        usuario: req.session.usuario,
                        busqueda: busqueda,
                        paginas: paginas,
                        actual: pg,
                    });
                    app.get("logger").info( req.session.usuario.email+ 'ha relizado un listado de ofertas');
                    res.send(respuesta);
                }
            });
    });
    /**
     * Compras la oferta pasada como id,
     * Obtines esa oferta y miras cuanto cuesta si ya esta comprada o no tienes suficiente dinero
     * para comprala sale el error y se redirije al listado
     * además si el usario no esta identificado redirige al incio de sesión
     */
    app.get('/offer/buy/:id', function (req, res) {
        let criterio = {
            "_id": gestorBD.mongo.ObjectID(req.params.id)
        };
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length == 0){
                app.get("logger").error('Error al comprar la oferta');
                res.redirect("/offer/list?mensaje=Error al comprar la oferta&tipoMensaje=alert-danger");
            }
            if (ofertas[0].comprada){
                app.get("logger").error('Error al comprar la oferta, ya está comprada');
                res.redirect("/offer/list?mensaje=Oferta ya comprada&tipoMensaje=alert-danger");
            }
            if (ofertas[0].precio > req.session.usuario.dinero){
                app.get("logger").error('Error al comprar la oferta, saldo insuficiente');
                res.redirect("/offer/list?mensaje=Saldo insuficiente&tipoMensaje=alert-danger");
            }
            else {
                let oferta = {
                    "comprador": gestorBD.mongo.ObjectID(req.session.usuario._id),
                    "comprada":true
                };
                gestorBD.modificarOferta(criterio, oferta, function (result) {
                    if (result == null) {
                        app.get("logger").error('Error al comprar la oferta');
                        //redireccionar
                    } else {
                        criterio = {"email": req.session.usuario.email};
                        let nuevoSaldo = req.session.usuario.dinero - ofertas[0].precio;
                        let usuarioModificado = {"dinero": nuevoSaldo};

                        gestorBD.modificarUsuario(criterio, usuarioModificado, function (result) {
                            if (result == null) {
                                logger.error("Error al comprar las ofertas " + req.params.id);
                            } else {
                                req.session.usuario.dinero = nuevoSaldo;
                                app.get("logger").info(req.session.usuario.email + ": ha comprado la oferta" + req.params.id);
                                res.redirect("/offer/list");
                            }
                        });
                    }
                });
            }
        });
    });
    /**
     * Listado de ofertas compradas por el usuario que inicio
     * sesión en la aplicación
     */
    app.get("/offer/buyed", function (req, res) {
        let criterio = {"comprador": gestorBD.mongo.ObjectID(req.session.usuario._id)};
            gestorBD.obtenerOfertas(criterio, function (ofertas) {
                if (ofertas == null) {
                    app.get("logger").error(req.session.usuario.email + ":Error al realizar el listado de ofertas");
                    res.redirect("/home?mensaje=Error al realizar el listado de ofertas&tipoMensaje=alert-danger");
                } else {
                    var respuesta = swig.renderFile('views/offer/listOffersBuyed.html',
                        {
                            ofertaList: ofertas,
                            usuario: req.session.usuario
                        });
                    res.send(respuesta);
                }
            });
         });

    /**
     * Función que incrementa al usuario el valor solicitado (puede ser negativo).
     * @param usuario a modificar.
     * @param saldoIncrementar valor a incrementar (puede ser negativo para decrementar).
     * @param funcionCallback
     */
    function modificarSaldoUsuario(usuario, saldoIncrementar, funcionCallback) {
        let nuevoSaldo = usuario.dinero + saldoIncrementar;
        if (nuevoSaldo < 0) {
            funcionCallback(-1);
        }

        let criterio = {"email" : usuario.email};
        let usuarioModificado = {
            "dinero" : nuevoSaldo
        }

        gestorBD.modificarUsuario(criterio, usuarioModificado, function (result) {
            if (result == null) {
                funcionCallback(null);
            } else {
                funcionCallback(nuevoSaldo);
            }
        });
    }

};
