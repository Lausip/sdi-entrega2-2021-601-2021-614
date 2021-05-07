package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.uniovi.tests.util.SeleniumUtils;



public class PO_PrivateView extends PO_NavView {
	
	public static void login(WebDriver driver, String dni, String password) {
		// Vamos al formulario de logueo.
		clickOption(driver, "login", "class", "btn btn-primary");
		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, dni, password);
		// Cpmprobamos que entramos en la pagina privada
		checkElement(driver, "text", dni);
		SeleniumUtils.esperarSegundos(driver, 1);
	}
	
	public static void logout(WebDriver driver) {
		clickOption(driver, "logout", "text", "Identifícate");
	}
	
	public static void searchOffer(WebDriver driver, String searchtext) {
		
		// Pinchamos en el buscador y escribimos el texto correspondiente.
		WebElement searchOffer = driver.findElement(By.name("searchText"));
		searchOffer.click();
		searchOffer.clear();
		searchOffer.sendKeys(searchtext);
		
		// Pinchamos el botón de buscar
		WebElement searchOfferButton = driver.findElement(By.name("searchOfferButton"));
		searchOfferButton.click();
	}
	
}
