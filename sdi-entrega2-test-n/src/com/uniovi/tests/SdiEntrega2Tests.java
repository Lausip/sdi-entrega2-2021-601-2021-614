package com.uniovi.tests;

import java.util.Date;
//Paquetes Java
import java.util.List;
//Paquetes JUnit 
import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertTrue;
//Paquetes Selenium 
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.*;
import org.bson.Document;

//Ordenamos las pruebas por el nombre del mÃ©todo
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SdiEntrega2Tests {
	static MongoClient mongoClient;
	static MongoDatabase database;

	// Parámetros de Laura
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "C:\\Users\\laura\\Escritorio\\Uni\\3-Uni\\2Semestre\\SDI\\LAB\\Sesion05\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";

	// Parámetros de Rut
	// static String PathFirefox65 = "C:\\Program Files\\Mozilla
	// Firefox\\firefox.exe";
	// static String Geckdriver024 =
	// "C:\\Users\\rualg\\OneDrive\\Escritorio\\SDI\\Práctica5\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";

	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081";
	static String URLApi = "http://localhost:8081/homeRest.html";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	/**
	 * Borra las tablas del mongo
	 */
	public static void removeDataTest() {
		database.getCollection("ofertas").drop();
		database.getCollection("usuarios").drop();
		database.getCollection("conversaciones").drop();
		database.getCollection("mensajes").drop();

	}

	/**
	 * Metodo que añade los datos a las deiferetnes tablas de la base de datos
	 */
	public static void insertData() {
		// Añadimos los usuarios
		MongoCollection<Document> col = database.getCollection("usuarios");
		col.insertOne(new Document().append("email", "admin@email.com").append("nombre", "Admin")
				.append("apellido", "de la Aplicación").append("dinero", 3000).append("rol", "admin")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a"));
		col.insertOne(new Document().append("email", "pepe@email.com").append("nombre", "Pepe")
				.append("apellido", "Álvarez Pérez").append("dinero", 100.0).append("rol", "estandar")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a"));
		col.insertOne(new Document().append("email", "juana@email.com").append("nombre", "Juana")
				.append("apellido", "Álvarez Fernández").append("dinero", 20.0).append("rol", "estandar")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a"));
		col.insertOne(new Document().append("email", "alberto@email.com").append("nombre", "Alberto")
				.append("apellido", "Rodríguez Álvarez").append("dinero", 0.0).append("rol", "estandar")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a"));
		col.insertOne(new Document().append("email", "susana@email.com").append("nombre", "Susana")
				.append("apellido", "González Almonte").append("dinero", 80.0).append("rol", "estandar")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a"));
		col.insertOne(new Document().append("email", "fernando@email.com").append("nombre", "Fernando Manuel")
				.append("apellido", "García del Monte").append("dinero", 10.0).append("rol", "estandar")
				.append("password", "6fabd6ea6f1518592b7348d84a51ce97b87e67902aa5a9f86beea34cd39a6b4a"));
		col = database.getCollection("ofertas");
		// Ofertas de Pepe
		col.insertOne(new Document().append("titulo", "Juguete").append("detalle", "Juguete infantil")
				.append("precio", 10.0).append("autor", "pepe@email.com").append("fecha", new Date())
				.append("comprador", "").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Patinete").append("detalle", "Patinete eléctrico")
				.append("precio", 280.0).append("autor", "pepe@email.com").append("fecha", new Date())
				.append("comprador", "juana@email.com").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Rotuladores").append("detalle", "Rotuladores de colores")
				.append("precio", 12.0).append("autor", "pepe@email.com").append("fecha", new Date())
				.append("comprador", "").append("destacada", false));

		// Ofertas Juana
		col.insertOne(new Document().append("titulo", "Raqueta").append("detalle", "Raqueta de tenis")
				.append("precio", 20.0).append("autor", "juana@email.com").append("fecha", new Date())
				.append("comprador", "fernando@email.com").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Saco").append("detalle", "Saco de dormir").append("precio", 11.0)
				.append("autor", "juana@email.com").append("fecha", new Date()).append("comprador", "susana@email.com")
				.append("destacada", false));
		col.insertOne(new Document().append("titulo", "Balón").append("detalle", "Balón de baloncesto")
				.append("precio", 10.0).append("autor", "juana@email.com").append("fecha", new Date())
				.append("comprador", "susana@email.com").append("destacada", false));

		// Ofertas Alberto
		col.insertOne(new Document().append("titulo", "Pato").append("detalle", "Pato de goma").append("precio", 3.0)
				.append("autor", "alberto@email.com").append("fecha", new Date()).append("comprador", "pepe@email.com")
				.append("destacada", true));
		col.insertOne(new Document().append("titulo", "Vestido").append("detalle", "Vestido azul")
				.append("precio", 20.0).append("autor", "alberto@email.com").append("fecha", new Date())
				.append("comprador", "").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Tienda").append("detalle", "Tienda de campaña")
				.append("precio", 80.0).append("autor", "alberto@email.com").append("fecha", new Date())
				.append("comprador", "").append("destacada", false));
		// Ofertas Susana
		col.insertOne(new Document().append("titulo", "Caja").append("detalle", "Caja de cartón azul")
				.append("precio", 5.0).append("autor", "susana@email.com").append("fecha", new Date())
				.append("comprador", "fernando@email.com").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Bicicleta").append("detalle", "Bicicleta infantil")
				.append("precio", 70.0).append("autor", "susana@email.com").append("fecha", new Date())
				.append("comprador", "juana@email.com").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Folios").append("detalle", "Paquete de 500 folios")
				.append("precio", 4.0).append("autor", "susana@email.com").append("fecha", new Date())
				.append("comprador", "alberto@email.com").append("destacada", false));
		// Ofertas Fernando
		col.insertOne(new Document().append("titulo", "Zapatos").append("detalle", "Zapatos de baile")
				.append("precio", 11.0).append("autor", "fernando@email.com").append("fecha", new Date())
				.append("comprador", "pepe@email.com").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Sandalias").append("detalle", "Sandalias de plataforma")
				.append("precio", 15.0).append("autor", "fernando@email.com").append("fecha", new Date())
				.append("comprador", "alberto@email.com").append("destacada", false));
		col.insertOne(new Document().append("titulo", "Boli").append("detalle", "Boli azul").append("precio", 2.0)
				.append("autor", "fernando@email.com").append("fecha", new Date()).append("comprador", "")
				.append("destacada", false));
		// Falta conversaciones y mensajes
	}

	/**
	 * Inicia la base de datos para las pruebas
	 */
	public static void initdb() {
		mongoClient = MongoClients.create(
				"mongodb://admin:sdi@tiendamusica-shard-00-00.nq3br.mongodb.net:27017,tiendamusica-shard-00-01.nq3br.mongodb.net:27017,tiendamusica-shard-00-02.nq3br.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-tdx28j-shard-0&authSource=admin&retryWrites=true&w=majority");
		database = mongoClient.getDatabase("myFirstDatabase");
		removeDataTest();
		insertData();
	}

	@Before
	public void setUp() {
		// Iniciamos la base de datos y vamos a la url
		initdb();
		driver.navigate().to(URL);

	}

	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}
	// database.getCollection("nhombre de la tabla");
	// objectDelATRIBUTO =objetoquedevulve.get("atributo");

	@BeforeClass
	static public void begin() {
		// Configuramos las pruebas.
		// Fijamos el timeout en cada opción de carga de una vista. 2 segundos.
		PO_View.setTimeout(3);

	}

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// ***** TESTS ******

	// PR01.Registro de Usuario con datos válidos
	@Test
	public void PR01() {
		// Vamos al formulario de registro
		driver.navigate().to(URL + "/signup");
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "laura@mail.com", "Laura", "Vigil", "123456", "123456");
		PO_View.checkElement(driver, "text", "Bienvenido a la aplicación");
	}

	// PR02. Sin hacer /
	@Test
	public void PR02() {
		assertTrue("PR02 sin hacer", false);
	}

	// PR03. Sin hacer /
	@Test
	public void PR03() {
		assertTrue("PR03 sin hacer", false);
	}

	// PR04. Sin hacer /
	@Test
	public void PR04() {
		assertTrue("PR04 sin hacer", false);
	}

	// PR05. Sin hacer /
	@Test
	public void PR05() {
		assertTrue("PR05 sin hacer", false);
	}

	// PR06. Sin hacer /
	@Test
	public void PR06() {
		assertTrue("PR06 sin hacer", false);
	}

	// PR07. Sin hacer /
	@Test
	public void PR07() {
		assertTrue("PR07 sin hacer", false);
	}

	// PR08. Sin hacer /
	@Test
	public void PR08() {
		assertTrue("PR08 sin hacer", false);
	}

	// PR09. Sin hacer /
	@Test
	public void PR09() {
		assertTrue("PR09 sin hacer", false);
	}

	// PR10. Sin hacer /
	@Test
	public void PR10() {
		assertTrue("PR10 sin hacer", false);
	}

	// PR11. Sin hacer /
	@Test
	public void PR11() {
		assertTrue("PR11 sin hacer", false);
	}

	// PR12. Sin hacer /
	@Test
	public void PR12() {
		assertTrue("PR12 sin hacer", false);
	}

	// PR13. Sin hacer /
	@Test
	public void PR13() {
		assertTrue("PR13 sin hacer", false);
	}

	// PR14. Sin hacer /
	@Test
	public void PR14() {
		assertTrue("PR14 sin hacer", false);
	}

	// PR15. Sin hacer /
	@Test
	public void PR15() {
		assertTrue("PR15 sin hacer", false);
	}

	// PR16. Sin hacer /
	@Test
	public void PR16() {
		assertTrue("PR16 sin hacer", false);
	}

	// PR017. Sin hacer /
	@Test
	public void PR17() {
		assertTrue("PR17 sin hacer", false);
	}

	// PR18. Sin hacer /
	@Test
	public void PR18() {
		assertTrue("PR18 sin hacer", false);
	}

	// PR19. Sin hacer /
	@Test
	public void PR19() {
		assertTrue("PR19 sin hacer", false);
	}

	// P20. Sin hacer /
	@Test
	public void PR20() {
		assertTrue("PR20 sin hacer", false);
	}

	// PR21. Sin hacer /
	@Test
	public void PR21() {
		assertTrue("PR21 sin hacer", false);
	}

	// PR22. Sin hacer /
	@Test
	public void PR22() {
		assertTrue("PR22 sin hacer", false);
	}

	// PR23. Sin hacer /
	@Test
	public void PR23() {
		assertTrue("PR23 sin hacer", false);
	}

	// PR24. Sin hacer /
	@Test
	public void PR24() {
		assertTrue("PR24 sin hacer", false);
	}

	// PR25. Sin hacer /
	@Test
	public void PR25() {
		assertTrue("PR25 sin hacer", false);
	}

	// PR26. Sin hacer /
	@Test
	public void PR26() {
		assertTrue("PR26 sin hacer", false);
	}

	// PR27. Sin hacer /
	@Test
	public void PR27() {
		assertTrue("PR27 sin hacer", false);
	}

	// PR029. Sin hacer /
	@Test
	public void PR29() {
		assertTrue("PR29 sin hacer", false);
	}

	// PR030. Sin hacer /
	@Test
	public void PR30() {
		assertTrue("PR30 sin hacer", false);
	}

	// PR031. Sin hacer /
	@Test
	public void PR31() {
		assertTrue("PR31 sin hacer", false);
	}

}
