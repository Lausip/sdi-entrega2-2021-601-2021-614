module.exports = function (app, swig, gestorBD) {

    /**
     * Muestra la página de registro si hay usuario en sesión,
     * si no lo hay vuelve al home
     */
    app.get("/signup", function (req, res) {
        if (req.session.usuario == undefined) {
        let respuesta = swig.renderFile('views/bsignup.html', {});
        app.get('logger').info('Registrarse: se va a mostrar la página de registro');
        res.send(respuesta);}
        else{
            app.get('logger').error('Registrarse: no estas en sesión')
            res.redirect("/home?mensaje=Usted ya esta en sesión &tipoMensaje=alert-danger");
        }
    });
    /**
     * Permite el registro de un usuario si todo va bien
     * si no se explica el error
     */
    app.post('/signup', function (req, res) {
        app.get('logger').info('Usuario se va a registrar');
        if (req.body.name === undefined || req.body.name === '' || req.body.surname === undefined || req.body.surname === ''
            || req.body.password === undefined || req.body.password === '' || req.body.repassword === undefined || req.body.repassword === ''
            || req.body.email === undefined || req.body.email === '') {
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
     * Home privado del usuario:
     * Si no está identificado le manda a identificarse
     */
    app.get("/home", function (req, res) {
        if (req.session.usuario === null) {
            app.get("logger").error('Usuario no identificado ha intentado entrar en zona privada');
            res.redirect("/identificarse?mensaje=Usuario no identificado &tipoMensaje=alert-danger");
        }
         else {
             app.get("logger").info('Usuario ha entrado a su zona privada');
        var respuesta = swig.renderFile('views/bhome.html',
            {
                usuario: req.session.usuario
            });
            res.send(respuesta);

        }
    });


    /**
     * Listar los usuarios de la aplicación:
     * Si el usario que lo busca no es admin
     * No se muestra los usuarios admin
     */
    app.get("/user/list", function (req, res) {
        let usuario = req.session.usuario;
        if (usuario.rol == 'estandar') {
            app.get("logger").error('No se puede acceder a listar ya que no usted no es admin');
           res.redirect("/bhome?mensaje=No puede acceder a esta zona de la web");
        } else {
            var criterio = {
                rol: "estandar",
            };
            let pg = parseInt(req.query.pg); // Es String !!!
            if ( req.query.pg == null){ // Puede no venir el param
                pg = 1;
            }
            gestorBD.obtenerUsuariosPg(criterio,pg, function (usuarios,total) {
                if (usuarios == null) {
                    app.get('logger').info('Listado de usuarios: error en el listado');
                }
                else {
                    let ultimaPg = total/5;
                    if (total % 5 > 0 ){ // Sobran decimales
                        ultimaPg = ultimaPg+1;
                    }
                    let paginas = []; // paginas mostrar
                    for(let i = pg-2 ; i <= pg+2 ; i++){
                        if ( i > 0 && i <= ultimaPg){
                            paginas.push(i);
                        }
                    }
                    let respuesta = swig.renderFile('views/user/list.html', {
                        usuarioList: usuarios,
                        paginas:paginas,
                        actual:pg
                    });
                    app.get('logger').info('Listado de usuarios: se va a mostrar el listado de usuarios');
                    res.send(respuesta);
                }
            });
        }
    });

    /**
     * Elimina los usuarios seleccionados en el checkbox y lo mete en un array
     *Si estos se eliminar tambien se eliminan todas aquellas conversaciones mensajes y ofertas relacionadas con él
     */
    app.post('/user/delete/', function (req, res) {
        let ids = req.body.idsUserDelete;
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
                app.get('logger').error('Eliminar usuarios: no se han podido eliminar');
            } else {
                let criterio = {
                    vendedor: {$in: ids}
                };
                gestorBD.eliminarOferta(criterio, function (ofertas) {
                    if (ofertas == null) {
                        app.get('logger').error("Fallo al eliminar ofertas creadas por los usuarios borrados");
                        //Redireccionar
                    } else {
                        var criterio = {
                            rol: "estandar",
                        };
                        let pg = parseInt(req.query.pg); // Es String !!!
                        if (req.query.pg == null) { // Puede no venir el param
                            pg = 1;
                        }
                        gestorBD.obtenerUsuariosPg(criterio, pg, function (usuarios, total) {
                            if (usuarios == null) {
                                app.get('logger').info('Listado de usuarios: error en el listado');
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
                                    actual: pg
                                });
                                app.get('logger').info('Eliminar usuarios: exito en la eliminación de usuario/s');
                                res.redirect("/user/list" + "?mensaje=Exito en el borrado de usuario/s" +
                                    "&tipoMensaje=alert-success ");
                            }
                        });
                    }

                });
            }
        });
    });


}