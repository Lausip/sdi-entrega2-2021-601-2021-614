package com.uniovi.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.uniovi.tests.util.SeleniumUtils;

public class PO_LoginView extends PO_NavView {

	static public void fillForm(WebDriver driver, String dnip, String passwordp) {
		WebElement dni = driver.findElement(By.name("email"));
		dni.click();
		dni.clear();
		dni.sendKeys(dnip);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordp);
		//Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();	
	}
	
	static public void login(WebDriver driver, String dnip, String passwordp,String url) {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, url, "class", "btn btn-primary");
		fillForm( driver, dnip, passwordp);
		
	}
	static public void logout(WebDriver driver) {
		PO_HomeView.clickOption(driver,"/logout",  "class", "btn btn-primary");

	}

	public static void loginRest(WebDriver driver, String dnip, String passw) {
		driver.navigate().to("https://localhost:8081/homeRest.html");
		fillForm( driver, dnip, passw);
		
	}
	
}
