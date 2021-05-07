package com.uniovi.tests.pageobjects;

import org.openqa.selenium.WebDriver;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_NavBarView extends PO_NavView {

	static public void checkFieldsAuthenticated(WebDriver driver, int language) {
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("language.change", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("logout.message", language), getTimeout());
	}

	static public void checkChangeIdiomAuthenticated(WebDriver driver, String textIdiom1, String textIdiom2, int locale1, int locale2) {
		
		// Esperamos a que se cargue la página.
		PO_NavBarView.checkFieldsAuthenticated(driver, locale1);
		
		// Cambiamos a segundo idioma.
		PO_NavBarView.changeIdiom(driver, textIdiom2);
		
		// Comprobamos que el texto de la página haya cambiado a segundo idioma.
		PO_NavBarView.checkFieldsAuthenticated(driver, locale2);
		
		// Volvemos al primer idioma.
		PO_NavBarView.changeIdiom(driver, textIdiom1);
		
		// Esperamos a que se cargue el el texto de la página en el primer idioma.
		PO_NavBarView.checkFieldsAuthenticated(driver, locale1);

	}
	
	static public void checkFieldsNoAuthenticated(WebDriver driver, int language) {
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("language.change", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("signup.message", language), getTimeout());
		SeleniumUtils.EsperaCargaPagina(driver, "text", p.getString("login.message.nav", language), getTimeout());
	}

	static public void checkChangeIdiomNoAuthenticated(WebDriver driver, String textIdiom1, String textIdiom2, int locale1, int locale2) {
		
		// Esperamos a que se cargue la página.
		PO_NavBarView.checkFieldsNoAuthenticated(driver, locale1);
		
		// Cambiamos a segundo idioma.
		PO_NavBarView.changeIdiom(driver, textIdiom2);
		
		// Comprobamos que el texto de la página haya cambiado a segundo idioma.
		PO_NavBarView.checkFieldsNoAuthenticated(driver, locale2);
		
		// Volvemos al primer idioma.
		PO_NavBarView.changeIdiom(driver, textIdiom1);
		
		// Esperamos a que se cargue el el texto de la página en el primer idioma.
		PO_NavBarView.checkFieldsNoAuthenticated(driver, locale1);

	}
	
}
