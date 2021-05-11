package com.uniovi.tests.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.uniovi.tests.util.SeleniumUtils;


public class PO_PrivateView extends PO_NavView{
	static public void fillFormAddMark(WebDriver driver, int userOrder, String descriptionp, String scorep)
	{	
		//Espero por que se cargue el formulario de asñadir nota (Concretamente el botón class="btn")
		PO_View.checkElement(driver, "class", "btn");
		//Seleccionamos el alumnos userOrder
	    new Select (driver.findElement(By.id("user"))).selectByIndex(userOrder);
	    //Rellenemos el campo de descripción
	    WebElement description = driver.findElement(By.name("description"));
		description.clear();
		description.sendKeys(descriptionp);
		WebElement score = driver.findElement(By.name("score"));
		score.click();
		score.clear();
		score.sendKeys(scorep);
		By boton = By.className("btn");
		driver.findElement(boton).click();	
	}

	public static void userList(WebDriver driver, String nombre, String pass) {
		PO_LoginView.login(driver, nombre, pass,"https://localhost:8081/login");
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "users-menu", PO_View.getTimeout());
		elementos.get(0).click();
		elementos = SeleniumUtils.EsperaCargaPagina(driver, "@href", "/user/list", PO_View.getTimeout());
		elementos.get(0).click();

	}

	public static void logout(WebDriver driver) {
		driver.navigate().to("https://localhost:8081/logout");
		
	}

	public static void offerList(WebDriver driver, String nombre, String pass) {
		PO_LoginView.login(driver, nombre, pass,"https://localhost:8081/login");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/list");
		
	}
	public static void offerListBought(WebDriver driver, String nombre, String pass) {
		PO_LoginView.login(driver, nombre, pass,"https://localhost:8081/login");
		PO_NavView.clickDropdownMenuOption(driver, "offers-dropdown", "offers-menu", "offer/buyed");
		
	}
public static void searchOffer(WebDriver driver, String searchtext) {
		
		// Pinchamos en el buscador y escribimos el texto correspondiente.
		WebElement searchOffer = driver.findElement(By.name("busqueda"));
		searchOffer.click();
		searchOffer.clear();
		searchOffer.sendKeys(searchtext);
		
		// Pinchamos el bot�n de buscar
		WebElement searchOfferButton = driver.findElement(By.name("searchOfferButton"));
		searchOfferButton.click();
	}
	
}