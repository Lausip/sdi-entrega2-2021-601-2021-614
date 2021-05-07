package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PO_LoginView extends PO_NavView {

	public static void fillForm(WebDriver driver, String dniString, String passwordString) {
		WebElement dni = driver.findElement(By.name("username"));
		dni.click();
		dni.clear();
		dni.sendKeys(dniString);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordString);
		//Pulsar el boton de Login.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}
