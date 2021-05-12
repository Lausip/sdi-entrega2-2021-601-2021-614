module.exports = function (app, swig, gestorBD) {

    /**
     * Envía a la vista de agregar oferta.
     */
    app.get('/offer/add', function (req, res) {
        app.get("logger").info('Acceso a la vista de agregar oferta.');
        let respuesta = swig.renderFile('views/offer/add.html', {
            usuario:req.session.usuario
        });
        res.send(respuesta);
    });

    /**
     * Procesa la petición enviada por el formulario de agregar oferta.
     */
    app.post("/offer", function (req, res) {
        if (req.body.titulo === undefined || req.body.titulo === ''
            || req.body.detalle === undefined || req.body.detalle === ''
            || req.body.precio === undefined || req.body.precio === '') {
            app.get("logger").info('Error al agregar oferta: ningún dato puede estar vacío.');
            res.redirect("/offer/add?mensaje=Error al agregar la oferta: ningún campo puede estar vacío.&tipoMensaje=alert-danger");
        } else if (req.body.titulo.length > 20) {
            app.get("logger").info('Error al agregar oferta: el título debe tener como máximo 20 caracteres.');
            res.redirect("/offer/add?mensaje=Error al agregar la oferta: el título debe tener como máximo 20 caracteres.&tipoMensaje=alert-danger");
        } else if (req.body.detalle.length > 40) {
            app.get("logger").info('Error al agregar oferta: el detalle debe tener como máximo 40 caracteres.');
            res.redirect("/offer/add?mensaje=Error al agregar la oferta: el detalle debe tener como máximo 40 caracteres.&tipoMensaje=alert-danger");
        } else if (req.body.precio < 0) {
            app.get("logger").info('Error al agregar oferta: el precio debe ser un número positivo.');
            res.redirect("/offer/add?mensaje=Error al agregar la oferta: el precio debe ser un número positivo.&tipoMensaje=alert-danger");
        } else {
            let oferta = {
                titulo: req.body.titulo,
                detalle: req.body.detalle,
                precio: req.body.precio,
                autor: req.session.usuario.email,
                fecha: req.body.fecha,
                destacada: req.body.destacada!=undefined,
                comprada: false,
                comprador:""
            }
            gestorBD.insertarOferta(oferta, function (id) {
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
                                app.get("logger").info('Oferta destacada agregada correctamente.');
                                res.redirect("/offer/myList");
                            }
                        });
                    } else {
                        app.get("logger").info('Oferta normal agregada correctamente.');
                        res.redirect("/offer/myList");
                    }
                }
            });
        }
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
                        ofertas : ofertas,
                        usuario: req.session.usuario
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
        let usuario = req.session.usuario;
        let ofertaId = gestorBD.mongo.ObjectID(req.params.id);

        let criterio = {"_id" : ofertaId};
        puedeUsuarioModificarEliminarOferta(usuario, ofertaId, function(puedeModificar) {
            if (puedeModificar == null) {
                app.get("logger").info('Error: la oferta seleccionada no pertenece al usuario identificado.');
                res.redirect("/offer/myList?mensaje=Error al encontrar la oferta seleccionada, no pertenece al usuario identificado.&tipoMensaje=alert-danger");
            } else if (!puedeModificar) {
                app.get("logger").info('Error: no se ha podido modificar la oferta, ya que está vendida.');
                res.redirect("/offer/myList?mensaje=Error al modificar la oferta seleccionada, ya está vendida.&tipoMensaje=alert-danger");
            } else {
                esOfertaDestacada(ofertaId, function(esDestacada) {
                    if (esDestacada) {
                        app.get("logger").info('Error: la oferta seleccionada ya está en estado destacada.');
                        res.redirect("/offer/myList?mensaje=Error: la oferta seleccionada ya está en estado destacada.&tipoMensaje=alert-danger");
                    } else {
                        let oferta = {
                            "destacada": true
                        };
                        gestorBD.modificarOferta(criterio, oferta, function (result) {
                            if (result == null) {
                                app.get("logger").info('Error al marcar como destacada la oferta');
                                res.redirect("/offer/myList?mensaje=Error al marcar como destacada la oferta.&tipoMensaje=alert-danger");
                            } else {
                                modificarSaldoUsuario(usuario, -20, function (nuevoSaldo) {
                                    if (nuevoSaldo == null) {
                                        app.get("logger").info('Error al realizar el pago.');
                                        res.redirect("/offer/myList?mensaje=Error al realizar el pago.&tipoMensaje=alert-danger");
                                    } else if (nuevoSaldo < 0) {
                                        app.get("logger").info('Error: el usuario no dispone del suficiente saldo.');
                                        res.redirect("/offer/myList?mensaje=Error: no dispone del suficiente saldo.&tipoMensaje=alert-danger");
                                    } else {
                                        req.session.usuario.dinero = nuevoSaldo;
                                        app.get("logger").info("La oferta ha sido marcada como destacada con éxito.");
                                        res.redirect("/offer/myList");
                                    }
                                });
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
    app.get('/offer/normal/:id', function(req, res) {
        let usuario = req.session.usuario;
        let ofertaId = gestorBD.mongo.ObjectID(req.params.id);

        let criterio = {"_id" : ofertaId};
        puedeUsuarioModificarEliminarOferta(usuario, ofertaId, function(puedeModificar) {
            if (puedeModificar == null) {
                app.get("logger").info('Error: la oferta seleccionada no pertenece al usuario identificado.');
                res.redirect("/offer/myList?mensaje=Error al encontrar la oferta seleccionada, no pertenece al usuario identificado.&tipoMensaje=alert-danger");
            } else if (!puedeModificar) {
                app.get("logger").info('Error: no se ha podido modificar la oferta, ya que está vendida.');
                res.redirect("/offer/myList?mensaje=Error al modificar la oferta seleccionada, ya está vendida.&tipoMensaje=alert-danger");
            } else {
                esOfertaDestacada(ofertaId, function(esDestacada) {
                    if (!esDestacada) {
                        app.get("logger").info('Error: la oferta seleccionada ya está en estado normal.');
                        res.redirect("/offer/myList?mensaje=Error: la oferta seleccionada ya está en estado normal.&tipoMensaje=alert-danger");
                    } else {
                        let oferta = {
                            "destacada": false
                        };
                        gestorBD.modificarOferta(criterio, oferta, function (result) {
                            if (result == null) {
                                app.get("logger").info('Error al marcar como normal la oferta');
                                res.redirect("/offer/myList?mensaje=Error al marcar como normal la oferta.&tipoMensaje=alert-danger");
                            } else {
                                modificarSaldoUsuario(usuario, 20, function (nuevoSaldo) {
                                    if (nuevoSaldo == null) {
                                        app.get("logger").info('Error al realizar el pago.');
                                        res.redirect("/offer/myList?mensaje=Error al realizar el pago.&tipoMensaje=alert-danger");
                                    } else {
                                        req.session.usuario.dinero = nuevoSaldo;
                                        app.get("logger").info("La oferta ha sido marcada como normal con éxito.");
                                        res.redirect("/offer/myList");
                                    }
                                });
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
        let usuario = req.session.usuario;
        let ofertaId = gestorBD.mongo.ObjectID(req.params.id);

        let criterio = {"_id" : ofertaId};
        puedeUsuarioModificarEliminarOferta(usuario, ofertaId, function(puedeEliminar) {
            if (puedeEliminar == null) {
                app.get("logger").info('Error: no se ha podido eliminar la oferta, ya que no pertenece al usuario identificado.');
                res.redirect("/offer/myList?mensaje=Error al eliminar la oferta seleccionada, no pertenece al usuario identificado.&tipoMensaje=alert-danger");
            } else if (!puedeEliminar) {
                app.get("logger").info('Error: no se ha podido eliminar la oferta, ya que está vendida.');
                res.redirect("/offer/myList?mensaje=Error al eliminar la oferta seleccionada, la oferta ya se ha vendido.&tipoMensaje=alert-danger");
            } else {
                gestorBD.eliminarOferta(criterio, function (ofertas) {
                    if (ofertas == null) {
                        app.get("logger").info('Error: no se ha podido eliminar la oferta.');
                        res.redirect("/offer/myList?mensaje=Error al eliminar la oferta seleccionada&tipoMensaje=alert-danger");
                    } else {
                        app.get("logger").info('La oferta ha sido eliminada correctamente.');
                        res.redirect("/offer/myList");
                    }
                });
            }
        });
    });

    /**
     * Lista todas las ofertas del myWallapop menos la del usuario que inicia sesión
     * Se mira si el usario inicio sesion
     */
    app.get("/offer/list", function (req, res) {
            var criterio = {
                autor: {$ne: req.session.usuario.email}
            };
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
            if (ofertas[0].comprador!=null && ofertas[0].comprador!=""){
                app.get("logger").error('Error al comprar la oferta, ya está comprada');
                res.redirect("/offer/list?mensaje=Oferta ya comprada&tipoMensaje=alert-danger");
            }
            if (ofertas[0].precio > req.session.usuario.dinero){
                app.get("logger").error('Error al comprar la oferta, saldo insuficiente');
                res.redirect("/offer/list?mensaje=Saldo insuficiente&tipoMensaje=alert-danger");
            }
            else {
                let oferta = {
                    "comprador": req.session.usuario.email,
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
        let criterio = {"comprador": req.session.usuario.email};
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
     * Función que incrementa el saldo del usuario pasado como parámetro en la cantidad
     * indicada en el parámtro correspondiente (puede ser negativo).
     * @param usuario a modificar.
     * @param saldoIncrementar valor a incrementar (puede ser negativo para decrementar).
     * @param funcionCallback
     */
    function modificarSaldoUsuario(usuario, saldoIncrementar, funcionCallback) {
        let nuevoSaldo = usuario.dinero + saldoIncrementar;
        if (nuevoSaldo < 0) {
            funcionCallback(-1);
        } else {
            let criterio = {"email": usuario.email};
            let usuarioModificado = {
                "dinero": nuevoSaldo
            }
            gestorBD.modificarUsuario(criterio, usuarioModificado, function (result) {
                if (result == null) {
                    funcionCallback(null);
                } else {
                    funcionCallback(nuevoSaldo);
                }
            });
        }
    }

    /**
     * Comprueba si el usuario pasado como parámetro puede modificar o eliminar
     * la oferta cuyo ID se pasa también como parámetro. Esto es cuando es suya
     * y no ha sido vendida.
     * @param usuario
     * @param ofertaId
     * @param funcionCallback
     */
    function puedeUsuarioModificarEliminarOferta(usuario, ofertaId, funcionCallback) {
        let criterio_oferta_autor = { $and : [{"_id" : ofertaId}, {"autor" : usuario.email}] };
        gestorBD.obtenerOfertas(criterio_oferta_autor, function(ofertas) {
            if (ofertas == null || ofertas.length !== 1) {
                funcionCallback(null);
            } else {
                if (ofertas[0].comprada) {
                    funcionCallback(false);
                } else {
                    funcionCallback(true);
                }
            }
        });
    }

    /**
     * Comprueba si una oferta cuyo ID se pasa como parámtro está o no destacada.
     * @param ofertaId
     * @param funcionCallback
     */
    function esOfertaDestacada(ofertaId, funcionCallback) {
        let criterio = {"_id" : ofertaId};
        gestorBD.obtenerOfertas(criterio, function(ofertas) {
            if (ofertas == null || ofertas.length !== 1) {
                funcionCallback(null);
            } else {
                if (ofertas[0].destacada) {
                    funcionCallback(true);
                } else {
                    funcionCallback(false);
                }
            }
        });
    }

};
