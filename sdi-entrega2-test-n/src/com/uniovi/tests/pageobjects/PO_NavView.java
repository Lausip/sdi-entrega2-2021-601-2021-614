package com.uniovi.tests.pageobjects;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_NavView  extends PO_View{

	/**
	 * CLicka una de las opciones principales (a href) y comprueba que se vaya a la vista con el elemento de tipo type con el texto Destino
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param textOption: Texto de la opci√≥n principal.
	 * @param criterio: "id" or "class" or "text" or "@attribute" or "free". Si el valor de criterio es free es una expresion xpath completa. 
	 * @param textoDestino: texto correspondiente a la b√∫squeda de la p√°gina destino.
	 */
	public static void clickOption(WebDriver driver, String textOption, String criterio, String textoDestino) {
		//CLickamos en la opci√≥n de registro y esperamos a que se cargue el enlace de Registro.
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "@href", textOption, getTimeout());
		//Tiene que haber un s√≥lo elemento.
		assertTrue(elementos.size()==1);
		//Ahora lo clickamos
		elementos.get(0).click();
		//Esperamos a que sea visible un elemento concreto
		elementos = SeleniumUtils.EsperaCargaPagina(driver, criterio, textoDestino, getTimeout());
		//Tiene que haber un s√≥lo elemento.
		assertTrue(elementos.size()==1);	
	}

	/**
	 * Simula el click en una opcion de menu
	 * @param driver
	 * @param menuButtonId es el id del boton que despliega el menu dropdown
	 * @param menuId es el id del menu dropdown
	 * @param optionId es el enlace la opcion del menu dropdown sobre la que quieres hacer click
	 */
	public static void clickDropdownMenuOption(WebDriver driver, String menuButtonId, String menuId, String optionId) {
		// Pinchamos en la opciÛn de gestiÛn de usuarios del men˙.
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id, "+"'"+menuId+"'"+")]/a");
		elementos.get(0).click();
		
		// Pinchamos en la opciÛn de lista de usuarios.
		elementos = PO_View.checkElement(driver, "free", "//a[contains(@href,"+"'"+optionId+"'"+")]");
		elementos.get(0).click();
	}



}
