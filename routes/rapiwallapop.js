module.exports = function(app,swig, gestorBD) {

    app.post("/api/autentication/", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        };
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length === 0) {
                res.status(401);
                app.get("logger").error("Usted no esta logeado con credendiales malas: " + criterio.email);
                res.json({
                    autenticado: false,
                    error: "Las credenciales introducidas no son correctas"
                })
            } else {
                let ttoken = app.get('jwt').sign(
                    {usuario: criterio.email, id: usuarios[0]._id, tiempo: Date.now() / 1000},
                    "secreto");
                app.get("logger").info(criterio.email + " se loguea en la apliación");
                res.status(200);
                res.json({
                    autenticado: true,
                    token: ttoken
                });
            }
        });
    });

    app.get("/api/offer/list", function (req, res) {
        let criterio = {
            vendedor: {$ne: res.headers.email}
        };
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length === 0) {
               // logger.error("Error listing other offers at API ");
                res.status(500);
                res.json({
                    error: "se ha producido un error"
                });
            } else {
               // logger.info("API user lists other offers.");
                res.status(200);
                res.send(JSON.stringify(ofertas));

            }
        });
    });
}