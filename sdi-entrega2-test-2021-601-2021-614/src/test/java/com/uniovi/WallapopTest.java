package com.uniovi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.uniovi.entities.Chat;
import com.uniovi.entities.Message;
import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.repositories.ChatsRepository;
import com.uniovi.repositories.MessagesRepository;
import com.uniovi.repositories.OffersRepository;
import com.uniovi.repositories.UsersRepository;
import com.uniovi.services.ChatsService;
import com.uniovi.services.MessagesService;
import com.uniovi.services.OffersService;
import com.uniovi.services.RolesService;
import com.uniovi.services.UsersService;
import com.uniovi.tests.pageobjects.PO_AddOfferView;
import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_ListUserView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_NavBarView;
import com.uniovi.tests.pageobjects.PO_NavView;
import com.uniovi.tests.pageobjects.PO_PrivateView;
import com.uniovi.tests.pageobjects.PO_Properties;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.util.SeleniumUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class WallapopTest {

	// Parámetros de Laura
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "C:\\Users\\laura\\Escritorio\\Uni\\3-Uni\\2Semestre\\SDI\\LAB\\Sesion05\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	
	// Parámetros de Rut
	//static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
//	static String Geckdriver024 = "C:\\Users\\rualg\\OneDrive\\Escritorio\\SDI\\Práctica5\\PL-SDI-Sesión5-material\\PL-SDI-Sesión5-material\\geckodriver024win64.exe";
	
	// Común a Windows y a MACOSX
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "http://localhost:8090";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	} /* Resto del código de la clase */

	// Antes de cada prueba se navega al URL home de la aplicación
	@Before()
	public void setUp() {
		initDB();
		driver.navigate().to(URL);
	}
	
	// Después de cada prueba se borran las cookies del navegador
	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	// Antes de la primera prueba
	@BeforeClass
	static public void begin() {
	}

	// Al finalizar la última prueba
	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}
	
	// Registro de usuario con datos válidos.
	@Test
	public void PR01() {
		
		//Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		
		//Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "prueba1@example.com", "Juan", "Pérez", "123456", "123456");
		
		//Comprobamos que entramos en la sección privada
		PO_View.checkElement(driver, "text", "prueba1@example.com");
		
	}
	
	// Registro de usuario con datos inválidos (email vacío, nombre vacío, apellidos vacíos).
	@Test
	public void PR02() {
		
		//Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		
		//Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "", "", "", "123456", "123456");
		
		//Comprobamos el error de email vacío.
		PO_RegisterView.checkKey(driver, "Error.empty.email", PO_Properties.getSPANISH());
		
		//Comprobamos el error de nombre vacío.
		PO_RegisterView.checkKey(driver, "Error.empty.name", PO_Properties.getSPANISH());
		
		//Comprobamos el error de apellidos vacío.
		PO_RegisterView.checkKey(driver, "Error.empty.lastName", PO_Properties.getSPANISH());
		
	}
	
	// Registro de usuario con datos inválidos (repetición de contraseña inválida).
	@Test
	public void PR03() {
		
		//Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		
		//Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "prueba3@example.com", "Pepe", "Álvarez", "123456", "123457");
		
		//Comprobamos el error de repetición de contraseña inválida.
		PO_RegisterView.checkKey(driver, "Error.signup.passwordConfirm.coincidence", PO_Properties.getSPANISH());
		
	}
	
	// Registro de usuario con datos inválidos (email existente).
	@Test
	public void PR04() {
		
		//Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", "class", "btn btn-primary");
		
		//Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "pepe@email.com", "Francisco", "López", "123456", "123456");
		
		//Comprobamos el error de repetición de contraseña inválida.
		PO_RegisterView.checkKey(driver, "Error.signup.email.duplicate", PO_Properties.getSPANISH());
		
	}

	// Inicio de sesión con datos válidos (administrador).
	@Test
	public void PR05() {
		PO_PrivateView.login(driver, "admin@email.com", "admin");
		SeleniumUtils.textoPresentePagina(driver, "Usuario autenticado como");
	}

	// Inicio de sesión con datos válidos (usuario estándar).
	@Test
	public void PR06() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		SeleniumUtils.textoPresentePagina(driver, "Su saldo es de");
		
	}

	// Inicio de sesión con datos inválidos (usuario estándar, campo email y
	// contraseña vacíos)
	@Test
	public void PR07() {
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "", "123456");
		SeleniumUtils.textoPresentePagina(driver, "Identifícate");
		PO_LoginView.fillForm(driver, "Jose@gmail.com", " ");
		SeleniumUtils.textoPresentePagina(driver, "Identifícate");
		PO_LoginView.fillForm(driver, " ", " ");
		SeleniumUtils.textoPresentePagina(driver, "Identifícate");
	}

	// Inicio de sesión con datos válidos (usuario estándar, email existente, pero
	// contraseña incorrecta).
	@Test
	public void PR08() {
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "Jose@gmail.com", "123");
		PO_RegisterView.checkKey(driver, "Error.login.error", PO_Properties.getSPANISH());
	}

	// Inicio de sesión con datos inválidos (usuario estándar, email no existente en
	// la aplicación).
	@Test
	public void PR09() {
		PO_HomeView.clickOption(driver, "login", "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "Joselito@gmail.com", "123456");
		PO_RegisterView.checkKey(driver, "Error.login.error", PO_Properties.getSPANISH());
	}
	
	// Hacer click en la opción de salir de sesión y comprobar que se redirige a la página de inicio
	// de sesión (Login).
	@Test
	public void PR10() {
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		PO_PrivateView.logout(driver);
		SeleniumUtils.textoPresentePagina(driver, "Identifícate");
	}
	// Comprobar que el botón cerrar sesión no está visible si el usuario no está
	// autenticado.
	@Test
	public void PR11() {
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		PO_PrivateView.logout(driver);
		SeleniumUtils.textoNoPresentePagina(driver, "Desconectar");
	}
	
	// Mostrar el listado de usuarios y comprobar que se muestran todos los que
	// existen en el sistema.
	@Test
	public void PR12() {
		PO_PrivateView.login(driver, "admin@email.com", "admin");
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "users-menu", PO_View.getTimeout());
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "@href", "/user/list", PO_View.getTimeout());
		elementos.get(0).click();
		List<WebElement> elementos2 = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr",
				PO_View.getTimeout());
		assertTrue(elementos2.size() ==6);

	}
	
	/* Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar
	 * que la lista se actualiza y que el usuario desaparece.
	 */
	@Test
	public void PR13() {
		
		// Iniciamos sesión como el usuario administrador.
		PO_PrivateView.login(driver, "admin@email.com", "admin");
		
		// Accedemos a la vista de usuarios.
		PO_NavView.clickDropdownMenuOption(driver, "users-dropdown", "users-menu", "user/list");
		
		// Esperamos a que se muestre hasta el último usuario.
		List<WebElement> elementos = PO_View.checkElement(driver, "text", "García del Monte");
		
		// Seleccionamos el checkbox del primero los usuarios de la lista.
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
	
	/*
	 * Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar
	 * que la lista se actualiza y que el usuario desaparece.
	 */
	@Test
	public void PR14() {
		
		// Iniciamos sesión como el usuario administrador.
		PO_PrivateView.login(driver, "admin@email.com", "admin");
		
		// Accedemos a la vista de usuarios.
		PO_NavView.clickDropdownMenuOption(driver, "users-dropdown", "users-menu", "user/list");
		
		// Esperamos a que se muestre hasta el último usuario.
		List<WebElement> elementos = PO_View.checkElement(driver, "text", "García del Monte");
		
		// Seleccionamos el checkbox del primero los usuarios de la lista.
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
	
	/*
	 * Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se
	 * actualiza y que los usuarios desaparecen.
	 */
	@Test
	public void PR15() {
		
		// Iniciamos sesión como el usuario administrador.
		PO_PrivateView.login(driver, "admin@email.com", "admin");
		
		// Accedemos a la vista de usuarios.
		PO_NavView.clickDropdownMenuOption(driver, "users-dropdown", "users-menu", "user/list");
		
		// Esperamos a que se muestre hasta el último usuario.
		List<WebElement> elementos = PO_View.checkElement(driver, "text", "García del Monte");
		
		// Seleccionamos el checkbox del primero los usuarios de la lista.
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
	
	//Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el botón Submit. 
	//Comprobar que la oferta sale en el listado de ofertas de dicho usuario.
	@Test
	public void PR16() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "15.0",false);
		PO_HomeView.checkElement(driver, "text", "Hola");
	}
	
	//Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo título vacío) y pulsar 
	//el botón Submit. Comprobar que se muestra el mensaje de campo obligatorio
	@Test
	public void PR17() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		PO_AddOfferView.fillForm(driver, "", "Hola esto es una prueba", "15.0",false);
		PO_RegisterView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());
		PO_AddOfferView.fillForm(driver, "Hola", " ", "46.0",false);
		PO_RegisterView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", " ",false);
		PO_RegisterView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());
	}
	//	Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran todas los que
	// existen para este usuario.
	@Test
	public void PR18() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/mylist");
		List<WebElement> elementos2 = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr",
				PO_View.getTimeout());
		assertTrue(elementos2.size() ==3);
	
	}
	
	/* 
	 * Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar
	 * que la lista se actualiza y que la oferta desaparece.
	 */
	@Test
	public void PR19() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		
		// Accedemos a la vista de mis ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/mylist");
		
		// Esperamos a que se muestren los botones de paginación.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Vamos a la primera página.
		elementos.get(1).click();
		
		// Esperamos a que aparezca el Juguete y pinchamos en su enlace de borrado.
		elementos = PO_View.checkElement(driver, "id", "deleteBtn");
		elementos.get(0).click();
		
		// Esperamos a que vuelva a cargar la página.
		elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Vamos a la primera página.
		elementos.get(1).click();
		
		// Comprobamos que la oferta ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Juguete", PO_View.getTimeout());
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}
	
	/* 
	 * Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar
	 * que la lista se actualiza y que la oferta desaparece.
	 */
	@Test
	public void PR20() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		
		// Accedemos a la vista de mis ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/mylist");
		
		// Esperamos a que se muestren los botones de paginación.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Comprobamos que estamos en la primera página.
		elementos.get(1).click();
		
		// Esperamos a que aparezca el Rotuladores y pinchamos en su enlace de borrado.
		elementos = PO_View.checkElement(driver, "free", "//td[contains(text(), 'Rotuladores')]/following-sibling::*/a[contains(@href, 'offer/delete')]");
		elementos.get(0).click();
		
		// Esperamos a que vuelva a cargar la página.
		elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Comprobamos que estamos en la primera página.
		elementos.get(1).click();
		
		// Comprobamos que la oferta ya no está en la lista.
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Rotuladores", PO_View.getTimeout());
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
	}
	
	//	Hacer una búsqueda con el campo vacío y comprobar que se muestra la página que
	//	corresponde con el listado de las ofertas existentes en el sistema
	@Test
	public void PR21() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/list");
		WebElement buscar = driver.findElement(By.name("searchText"));
		buscar.click();
		buscar.clear();
		buscar.sendKeys("");
		buscar.click();
		List<WebElement> elementos;
		for(int i=0;i<=2;i++){
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", PO_View.getTimeout());
		assertTrue(elementos.size() == 5);
		driver.findElements(By.xpath("//a[contains(@class, 'page-link')]")).get(i+1).click();
		}
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", PO_View.getTimeout());
		assertTrue(elementos.size() == 2);

	}
	//Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar que se 
	//muestra la página que corresponde, con la lista de ofertas vacía.
	@Test
	public void PR22() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/list");
		WebElement buscar = driver.findElement(By.name("searchText"));
		buscar.click();
		buscar.clear();
		buscar.sendKeys("hello");
		driver.findElement(By.className("btn")).click();
		assertTrue(driver.findElements(By.xpath("//table[@id='tableOffers']/tbody/tr")).size() == 0);

	}
	
	/*
	 * Sobre una búsqueda determinada (a elección del desarrollador), comprar una
	 * oferta que deja un saldo positivo en el contador del comprador. Comprobar
	 * que el contador se actualiza correctamente en la vista del comprador.
	 */
	@Test
	public void PR23() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		
		// Accedemos a la vista de ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/list");
		
		// Esperamos a que vuelva a cargar la página.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Buscamos el texto "vest" en el buscador.
		PO_PrivateView.searchOffer(driver, "vest");
		
		// Esperamos a que aparezca el Vestido y pinchamos en su enlace de Comprar.
		elementos = PO_View.checkElement(driver, "free", "//td[contains(text(), 'Vestido')]/following-sibling::*/a[contains(@href, 'offer/purchase')]");
		elementos.get(0).click();
		
		// Comprobamos que la oferta ahora está en la lista de ofertas compradas.
		SeleniumUtils.textoPresentePagina(driver, "Vestido");
		SeleniumUtils.textoPresentePagina(driver, "Vestido azul");
		SeleniumUtils.textoPresentePagina(driver, "alberto@email.com");
		SeleniumUtils.textoPresentePagina(driver, "80.0");
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
		
	}
	
	/*
	 * Sobre una búsqueda determinada (a elección del desarrollador), comprar una
	 * oferta que deja un saldo 0 en el contador del comprador. Comprobar que el
	 * contador se actualiza correctamente en la vista del comprador.
	 */
	@Test
	public void PR24() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "juana@email.com", "123456");
		
		// Accedemos a la vista de ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/list");
		
		// Esperamos a que vuelva a cargar la página.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Buscamos el texto "vest" en el buscador.
		PO_PrivateView.searchOffer(driver, "vest");
		
		// Esperamos a que aparezca el Vestido y pinchamos en su enlace de Comprar.
		elementos = PO_View.checkElement(driver, "free", "//td[contains(text(), 'Vestido')]/following-sibling::*/a[contains(@href, 'offer/purchase')]");
		elementos.get(0).click();
		
		// Comprobamos que la oferta ahora está en la lista de ofertas compradas.
		SeleniumUtils.textoPresentePagina(driver, "Vestido");
		SeleniumUtils.textoPresentePagina(driver, "Vestido azul");
		SeleniumUtils.textoPresentePagina(driver, "alberto@email.com");
		SeleniumUtils.textoPresentePagina(driver, "0.0");
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
		
	}
	
	/*
	 * Sobre una búsqueda determinada (a elección del desarrollador), intentar
	 * comprar una oferta que esté por encima de saldo disponible del comprador.
	 * Y comprobar que se muestra el mensaje de saldo no suficiente.
	 */
	@Test
	public void PR25() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		
		// Accedemos a la vista de ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/list");
		
		// Esperamos a que vuelva a cargar la página.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Buscamos el texto "vest" en el buscador.
		PO_PrivateView.searchOffer(driver, "vest");
		
		// Esperamos a que aparezca el Vestido y pinchamos en su enlace de Comprar.
		elementos = PO_View.checkElement(driver, "free", "//td[contains(text(), 'Vestido')]/following-sibling::*/a[contains(@href, 'offer/purchase')]");
		elementos.get(0).click();
		
		// Comprobamos que sale un mensaje de error dieciendo que el usuario no tiene saldo suficiente.
		SeleniumUtils.textoPresentePagina(driver, "La compra no se puede realizar, ya que no dispone del saldo necesario.");
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
		
	}
	
	/*
	 * Ir a la opción de ofertas compradas del usuario y mostrar la lista.
	 * Comprobar que aparecen las ofertas que deben aparecer.
	 */
	@Test
	public void PR26() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		
		// Accedemos a la vista de ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/purchasedlist");
		
		// Esperamos a que se muestre los botones de paginación.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//a[contains(@class, 'page-link')]");
		
		// Comprobamos que estamos en la primera página.
		elementos.get(1).click();
		
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
	
	/*
	 * Visualizar al menos cuatro páginas haciendo el cambio español/inglés/español
	 * (comprobando que algunas de las etiquetas cambian al idioma correspondiente).
	 * Página principal/Opciones principales de usuario/Listado de usuarios /Vista
	 * de alta de oferta.
	 */
	@Test
	public void PR27() {
		
		// Comprobamos la Página principal
		PO_HomeView.checkChangeIdiom(driver, "btnSpanish", "btnEnglish", PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
		
		// Comprobamos las Opciones principales de usuario
		PO_NavBarView.checkChangeIdiomNoAuthenticated(driver, "btnSpanish", "btnEnglish", PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
		
		// Iniciamos sesión como el usuario administrador.
		PO_PrivateView.login(driver, "admin@email.com", "admin");
		
		// Comprobamos las Opciones principales de usuario
		PO_NavBarView.checkChangeIdiomAuthenticated(driver, "btnSpanish", "btnEnglish", PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
		
		// Accedemos a la vista de ver el listado de usuarios.
		PO_NavView.clickDropdownMenuOption(driver, "users-dropdown", "users-menu", "user/list");
		
		// Comprobamos el Listado de usuarios
		PO_ListUserView.checkChangeIdiom(driver, "btnSpanish", "btnEnglish", PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
		
		// Nos desconectamos.
		PO_PrivateView.logout(driver);
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		
		// Accedemos a la vista de agregar ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		
		// Comprobamos la Vista de alta de oferta
		PO_AddOfferView.checkChangeIdiom(driver, "btnSpanish", "btnEnglish", PO_Properties.getSPANISH(), PO_Properties.getENGLISH());
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
		
	}
	
	//	Intentar acceder sin estar autenticado a la opción de listado de usuarios del administrador. Se
	//	deberá volver al formulario de login
	@Test
	public void PR28() {
		driver.get(URL + "/user/list");
		PO_View.checkElement(driver, "text", "Identifícate");
	}
	
	//	Intentar acceder sin estar autenticado a la opción de listado de ofertas propias de un usuario
	//	estándar. Se deberá volver al formulario de login.
	@Test
	public void PR29() {
		driver.get(URL + "/offer/mylist");
		PO_View.checkElement(driver, "text", "Identifícate");
	}
	
	//	Estando autenticado como usuario estándar intentar acceder a la opción de listado de
	//	usuarios del administrador. Se deberá indicar un mensaje de acción prohibida.
	@Test
	public void PR30() {
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		driver.get(URL + "/user/list");
		SeleniumUtils.textoPresentePagina(driver, "HTTP Status 403 – Forbidden");
	}
	
	/*
	 * Mostrar el listado de conversaciones ya abiertas. Comprobar que
	 * el listado contiene las conversaciones que deben ser.
	 */
	@Test
	public void PR33() {
		
		// Iniciamos sesión como el usuario pepe.
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		
		// Accedemos a la vista de ofertas.
		PO_NavView.clickDropdownMenuOption(driver, "chats-dropdown", "chats-menu", "chat/list");
		
		// Comprobamos que los chats están en la lista.
		SeleniumUtils.textoPresentePagina(driver, "Juguete");
		SeleniumUtils.textoPresentePagina(driver, "Patinete");
		SeleniumUtils.textoPresentePagina(driver, "Rotuladores");
		SeleniumUtils.textoPresentePagina(driver, "Pato");
		SeleniumUtils.textoPresentePagina(driver, "Zapatos");
		SeleniumUtils.textoPresentePagina(driver, "alberto@email.com");
		SeleniumUtils.textoPresentePagina(driver, "fernando@email.com");
		
		// Finalmente, nos desconectamos.
		PO_PrivateView.logout(driver);
		
	}
	
	//Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la primera y
	//comprobar que el listado se actualiza correctamente.
	@Test
	public void PR34() {
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "chats-menu", "chats-menu", "/chat/list");
		PO_View.getTimeout();
		List<WebElement> hh = driver.findElements(By.id("deleteSellerBtn"));
		hh.get(0).click();
		List<WebElement> elementos2 = SeleniumUtils.EsperaCargaPagina(driver, "id", "tabletrChatsAsSeller", PO_View.getTimeout());
		assertTrue(elementos2.size() ==2);
	}
	
		//Sobre el listado de conversaciones ya abiertas. Pinchar el enlace Eliminar de la última y
		//comprobar que el listado se actualiza correctamente.
		@Test
		public void PR35() {
			PO_PrivateView.login(driver, "pepe@email.com", "123456");
			PO_NavView.clickDropdownMenuOption(driver, "chats-menu", "chats-menu", "/chat/list");
			List<WebElement> hh = driver.findElements(By.id("deleteInterestedBtn"));
			hh.get(1).click();
			List<WebElement> elementos2 = SeleniumUtils.EsperaCargaPagina(driver, "id", "tableChatsAsInterested",
					PO_View.getTimeout());
			assertTrue(elementos2.size() ==1);
		}
	
	//Al crear una oferta marcar dicha oferta como destacada y a continuación comprobar: i) que
	//aparece en el listado de ofertas destacadas para los usuarios y que el saldo del usuario se actualiza
	//adecuadamente en la vista del ofertante (-20).
	@Test
	public void PR36() {
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		String money = driver.findElement(By.id("money")).getText();
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "15.0",true);
		String newMoney =  driver.findElement(By.id("money")).getText();
		assertEquals(Double.parseDouble(money) - 20, Double.parseDouble(newMoney), 0.1);
		driver.get(URL + "/home");
		PO_PrivateView.logout(driver);
		PO_PrivateView.login(driver, "fernando@email.com", "123456");
		PO_HomeView.checkElement(driver, "text", "Hola");
	}

	//Sobre el listado de ofertas de un usuario con mas de 20 euros de saldo, pinchar en el
	//enlace Destacada y a continuación comprobar: i) que aparece en el listado de ofertas destacadas para los
	//usuarios y que el saldo del usuario se actualiza adecuadamente en la vista del ofertante (-20).
	@Test
	public void PR37() {
		PO_PrivateView.login(driver, "pepe@email.com", "123456");
		String money = driver.findElement(By.id("money")).getText();
		driver.get(URL + "/home");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/mylist");
		List<WebElement> hh = driver.findElements(By.id("highlightBtn"));
		hh.get(0).click();
		String newmoney = driver.findElement(By.id("money")).getText();
		assertEquals(Double.parseDouble(money) - 20,Double.parseDouble(newmoney), 0.1);
		driver.get(URL + "/home");
		PO_PrivateView.logout(driver);
		PO_PrivateView.login(driver, "fernando@email.com", "123456");
		PO_HomeView.checkElement(driver, "text", "Juguete");
	}
	
	//Sobre el listado de ofertas de un usuario con menos de 20 euros de saldo, pinchar en el
	//enlace Destacada y a continuación comprobar que se muestra el mensaje de saldo no suficiente.
	@Test
	public void PR38() {
		PO_PrivateView.login(driver, "alberto@email.com", "123456");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/add");
		PO_AddOfferView.fillForm(driver, "Hola", "Hola esto es una prueba", "15.0",false);
		driver.get(URL + "/home");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/mylist");
		List<WebElement> hh = driver.findElements(By.id("highlightBtn"));
		hh.get(0).click();
		PO_RegisterView.checkKey(driver, "Error.highlight.insuficient", PO_Properties.getSPANISH());
	}
	
	
	/*
	 * Inicialización de la BD
	 */
	@Autowired
	private RolesService rolesService;
	
	@Autowired
	private UsersService usersService;

	@Autowired
	private OffersService offersService;
	
	@Autowired
	private ChatsService chatsService;
	
	@Autowired
	private MessagesService messagesService;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private OffersRepository offersRepository;
	
	@Autowired
	private ChatsRepository chatsRepository;
	
	@Autowired
	private MessagesRepository messagesRepository;
	
	public void initDB() {
		
		// Borramos todas las entidades.
		messagesRepository.deleteAll();
		chatsRepository.deleteAll();
		offersRepository.deleteAll();
		usersRepository.deleteAll();
		
		// Creamos de nuevo todas las entidades.
		
		// Añadir usuarios
		
		User user1 = new User("admin@email.com", "Admin", "Admin");
		user1.setPassword("admin");
		user1.setRole(rolesService.getRoles()[1]);
		usersService.addUser(user1);
		
		User user2 = new User("pepe@email.com", "Pepe", "Álvarez Pérez");
		user2.setPassword("123456");
		user2.setRole(rolesService.getRoles()[0]);
		user2.setMoney(100.0);
		usersService.addUser(user2);
		
		User user3 = new User("juana@email.com", "Juana", "Fernández");
		user3.setPassword("123456");
		user3.setRole(rolesService.getRoles()[0]);
		user3.setMoney(20.0);
		usersService.addUser(user3);
		
		User user4 = new User("alberto@email.com", "Alberto", "Rodríguez Álvarez");
		user4.setPassword("123456");
		user4.setRole(rolesService.getRoles()[0]);
		user4.setMoney(0.0);
		usersService.addUser(user4);
		
		User user5 = new User("susana@email.com", "Susana", "González Almonte");
		user5.setPassword("123456");
		user5.setRole(rolesService.getRoles()[0]);
		user5.setMoney(80.0);
		usersService.addUser(user5);
		
		User user6 = new User("fernando@email.com", "Fernando Manuel", "García del Monte");
		user6.setPassword("123456");
		user6.setRole(rolesService.getRoles()[0]);
		user6.setMoney(10.0);
		usersService.addUser(user6);
		
		
		// Añadir ofertas
		
		// Ofertas user2
		Offer offer = new Offer("Juguete", "Juguete infantil", 10.0, user2,false);
		offersService.addOffer(offer);
		Chat chat = new Chat(user2, user4, offer);
		chatsService.addChat(chat);
		Message message = new Message("Hola, soy Alberto, el precio es negociable?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, cuánto me ofreces?", user2, chat);
		messagesService.addMessage(message);
		message = new Message("8€, qué te parece?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Mucho mejor, creo que me lo voy a quedar, me lo pensaré", user2, chat);
		messagesService.addMessage(message);
		
		offer = new Offer("Patinete", "Patinete eléctrico", 280.0, user2,false);
		offersService.addOffer(offer);
		chat = new Chat(user2, user4, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, soy Alberto, cuánta potencia tiene el patinete?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Buenas tardes, no recuerdo cuánta es, pero va muy bien", user2, chat);
		messagesService.addMessage(message);
		message = new Message("No sé...", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Sube muy bien las cuestas incluso", user2, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user3);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Rotuladores", "Rotuladores de colores", 12.0, user2,false);
		offersService.addOffer(offer);
		chat = new Chat(user2, user6, offer);
		chatsService.addChat(chat);
		message = new Message("Buenas tardes, cuántos rotuladores vienen?", user6, chat);
		messagesService.addMessage(message);
		message = new Message("Buenas, vienen 12", user2, chat);
		messagesService.addMessage(message);
		message = new Message("No parecen muy buenos, son de marca?", user6, chat);
		messagesService.addMessage(message);
		message = new Message("Son de marca blanca, pero pintan muy bien", user2, chat);
		messagesService.addMessage(message);
		
		
		// Ofertas user3
		offer = new Offer("Raqueta", "Raqueta de tenis", 20.0, user3,false);
		offersService.addOffer(offer);
		chat = new Chat(user3, user6, offer);
		chatsService.addChat(chat);
		message = new Message("Buenas tardes, de qué tipo es la raqueta?", user6, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, es profesional", user3, chat);
		messagesService.addMessage(message);
		message = new Message("Ah, pues entonces no me interesa, estoy empezando", user6, chat);
		messagesService.addMessage(message);
		message = new Message("Sin problema", user3, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user6);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Saco", "Saco de dormir", 11.0, user3,false);
		offersService.addOffer(offer);
		chat = new Chat(user3, user5, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, me interesa el saco, pero me preocupa que no sea lo suficientemente cálido", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Buenas tardes, es para 12 grados", user3, chat);
		messagesService.addMessage(message);
		message = new Message("Ah, pues me viene prefecto", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Genial", user3, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user5);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Balón", "Balón de baloncesto", 10.0, user3,false);
		offersService.addOffer(offer);
		chat = new Chat(user3, user5, offer);
		chatsService.addChat(chat);
		message = new Message("Buenas tardes, viene inflado?", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Ahora mismo no lo está, pero puedo hacerlo...", user3, chat);
		messagesService.addMessage(message);
		message = new Message("Es que no tengo máquina y mi hijo quiere jugar ya", user5, chat);
		messagesService.addMessage(message);
		message = new Message("No sería un problema :)", user3, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user5);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		
		// Ofertas user4
		offer = new Offer("Pato", "Pato de goma", 3.0, user4,true);
		offersService.addOffer(offer);
		chat = new Chat(user4, user2, offer);
		chatsService.addChat(chat);
		message = new Message("Hola!", user2, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Hola! Dígame", user4, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("El pato es amarillo?", user2, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Sí", user4, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user2);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Vestido", "Vestido azul", 20.0, user4,false);
		offersService.addOffer(offer);
		chat = new Chat(user4, user3, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, me interesa el vestido", user3, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Y qué necesitas saber?", user4, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Qué tono tiene el vestido?", user3, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Marino", user4, chat);
		messagesService.addMessage(message);
		
		offer = new Offer("Tienda", "Tienda de campaña", 80.0, user4,false);
		offersService.addOffer(offer);
		chat = new Chat(user4, user6, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, qué tamaño tiene?", user6, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Es de 4 x 5", user4, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Ah, así que es espaciosa", user6, chat);
		messagesService.addMessage(message);
		chatsService.addChat(chat);
		message = new Message("Exactamente, y de muy buena calidad", user4, chat);
		messagesService.addMessage(message);
		
		
		// Ofertas user5
		offer = new Offer("Caja", "Caja de cartón azul", 5.0, user5,false);
		offersService.addOffer(offer);
		chat = new Chat(user5, user4, offer);
		chatsService.addChat(chat);
		message = new Message("Buenos días, cuál es la dimensión de la caja?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, mide 30 x 30 x 30 cm", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Ah, vaya, es más grande de lo que me esperaba, no me interesa, disculpe", user4, chat);
		messagesService.addMessage(message);
		message = new Message("No se preocupe", user5, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user6);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Bicicleta", "Bicicleta infantil", 70.0, user5,false);
		offersService.addOffer(offer);
		chat = new Chat(user5, user3, offer);
		chatsService.addChat(chat);
		message = new Message("Buenas tardes, tengo cierto interés en la bici", user3, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, tienes alguna duda?", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Parece que le falta la pintura en algunos sitios", user3, chat);
		messagesService.addMessage(message);
		message = new Message("Una poquita sí, pero por lo demás está como nueva", user5, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user3);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Folios", "Paquete de 500 folios", 4.0, user5,false);
		offersService.addOffer(offer);
		chat = new Chat(user5, user4, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, el paquete está completo?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, le faltan algunos, pero muy pocos", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Cuántos son muy pocos?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Entorno a 20...", user5, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user4);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		
		// Ofertas user6
		offer = new Offer("Zapatos", "Zapatos de baile", 11.0, user6,false);
		offersService.addOffer(offer);
		chat = new Chat(user6, user2, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, estoy interesado", user2, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, cuál es tu duda?", user6, chat);
		messagesService.addMessage(message);
		message = new Message("Los zapatos parecen arañados", user2, chat);
		messagesService.addMessage(message);
		message = new Message("Para nada, es su impresión, están en perfecto estado", user6, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user2);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Sandalias", "Sandalias de plataforma", 15.0, user6,false);
		offersService.addOffer(offer);
		chat = new Chat(user6, user4, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, cuánto tacón tienen?", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, son unos 9 cm", user6, chat);
		messagesService.addMessage(message);
		message = new Message("Uf, son muy altos", user4, chat);
		messagesService.addMessage(message);
		message = new Message("Pero muy cómodos, te los recomiendo ;)", user6, chat);
		messagesService.addMessage(message);
		offer.setPurchaser(user4);
		offer.setPurchased(true);
		offersService.addOffer(offer);
		
		offer = new Offer("Boli", "Boli azul", 2.0, user6,false);
		offersService.addOffer(offer);
		chat = new Chat(user6, user5, offer);
		chatsService.addChat(chat);
		message = new Message("Hola, en la foto parece que no tiene tinta", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Hola, sí que tiene, lo que pasa es que es de tinta líquida", user6, chat);
		messagesService.addMessage(message);
		message = new Message("No sé, no me fío, lo siento", user5, chat);
		messagesService.addMessage(message);
		message = new Message("Pues entonces para qué me escribes?", user6, chat);
		messagesService.addMessage(message);
		
	}


}
