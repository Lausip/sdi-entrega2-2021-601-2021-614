module.exports = function (app, swig, gestorBD) {

    /**
     * Muestra la página de registro si hay usuario en sesión,
     * si no lo hay vuelve al home
     */
    app.get("/signup", function (req, res) {
        let respuesta = swig.renderFile('views/bsignup.html', {});
        app.get('logger').info('Registrarse: se va a mostrar la página de registro');
        res.send(respuesta);

    });
    /**
     * Permite el registro de un usuario si todo va bien
     * si no se explica el error
     */
    app.post('/signup', function (req, res) {
        app.get('logger').info('Usuario se va a registrar');
        if (req.body.name == undefined || req.body.name == '' || req.body.surname == undefined || req.body.surname == ''
            || req.body.password == undefined || req.body.password == '' || req.body.repassword == undefined || req.body.repassword == ''
            || req.body.email == undefined || req.body.email == '') {
            res.redirect("/signup?mensaje=Es necesario completar todos los campos&tipoMensaje=alert-danger")
            app.get('logger').error('Es necesario completar todos los campos');

        } else if (req.body.password !== req.body.repassword) {
            res.redirect("/signup?mensaje=Las contraseñas deben ser iguales&tipoMensaje=alert-danger")
            app.get('logger').error('Las contraseñas deben ser iguales');

        } else {
            let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let usuario = {
                email: req.body.email,
                nombre: req.body.name,
                apellido: req.body.surname,
                dinero: 100,
                rol: 'estandar',
                password: seguro
            };
            let criterio = {
                email: req.body.email
            };
            gestorBD.obtenerUsuarios(criterio, function (usuarios) {
                if (usuarios != null && usuarios.length !== 0) {
                    app.get("logger").error('El email ya está registrado');
                    res.redirect("/signup?mensaje=El email ya está registrado&tipoMensaje=alert-danger");
                } else {
                    gestorBD.insertarUsuario(usuario, function (id) {
                        if (id === undefined) {
                            app.get("logger").error('Error al registrar al usuarios');
                            res.redirect("/signup?mensaje=Error al registrar usuario&tipoMensaje=alert-danger")
                        } else {
                            req.session.usuario = usuario;
                            app.get("logger").info('Usuario registrado como ' + req.body.email);
                            delete req.session.usuario.password;
                            res.redirect("/home");
                        }
                    })
                }
            })
        }
    });

    /**
     * Muestra la página de identificación.
     */
    app.get("/login", function (req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', {});
        app.get('logger').info('Identificarse: se va a mostrar la página de identificación.');
        res.send(respuesta);
    });

    /**
     * Permite el inicio de sesión del usuario si todo va bien,
     * si no, muestra el error.
     */
    app.post("/login", function (req, res) {
        if (req.body.email === undefined || req.body.email === ''
            || req.body.password === undefined || req.body.password === '') {
            app.get("logger").info('Error al identificarse: ningún dato puede estar vacío.');
            res.redirect("/login?mensaje=Ningún campo puede estar vacío.&tipoMensaje=alert-danger");
        } else {
            let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let criterio = {
                email: req.body.email,
                password: seguro
            }
            gestorBD.obtenerUsuarios(criterio, function (usuarios) {
                if (usuarios == null || usuarios.length == 0) {
                    req.session.usuario = null;
                    app.get("logger").error('Error al identificar al usuario');
                    res.redirect("/login?mensaje=Email o password incorrecto&tipoMensaje=alert-danger");
                } else {
                    req.session.usuario = usuarios[0];
                    delete req.session.usuario.password;
                    app.get("logger").info('Usuario identificado como ' + req.body.email);
                    res.redirect("/home");
                }
            });
        }
    });

    /**
     * Cierra la sesión del usuario y lo redirige a la vista de identifiación.
     */
    app.get("/logout", function (req, res) {
        req.session.usuario = null;
        app.get('logger').info('Cerrar sesión: se va a cerrar sesión y mostrar la página de identificación.');
        res.redirect("/login");
    });

    /**
     * Home privado del usuario.
     */
    app.get("/home", function (req, res) {
        let criterio = {"destacada": true};
        //let criterio = {};
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null) {
                app.get("logger").error("Error al obtener el listado de ofertas destacadas.");
                res.redirect("/home?mensaje=Error al obtener el listado de ofertas destacadas.&tipoMensaje=alert-danger");
            } else {
                let respuesta = swig.renderFile('views/bhome.html',
                    {
                        ofertaList: ofertas,
                        usuario: req.session.usuario
                    });
                app.get("logger").info('Usuario ha entrado a su zona privada.');
                res.send(respuesta);
            }
        });
    });


    /**
     * Listar los usuarios de la aplicación:
     * Si el usario que lo busca no es admin
     * No se muestra los usuarios admin
     */
    app.get("/user/list", function (req, res) {
        console.log(req.session.usuario);
        var criterio = {
            rol: "estandar",
        };
        let pg = parseInt(req.query.pg);
        if (req.query.pg == null) {
            pg = 1;
        }
        gestorBD.obtenerUsuariosPg(criterio, pg, function (usuarios, total) {
            if (usuarios == null) {
                app.get('logger').info(req.session.usuario.email + ':ha realizado un listado de usuarios: error en el listado');
                res.redirect("/user/list" +
                    "?mensaje=No se pudieron obtener los usuarios");
            } else {
                let ultimaPg = total / 5;
                if (total % 5 > 0) { // Sobran decimales
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = []; // paginas mostrar
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                let respuesta = swig.renderFile('views/user/list.html', {
                    usuarioList: usuarios,
                    paginas: paginas,
                    actual: pg,
                    usuario:req.session.usuario
                });
                app.get('logger').info(req.session.usuario.email + ' ha realizado un listado de usuarios: se va a mostrar el listado de usuarios');
                res.send(respuesta);
            }
        });
    });

    /**
     * Elimina los usuarios seleccionados en el checkbox y lo mete en un array
     *Si estos se eliminar tambien se eliminan todas aquellas conversaciones mensajes y ofertas relacionadas con él
     */
    app.post('/user/delete/', function (req, res) {
        let ids = req.body.deleteUsersCheckbox;
        if (!Array.isArray(ids)) {
            let aux = ids;
            ids = [];
            ids.push(aux);
        }
        let criterio = {
            email: {$in: ids}
        };
        gestorBD.eliminarUsuario(criterio, function (usuarios) {
            if (usuarios == null) {
                res.redirect("/user/list" +
                    "?mensaje=No se pudieron borrar los usuarios");
                app.get('logger').error(req.session.usuario.email + ':Eliminar usuarios: no se han podido eliminar');
            } else {
                let criterio = {
                    autor: {$in: ids}
                };
                //borramos oferta
                gestorBD.eliminarOferta(criterio, function (ofertas) {
                    if (ofertas == null) {
                        app.get('logger').error(req.session.usuario.email + "Fallo al eliminar ofertas creadas por los usuarios borrados");
                        res.redirect("/user/list" +
                            "?mensaje=No se pudieron borrar las ofertas de los usuarios");
                    } else {
                        // borramos chats
                        let criterioChat = {
                            $or: [{vendedor: {$in: ids}},
                                {interesado: {$in:  ids}}]
                        };
                        gestorBD.eliminarChat(criterioChat, function (conversaciones) {
                            if (conversaciones == null){
                                app.get('logger').error(req.session.usuario.email + "Fallo al eliminar chats de los usuarios borrados");
                                res.redirect("/user/list" +
                                    "?mensaje=No se pudieron borrar las conversaciones de los usuarios");
                            }
                            else{
                                // borramos mensajes
                                let criterioChat = {
                                    $or: [{emisor: {$in: ids}},
                                        {receptor: {$in:  ids}}]
                                };
                                gestorBD.eliminarMensaje(criterioChat, function (conversaciones) {
                                    if (conversaciones == null) {
                                        app.get('logger').error(req.session.usuario.email + "Fallo al eliminar los mensajes de los usuarios borrados");
                                        res.redirect("/user/list" +
                                            "?mensaje=No se pudieron borrar los mensajes");
                                    } else {
                                        app.get('logger').info(req.session.usuario.email + 'Eliminar usuarios: exito en la eliminación de usuario/s');
                                        res.redirect("/user/list" + "?mensaje=Exito en el borrado de usuario/s" +
                                            "&tipoMensaje=alert-success ");
                                    }
                                });
                            }
                        });
                    }

                });
            }
        });
    });

}