package com.selenium.parishes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Arrays;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectedProvinces {
    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:\\Work\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        List<Parish> parishesList = new LinkedList<>();


        //Array of provinces
        String[] provinces = {"Dolnośląskie", "Kujawsko-Pomorskie", "Lubelskie", "Lubuskie", "Łódzkie", "Małopolskie",
                "Mazowieckie", "Opolskie", "Podkarpackie", "Podlaskie", "Pomorskie", "Śląskie", "Świętokrzyskie",
                "Warmińsko-Mazurskie", "Wielkopolskie", "Zachodniopomorskie"};


        parishesFromAllProvince(driver, provinces, parishesList);

    }

    //Data of selected parish
    public static void parishData(WebDriver driver, List parishesList) {

        Parish parish = new Parish(driver.findElement(By.xpath("//div[@class='item']/h1")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[2]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[4]")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[6]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[8]")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[10]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[12]")).getText());
        parishesList.add(parish);
        //parish.parishPresentation(); - just for me
        driver.navigate().back();

    }


    //Data of parishes from one page
    public static void parishesFromOnePage(WebDriver driver, List parishesList) {

        int amountOfParishesOnPage = driver.findElements(By.cssSelector("a[href*='/parafia-']")).size();
        int i = 0;

        while (i < amountOfParishesOnPage) {

            driver.findElements(By.cssSelector("a[href*='/parafia-']")).get(i).click();
            parishData(driver, parishesList);
            i++;

        }
    }

    //Data of parishes from one province
    public static void parishesFromOneProvince(WebDriver driver, List parishesList) {

        driver.findElement(By.cssSelector("a[title='ostatnia']")).click();
        int numberOfPages = Integer.parseInt(driver.findElement(By.cssSelector("span.nawigacja_a")).getText());
        driver.findElement(By.linkText("«")).click();

        int i = 0;
        while (i < numberOfPages) {

            parishesFromOnePage(driver, parishesList);
            driver.findElement(By.cssSelector("a[title='następna']")).click();
            i++;
        }

    }


    //Data of parishes from all provinces
    public static void parishesFromAllProvince(WebDriver driver, String[] provinces, List parishesList) {

        driver.get("http://www.plebanie.pl/");
        List<WebElement> provincesOnPage = driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']"));
        List provincesList = Arrays.asList(provinces);

        int j = 0;

        for (int i = 0; i < provincesOnPage.size(); i++) {

            String currentProvince = provincesOnPage.get(i).getText();

            if (provincesList.contains(currentProvince)) {

                driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).get(i).click();
                parishesFromOneProvince(driver, parishesList);
                j++;
                if (j == provinces.length)
                    break;

            }
        }
    }

}
