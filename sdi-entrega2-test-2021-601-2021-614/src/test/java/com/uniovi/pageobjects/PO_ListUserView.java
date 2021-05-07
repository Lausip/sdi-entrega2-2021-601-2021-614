package com.uniovi.tests.pageobjects;

import org.openqa.selenium.WebDriver;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_ListUserView extends PO_NavView {

	static public void checkFields(WebDriver driver, int language) {
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("user.list.title", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("user.list.subtitle", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("delete", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("user.list.email", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("user.list.name", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("user.list.surname", language), getTimeout());
	}

	static public void checkChangeIdiom(WebDriver driver, String textIdiom1, String textIdiom2, int locale1, int locale2) {
		
		// Esperamos a que se cargue la página.
		PO_ListUserView.checkFields(driver, locale1);
		
		// Cambiamos a segundo idioma.
		PO_ListUserView.changeIdiom(driver, textIdiom2);
		
		// Comprobamos que el texto de la página haya cambiado a segundo idioma.
		PO_ListUserView.checkFields(driver, locale2);
		
		// Volvemos al primer idioma.
		PO_ListUserView.changeIdiom(driver, textIdiom1);
		
		// Esperamos a que se cargue el el texto de la página en el primer idioma.
		PO_ListUserView.checkFields(driver, locale1);

	}
	
}
