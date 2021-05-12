package com.uniovi.tests;

import java.util.Date;
//Paquetes Java
import java.util.List;
//Paquetes JUnit 
import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
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
	//static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	//static String Geckdriver024 = "C:\\Users\\rualg\\OneDrive\\Escritorio\\SDI\\Práctica5\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";

	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "https://localhost:8081/";
	static String URLApi = "https://localhost:8081/homeRest.html";

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
		database.getCollection("chats").drop();
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
		mongoClient = MongoClients.create("mongodb://admin:sdi@tiendamusica-shard-00-00.nq3br.mongodb.net:27017,tiendamusica-shard-00-01.nq3br.mongodb.net:27017,tiendamusica-shard-00-02.nq3br.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-tdx28j-shard-0&authSource=admin&retryWrites=true&w=majority");
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
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "laura@mail.com", "Laura", "Vigil", "123456", "123456");
		PO_View.checkElement(driver, "text", "Bienvenido a la aplicación");
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR02.Registro de Usuario con datos inválidos (email, nombre y apellidos
	// vacíos)
	@Test
	public void PR02() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// 1 Email vacio
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "", "Laura", "Vigil Laruelo", "123456", "123456");
		PO_View.checkElement(driver, "text", "Es necesario completar todos los campos");
		// 2 nombre vacio
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "laura@email.com", "", "Vigil Laruelo", "123456", "123456");
		PO_View.checkElement(driver, "text", "Es necesario completar todos los campos");
		// 3 apellidos vacio
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "laura@email.com", "Laura", "", "123456", "123456");
		PO_View.checkElement(driver, "text", "Es necesario completar todos los campos");
	}

	// PR03.Registro de Usuario con datos inválidos (repetición de contraseña
	// inválida).
	@Test
	public void PR03() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "laura@email.com", "Laura", "Vigil Laruelo", "123456", "1234");
		PO_View.checkElement(driver, "text", "Las contraseñas deben ser iguales");
	}

	// PR04. Registro de Usuario con datos inválidos (email existente).
	@Test
	public void PR04() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "pepe@email.com", "Pepe", "Alvarez Gutierrez", "123456", "123456");
		PO_View.checkElement(driver, "text", "El email ya está registrado");
	}


	// PR05. Inicio de sesión con datos válidos.
	@Test
	public void PR05() {
		// Vamos al formulario de login e iniciamos sesión
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		// Comprobamos que se ha iniciado correctamente y se muestra la página principal.
		SeleniumUtils.textoPresentePagina(driver, "alberto@email.com");
		SeleniumUtils.textoPresentePagina(driver, "Bienvenido a la aplicación");
	}

	// PR06. Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta).
	@Test
	public void PR06() {
		// Vamos al formulario de login e iniciamos sesión
		PO_LoginView.fillForm(driver, "alberto@email.com", "111111");
		// Comprobamos que se muestra el mensaje de error.
		SeleniumUtils.textoPresentePagina(driver, "Email o password incorrecto");
	}

	// PR07. Inicio de sesión con datos inválidos (campo email o contraseña vacíos).
	@Test
	public void PR07() {
		// Vamos al formulario de login e iniciamos sesión
		PO_LoginView.fillForm(driver, "", "123456");
		// Comprobamos que se muestra el mensaje de error.
		SeleniumUtils.textoPresentePagina(driver, "Ningún campo puede estar vacío.");
		// Vamos al formulario de login e iniciamos sesión
		PO_LoginView.fillForm(driver, "alberto@email.com", "");
		// Comprobamos que se muestra el mensaje de error.
		SeleniumUtils.textoPresentePagina(driver, "Ningún campo puede estar vacío.");
	}

	// PR08. Inicio de sesión con datos inválidos (email no existente en la aplicación).
	@Test
	public void PR08() {
		// Vamos al formulario de login e iniciamos sesión
		PO_LoginView.fillForm(driver, "alb@email.com", "123456");
		// Comprobamos que se muestra el mensaje de error.
		SeleniumUtils.textoPresentePagina(driver, "Email o password incorrecto");
	}

	// PR09. Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de
	// inicio de sesión (Login).
	@Test
	public void PR09() {
		// Iniciamos sesión con un usuario válido.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		// Cerramos sesión.
		PO_PrivateView.logout(driver);
		// Comprobamos que se ha cerrado sesión correctamente.
		SeleniumUtils.textoPresentePagina(driver, "Identificación");
	}

	// PR10. Comprobar que el botón cerrar sesión no está visible si el usuario no está autenticado.
	@Test
	public void PR10() {
		// Iniciamos sesión con un usuario válido.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		// Cerramos sesión.
		PO_PrivateView.logout(driver);
		// Comprobamos que ya no se muestra el texto de cerrar sesión.
		SeleniumUtils.textoNoPresentePagina(driver, "Cierra sesión");
	}

	// PR11. Mostrar el listado de usuarios y comprobar que se muestran todos los
	// que existen en el
	// sistema
	@Test
	public void PR11() {
		// Vamos a la vista de la lista de usuarios como admin
		PO_PrivateView.userList(driver, "admin@email.com", "123456");
		// Comprobamos cuantos usarios hay
		List<WebElement> elementos2 = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr",
				PO_View.getTimeout());
		assertTrue(elementos2.size() == 5);
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR12. Ir a la lista de usuarios, borrar el primer usuario de la lista,
	// comprobar que la lista se
	// actualiza y dicho usuario desaparece.
	@Test
	public void PR12() {
		// Vamos a la vista de la lista de usuarios como admin
		PO_PrivateView.userList(driver, "admin@email.com", "123456");
		// Seleccionamos el checkbox del primero los usuarios de la lista.
		// Viendo que se cargar los primeros 5
		List<WebElement> elementos = PO_View.checkElement(driver, "text", "García del Monte");
		elementos = driver.findElements(By.name("deleteUsersCheckbox"));
		elementos.get(0).click();
		// Hacemos click en el botón de Eliminar.
		WebElement button = driver.findElement(By.id("deleteButton"));
		button.click();
		// Esperamos a que vuelva a cargar la página.
		elementos = PO_View.checkElement(driver, "text", "García del Monte");
		// Comprobamos que el usuario ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "pepe@email.com", PO_View.getTimeout());
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR13. Ir a la lista de usuarios, borrar el último usuario de la lista,
	// comprobar que la lista se
	// actualiza y dicho usuario desaparece.
	@Test
	public void PR13() {
		// Vamos a la vista de la lista de usuarios como admin
		PO_PrivateView.userList(driver, "admin@email.com", "123456");
		// Seleccionamos el checkbox del primero los usuarios de la lista.
		// Viendo que se cargar los primeros 5
		List<WebElement> elementos = PO_View.checkElement(driver, "text", "García del Monte");
		elementos = driver.findElements(By.name("deleteUsersCheckbox"));
		elementos.get(elementos.size() - 1).click();
		// Hacemos click en el botón de Eliminar.
		WebElement button = driver.findElement(By.id("deleteButton"));
		button.click();
		// Esperamos a que vuelva a cargar la página.
		elementos = PO_View.checkElement(driver, "text", "González Almonte");
		// Comprobamos que el usuario ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "fernando@email.com", PO_View.getTimeout());
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR14.Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se
	// actualiza y dichos
	// usuarios desaparecen
	@Test
	public void PR14() {
		// Vamos a la vista de la lista de usuarios como admin
		PO_PrivateView.userList(driver, "admin@email.com", "123456");
		// Seleccionamos el checkbox del primero los usuarios de la lista.
		// Esperamos a que se muestre hasta el último usuario.
		List<WebElement> elementos = PO_View.checkElement(driver, "text", "García del Monte");
		elementos = driver.findElements(By.name("deleteUsersCheckbox"));
		elementos.get(1).click();
		elementos.get(2).click();
		elementos.get(3).click();

		// Hacemos click en el botón de Eliminar.
		WebElement button = driver.findElement(By.id("deleteButton"));
		button.click();

		// Esperamos a que vuelva a cargar la página.
		elementos = PO_View.checkElement(driver, "text", "García del Monte");

		// Comprobamos que los usuarios ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "juana@email.com", PO_View.getTimeout());
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "alberto@email.com", PO_View.getTimeout());
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "susana@email.com", PO_View.getTimeout());

		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR15. Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit.
	// Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
	@Test
	public void PR15() {
		// Iniciamos sesión.
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		// Vamos a la opción del menú de agregar oferta.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		// Rellenamos los campos y agregamos la nueva oferta.
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "15.0",false);
		// Comprobamos que se muestra en el listado de ofertas del usuario.
		PO_HomeView.checkElement(driver, "text", "Hola");
	}

	// PR16. Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío y
	// precio en negativo) y pulsar el botón Submit. Comprobar que se muestra el mensaje de campo
	// obligatorio.
	@Test
	public void PR16() {
		// Iniciamos sesión.
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		// Vamos a la opción del menú de agregar oferta.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		// Rellenamos el los campos e intentamos agregar la oferta.
		PO_AddOfferView.fillForm(driver, "", "Hola esto es una prueba", "15.0", false);
		// Comprobamos que se muestra el mensaje de error correspondiente.
		SeleniumUtils.textoPresentePagina(driver, "Error al agregar la oferta: ningún campo puede estar vacío.");
		// Rellenamos el los campos e intentamos agregar la oferta.
		PO_AddOfferView.fillForm(driver, "Hola", "", "15.0", false);
		// Comprobamos que se muestra el mensaje de error correspondiente.
		SeleniumUtils.textoPresentePagina(driver, "Error al agregar la oferta: ningún campo puede estar vacío.");
		// Rellenamos el los campos e intentamos agregar la oferta.
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "", false);
		// Comprobamos que se muestra el mensaje de error correspondiente.
		SeleniumUtils.textoPresentePagina(driver, "Error al agregar la oferta: ningún campo puede estar vacío.");
		// Rellenamos el los campos e intentamos agregar la oferta.
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "-15.0", false);
		// Comprobamos que se muestra el mensaje de error correspondiente.
		SeleniumUtils.textoPresentePagina(driver, "Error al agregar la oferta: el precio debe ser un número positivo.");
	}

	// PR017. Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas las
	// que existen para este usuario.
	@Test
	public void PR17() {
		// Iniciamos sesión.
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		// Vamos a la opción del menú de ver ofertas del usuario.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/myList");
		// Esperamos a que se cargue la página.
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", PO_View.getTimeout());
		// Comprobamos que hay tres elementos.
		assertTrue(elementos.size() == 3);
	}

	// PR18. Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que la lista se
	// actualiza y que la oferta desaparece.
	@Test
	public void PR18() {
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		// Accedemos a la vista de mis ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/myList");
		// Esperamos a que aparezca el Juguete y pinchamos en su enlace de borrado.
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "btnEliminarMyList");
		elementos.get(0).click();
		// Comprobamos que la oferta ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Juguete", PO_View.getTimeout());
	}

	// PR19. Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que la lista se actualiza
	// y que la oferta desaparece.
	@Test
	public void PR19() {
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		// Accedemos a la vista de mis ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/myList");
		// Esperamos a que aparezca los Rotuladores y pinchamos en su enlace de borrado.
		List<WebElement> elementos = PO_View.checkElement(driver, "id", "btnEliminarMyList");
		elementos.get(elementos.size() - 1).click();
		// Comprobamos que la oferta ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Rotuladores", PO_View.getTimeout());
	}

	// P20. Hacer una búsqueda con el campo vacío y comprobar que se muestra la
	// página que
	// corresponde con el listado de las ofertas existentes en el sistema
	@Test
	public void PR20() {
		PO_PrivateView.offerList(driver, "alberto@email.com", "123456");
		PO_PrivateView.searchOffer(driver, "");
		List<WebElement> elementos;
		for (int i = 0; i <= 1; i++) {
			elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", PO_View.getTimeout());
			assertTrue(elementos.size() == 5);
			driver.findElements(By.xpath("//a[contains(@class, 'page-link')]")).get(i + 1).click();
		}
		driver.findElements(By.xpath("//a[contains(@class, 'page-link')]")).get(2).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", PO_View.getTimeout());
		assertTrue(elementos.size() == 2);
	}

	// PR21. Hacer una búsqueda escribiendo en el campo un texto que no exista y
	// comprobar que se
	// muestra la página que corresponde, con la lista de ofertas vacía.
	@Test
	public void PR21() {
		PO_PrivateView.offerList(driver, "alberto@email.com", "123456");
		PO_PrivateView.searchOffer(driver, "hello");
		assertTrue(driver.findElements(By.xpath("//table[@id='tableOffers']/tbody/tr")).size() == 0);
	}

	// PR22. Hacer una búsqueda escribiendo en el campo un texto en minúscula o
	// mayúscula y
	// comprobar que se muestra la página que corresponde, con la lista de ofertas
	// que contengan
	// dicho texto, independientemente que el título esté almacenado en minúsculas o
	// mayúscula
	@Test
	public void PR22() {
		PO_PrivateView.offerList(driver, "alberto@email.com", "123456");
		PO_PrivateView.searchOffer(driver, "VES");
		assertTrue(driver.findElements(By.xpath("//table[@id='tableOffers']/tbody/tr")).size() == 1);
		// Comprobamos que la oferta ahora está en la lista de ofertas compradas.
		SeleniumUtils.textoPresentePagina(driver, "Vestido");
		SeleniumUtils.textoPresentePagina(driver, "Vestido azul");
		SeleniumUtils.textoPresentePagina(driver, "20");
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR23.Sobre una búsqueda determinada (a elección de desarrollador), comprar
	// una oferta que deja un saldo positivo en el contador del comprobador. Y
	// comprobar que el
	// contador se actualiza correctamente en la vista del comprador.
	@Test
	public void PR23() {
		PO_PrivateView.offerList(driver, "susana@email.com", "123456");
		// Buscamos el texto "vest" en el buscador.
		PO_PrivateView.searchOffer(driver, "VES");
		// Esperamos a que aparezca el Vestido y pinchamos en su enlace de Comprar.
		List<WebElement> elementos = PO_View.checkElement(driver, "free",
				"//td[contains(text(), 'Vestido')]/following-sibling::*/a[contains(@href, 'offer/buy')]");
		elementos.get(0).click();
		// Vamos a la lista de compras para ver que está comprada

		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/buyed");
		// Comprobamos que la oferta ahora está en la lista de ofertas compradas.
		SeleniumUtils.textoPresentePagina(driver, "Vestido");
		SeleniumUtils.textoPresentePagina(driver, "Vestido azul");
		SeleniumUtils.textoPresentePagina(driver, "susana@email.com");
		// Se ha restado el precio en 20 euros
		SeleniumUtils.textoPresentePagina(driver, "60");

		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR24. Sobre una búsqueda determinada (a elección de desarrollador), comprar
	// una oferta que
	// deja un saldo 0 en el contador del comprobador. Y comprobar que el contador
	// se actualiza
	// correctamente en la vista del comprador.
	@Test
	public void PR24() {
		// Vamos a la página de ofertas
		PO_PrivateView.offerList(driver, "juana@email.com", "123456");
		// Buscamos el texto "vest" en el buscador.
		PO_PrivateView.searchOffer(driver, "VES");
		// Esperamos a que aparezca el Vestido y pinchamos en su enlace de Comprar.
		List<WebElement> elementos = PO_View.checkElement(driver, "free",
				"//td[contains(text(), 'Vestido')]/following-sibling::*/a[contains(@href, 'offer/buy')]");
		elementos.get(0).click();
		// Vamos a la lista de compras para ver que está comprada

		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/buyed");
		// Comprobamos que la oferta ahora está en la lista de ofertas compradas.
		SeleniumUtils.textoPresentePagina(driver, "Vestido");
		SeleniumUtils.textoPresentePagina(driver, "Vestido azul");
		SeleniumUtils.textoPresentePagina(driver, "juana@email.com");
		// Se ha restado el precio en 20 euros y se queda en 0
		SeleniumUtils.textoPresentePagina(driver, "0");
	}

	// PR25. Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una
	// oferta que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el
	// mensaje de saldo no suficiente.
	@Test
	public void PR25() {
		// Vamos a la página de ofertas
		PO_PrivateView.offerList(driver, "alberto@email.com", "123456");
		// Buscamos el texto "vest" en el buscador.
		PO_PrivateView.searchOffer(driver, "VES");
		// Esperamos a que aparezca el Vestido y pinchamos en su enlace de Comprar.
		List<WebElement> elementos = PO_View.checkElement(driver, "free",
				"//td[contains(text(), 'Vestido')]/following-sibling::*/a[contains(@href, 'offer/buy')]");
		elementos.get(0).click();
		// Vemos que nos sale un mensaje de que no tenemos saldo para comprarlo
		SeleniumUtils.textoPresentePagina(driver, "Saldo insuficiente");
	}

	// PR26. Ir a la opción de ofertas compradas del usuario y mostrar la lista.
	// Comprobar que aparecen las ofertas que deben aparecer.
	@Test
	public void PR26() {
		// Vamos a la página de ofertas compradas
		PO_PrivateView.offerListBought(driver, "pepe@email.com", "123456");
		// Comprobamos que las ofertas compradas están en la lista.
		SeleniumUtils.textoPresentePagina(driver, "Pato");
		SeleniumUtils.textoPresentePagina(driver, "Pato de goma");
		SeleniumUtils.textoPresentePagina(driver, "alberto@email.com");
		SeleniumUtils.textoPresentePagina(driver, "Zapatos");
		SeleniumUtils.textoPresentePagina(driver, "Zapatos de baile");
		SeleniumUtils.textoPresentePagina(driver, "fernando@email.com");
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}

	// PR27. Al crear una oferta marcar dicha oferta como destacada y a continuación comprobar: i)
	// que aparece en el listado de ofertas destacadas para los usuarios y que el saldo del usuario se
	// actualiza adecuadamente en la vista del ofertante (-20).
	@Test
	public void PR27() {
		// Iniciamos sesión.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		// Obtenemos el dinero que tiene en este momento el usuario.
		String money = driver.findElement(By.id("dinero")).getText();
		// Vamos a la opción del menú de agregar oferta.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		// Rellenamos el los campos e intentamos agregar la oferta.
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "15.0", true);
		// Vamos a la vista /home.
		List<WebElement> elements = driver.findElements(By.id("home"));
		elements.get(0).click();
		// Obtenemos el dinero que tiene en este momento el usuario.
		String newmoney = driver.findElement(By.id("dinero")).getText();
		// Comprobamos que el dinero que tiene actualmente el usuario es el anterior menos 20.
		assertEquals(Double.parseDouble(money) - 20, Double.parseDouble(newmoney), 0.1);
		// Comprobamos que la oferta en cuestión se muestra en la lista de destacadas.
		PO_HomeView.checkElement(driver, "text", "Hola");
	}

	// PR028. Sobre el listado de ofertas de un usuario con más de 20 euros de saldo, pinchar en el
	// enlace Destacada y a continuación comprobar: i) que aparece en el listado de ofertas destacadas
	// para los usuarios y que el saldo del usuario se actualiza adecuadamente en la vista del ofertante (-
	// 20).
	@Test
	public void PR28() {
		// Iniciamos sesión.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		// Obtenemos el dinero que tiene en este momento el usuario.
		String money = driver.findElement(By.id("dinero")).getText();
		// Vamos a la opción del menú de lista de ofertas del usuario.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/myList");
		// Se hace click en el botón de destacar.
		List<WebElement> elements = driver.findElements(By.id("btnNormal"));
		elements.get(0).click();
		// Vamos a la vista /home.
		elements = driver.findElements(By.id("home"));
		elements.get(0).click();
		// Obtenemos el dinero que tiene en este momento el usuario.
		String newmoney = driver.findElement(By.id("dinero")).getText();
		// Comprobamos que el dinero que tiene actualmente el usuario es el anterior menos 20.
		assertEquals(Double.parseDouble(money) - 20, Double.parseDouble(newmoney), 0.1);
		// Comprobamos que la oferta en cuestión se muestra en la lista de destacadas.
		PO_HomeView.checkElement(driver, "text", "Juguete");
	}
	
	// PR029. Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
	// enlace Destacada y a continuación comprobar que se muestra el mensaje de saldo no suficiente.
	@Test
	public void PR29() {
		// Iniciamos sesión.
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		// Vamos a la opción del menú de lista de ofertas del usuario.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/myList");
		// Se hace click en el botón de destacar.
		List<WebElement> elements = driver.findElements(By.id("btnNormal"));
		elements.get(0).click();
		// Comprobamos que sale un mensaje de saldo insuficiente.
		SeleniumUtils.textoPresentePagina(driver, "Error: no dispone del suficiente saldo.");
	}

	// PR030.Inicio de sesión con datos válidos
	@Test
	public void PR30() {
		// Vamos al formulario para loguearnos
		// Rellenamos el formulario
		PO_LoginView.loginRest(driver, "pepe@email.com", "123456");
		//Espremos que cargue la página
		SeleniumUtils.EsperaCargaPagina(driver, "id", "tableOffers", 4000);
		//Miramos que estamos en la página de ofertas de la página
		SeleniumUtils.textoPresentePagina(driver, "Ofertas disponibles en la aplicación");
	}

	// PR031.Inicio de sesión con datos inválidos (email existente, pero contraseña incorrecta)
	@Test
	public void PR31() {
		PO_LoginView.loginRest(driver, "pepe@email.com", "123");
		//Miramos que sale el error
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Las", 5000);
		SeleniumUtils.textoPresentePagina(driver, "Las credenciales introducidas no son correctas");
	}

	// PR032. Inicio de sesión con datos válidos (campo email o contraseña vacíos).
	@Test
	public void PR32() {
		PO_LoginView.loginRest(driver, "", "123456");
		//Miramos que sale el error
		SeleniumUtils.EsperaCargaPagina(driver, "text", "No", 5000);
		SeleniumUtils.textoPresentePagina(driver, "No puede haber campos vacios");
	}
	
	// PR033.Mostrar el listado de ofertas disponibles y comprobar que se muestran todas las que 
	//existen, menos las del usuario identificado
	@Test
	public void PR33() {
		PO_LoginView.loginRest(driver, "alberto@email.com", "123456");
		//Miramos que estan las ofertas deseadas
		SeleniumUtils.EsperaCargaPagina(driver, "text", "Título", 300);
		List<WebElement> elementos;
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", PO_View.getTimeout());
		assertTrue(elementos.size() == 12);
		//Miramos que no estan las suyas
		SeleniumUtils.textoNoPresentePagina(driver, "Pato");
		SeleniumUtils.textoNoPresentePagina(driver, "Tienda");
		SeleniumUtils.textoNoPresentePagina(driver, "Vestido");
	}

	// PR034. Sobre una búsqueda determinada de ofertas (a elección de desarrollador), enviar un
	// mensaje a una oferta concreta. Se abriría dicha conversación por primera vez. Comprobar que el
	// mensaje aparece en el listado de mensajes.
	@Test
	public void PR34() {
		assertTrue("PR34 sin hacer", false);
	}
	
	// PR035. Sobre el listado de conversaciones enviar un mensaje a una conversación ya abierta.
	// Comprobar que el mensaje aparece en el listado de mensajes.
	@Test
	public void PR35() {
		assertTrue("PR35 sin hacer", false);
	}
	
	// PR036. Mostrar el listado de conversaciones ya abiertas. Comprobar que el listado contiene las
	// conversaciones que deben ser.
	@Test
	public void PR36() {
		assertTrue("PR36 sin hacer", false);
	}
	
	// PR037. Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la primera y
	// comprobar que el listado se actualiza correctamente.
	@Test
	public void PR37() {
		assertTrue("PR37 sin hacer", false);
	}
	
	// PR038. Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la última y
	// comprobar que el listado se actualiza correctamente.
	@Test
	public void PR38() {
		assertTrue("PR38 sin hacer", false);
	}

}
