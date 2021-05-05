module.exports = function (app, swig, gestorBD) {

    /**
     * Lista todas las ofertas del myWallapop
     * Se mira si el usario inicio sesion
     */
    app.get("/offer/list", function (req, res) {
        if(req.session.usuario==undefined || req.session.usuario === null){
            app.get("logger").error('Usuario no esta en sesión y querido entrar al listado de ofertas');
            res.redirect("/identificarse?mensaje=Usuario no esta en sesión&tipoMensaje=alert-danger");
        }
        else {
            var criterio = {};
            let busqueda = req.query.busqueda;
            if (busqueda != null && busqueda != "" && busqueda != undefined) {
                criterio = {"titulo": {$regex: ".*" + busqueda + ".*", $options: 'i'}};
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
                    app.get("logger").info('Listado de ofertas');
                    res.send(respuesta);
                }
            });
        }
    });

};
