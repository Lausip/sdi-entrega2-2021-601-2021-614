package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_AddOfferView extends PO_NavView {

	static public void fillForm(WebDriver driver, String titlep, String descriptionp, String pricep,boolean highlight) {
		WebElement title = driver.findElement(By.name("titulo"));
		title.click();
		title.clear();
		title.sendKeys(titlep);
		WebElement description = driver.findElement(By.name("description"));
		description.click();
		description.clear();
		description.sendKeys(descriptionp);
		WebElement price = driver.findElement(By.name("price"));
		price.click();
		price.clear();
		price.sendKeys(pricep);
		if(highlight) {
		WebElement h = driver.findElement(By.name("highlight"));
		h.click();
		}
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
	
	static public void checkFields(WebDriver driver, int language) {
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("offer.add.titulo", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("offer.add.description", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("offer.add.price", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("offer.add.highlight", language), getTimeout());
	}

	static public void checkChangeIdiom(WebDriver driver, String textIdiom1, String textIdiom2, int locale1, int locale2) {
		
		// Esperamos a que se cargue la página.
		PO_AddOfferView.checkFields(driver, locale1);
		
		// Cambiamos a segundo idioma.
		PO_AddOfferView.changeIdiom(driver, textIdiom2);
		
		// Comprobamos que el texto de la página haya cambiado a segundo idioma.
		PO_AddOfferView.checkFields(driver, locale2);
		
		// Volvemos al primer idioma.
		PO_AddOfferView.changeIdiom(driver, textIdiom1);
		
		// Esperamos a que se cargue el el texto de la página en el primer idioma.
		PO_AddOfferView.checkFields(driver, locale1);

	}


}