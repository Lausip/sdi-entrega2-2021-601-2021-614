module.exports =function (app,swig,gestorBD){

    /**
     * Muestra la página de registro si hay usuario en sesión,
     * si no lo hay vuelve al home
     */
    app.get("/registrar", function (req, res) {
        if (req.session.usuario === undefined) {
        let respuesta = swig.renderFile('views/bregistro.html', {});
        app.get('logger').info('Registrarse: se va a mostrar la página de registro');
        res.send(respuesta);}
        else{
            app.get('logger').error('Registrarse: no estas en sesión')
            res.redirect("/home");
        }
    });
    /**
     * Permite el registro de un usuario si todo va bien
     * si no se explica el error
     */
    app.post('/registrar', function (req, res) {
        app.get('logger').info('Usuario se va a registrar');
        if (req.body.name === undefined || req.body.name === '' || req.body.surname === undefined || req.body.surname === ''
            || req.body.password === undefined || req.body.password === '' || req.body.repassword === undefined || req.body.repassword === ''
            || req.body.email === undefined || req.body.email === '') {
            res.redirect("/registrar?mensaje=Es necesario completar todos los campos&tipoMensaje=alert-danger")
            app.get('logger').error('Es necesario completar todos los campos');

        } else if (req.body.password !== req.body.repassword) {
            res.redirect("/registrar?mensaje=Las contraseñas deben ser iguales&tipoMensaje=alert-danger")
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
                    res.redirect("/registrar?mensaje=El email ya está registrado&tipoMensaje=alert-danger");
                } else {
                    gestorBD.insertarUsuario(usuario, function (id) {
                        if (id === undefined) {
                            app.get("logger").error('Error al registrar al usuarios');
                            res.redirect("/registrar?mensaje=Error al registrar usuario&tipoMensaje=alert-danger")
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
     *
     */
    app.get("/home", function (req, res) {
        if (req.session.usuario === null) {
            app.get("logger").error('Usuario no identificado ha intentado entrar en zona privada');
            res.redirect("/identificarse");
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


}