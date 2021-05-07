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
            vendedor: {$ne: res.headers.email}
        };
        gestorBD.obtenerOfertas(criterio, function (ofertas) {
            if (ofertas == null || ofertas.length === 0) {
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
}