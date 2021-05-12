package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_AddOfferView extends PO_NavView {

	static public void fillForm(WebDriver driver, String titlep, String detallep, String pricep,
			boolean highlight) {
		WebElement title = driver.findElement(By.name("titulo"));
		title.click();
		title.clear();
		title.sendKeys(titlep);
		WebElement detalle = driver.findElement(By.name("detalle"));
		detalle.click();
		detalle.clear();
		detalle.sendKeys(detallep);
		WebElement price = driver.findElement(By.name("precio"));
		price.click();
		price.clear();
		price.sendKeys(pricep);
		if (highlight) {
			WebElement h = driver.findElement(By.name("destacada"));
			h.click();
		}
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}

}