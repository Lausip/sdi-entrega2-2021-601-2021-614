module.exports = function (app, swig, gestorBD) {

    /**
     * Lista todas las ofertas del myWallapop
     * Se mira si el usario inicio sesion
     */
    app.get("/offer/list", function (req, res) {
        //HAY QUE HACER?
        if(req.session.usuario.rol != "estandar") {
            app.get("logger").error(req.session.usuario.email + ": no es estandar no puede entrar en la lista de compra");
            res.redirect("/home?mensaje=Usted no es usuario estandar no puede entrar&tipoMensaje=alert-danger");
        }

        if(req.session.usuario==undefined || req.session.usuario === null){
            app.get("logger").error('Usuario no esta en sesión y querido entrar al listado de ofertas');
            res.redirect("/identificarse?mensaje=Usuario no esta en sesión&tipoMensaje=alert-danger");
        }
        else {
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
        }
    });
    /**
     * Compras la oferta pasada como id,
     * Obtines esa oferta y miras cuanto cuesta si ya esta comprada o no tienes suficiente dinero
     * para comprala sale el error y se redirije al listado
     * además si el usario no esta identificado redirige al incio de sesión
     */
    app.get('/offer/buy/:id', function (req, res) {
        //HAY QUE HACER?
        if(req.session.usuario.rol != "estandar") {
            app.get("logger").error(req.session.usuario.email + ": no es estandar no puede entrar en la lista de compra");
            res.redirect("/home?mensaje=Usted no es usuario estandar no puede entrar&tipoMensaje=alert-danger");
        }
        if(req.session.usuario==undefined || req.session.usuario === null){
            app.get("logger").error('Usuario no esta en sesión y querido entrar al listado de ofertas');
            res.redirect("/identificarse?mensaje=Usuario no esta en sesión&tipoMensaje=alert-danger");
        }
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
        if(req.session.usuario.rol != "estandar") {
            app.get("logger").error(req.session.usuario.email + ": no es estandar no puede entrar en la lista de compra");
            res.redirect("/home?mensaje=Usted no es usuario estandar no puede entrar&tipoMensaje=alert-danger");
        }
        if(req.session.usuario==undefined){
            app.get("logger").error("No esta identificado");
            res.redirect("/identificarse?mensaje=Usted no esta identificado&tipoMensaje=alert-danger");
        }
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

};
