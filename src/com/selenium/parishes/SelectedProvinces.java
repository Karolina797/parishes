package com.selenium.parishes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        // String[] provinces = {"Dolnośląskie", "Kujawsko-Pomorskie", "Lubelskie", "Lubuskie", "Łódzkie", "Małopolskie",
        //         "Mazowieckie", "Opolskie", "Podkarpackie", "Podlaskie", "Pomorskie", "Śląskie", "Świętokrzyskie",
        //       "Warmińsko-Mazurskie", "Wielkopolskie", "Zachodniopomorskie"};

        String[] provinces = {"Kujawsko-Pomorskie"};

        parishesFromAllProvince(driver, provinces, parishesList);

    }

    //Data of selected parish
    public static void parishData(WebDriver driver, List parishesList) {

        WebDriverWait w = new WebDriverWait(driver, 5);
        w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='prawa2']/div[2]")));
        driver.findElement(By.xpath("//div[@class='prawa2']/div[2]"));
        Parish parish = new Parish(driver.findElement(By.xpath("//div[@class='item']/h1")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[2]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[4]")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[6]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[8]")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[10]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[12]")).getText());
        parishesList.add(parish);
        System.out.println(parish.getName());
        driver.navigate().back();
        System.out.println("po back");
        WebDriverWait w2 = new WebDriverWait(driver, 5);
        w2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='prawa2']/div[2]")));
        System.out.println("powrót do until");

    }


    //Data of parishes from one page
    public static void parishesFromOnePage(WebDriver driver, List parishesList) {

        int amountOfParishesOnPage = driver.findElements(By.cssSelector("a[href*='/parafia-']")).size();
        int i = 0;

        while (i < amountOfParishesOnPage) {
            WebDriverWait w = new WebDriverWait(driver, 5);
            w.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href*='/parafia-']")));

            driver.findElements(By.cssSelector("a[href*='/parafia-']")).get(i).click();
            try {
                w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='prawa2']/div[2]")));
            } catch (Exception exception) {
                System.out.println("Exception, przed klikiem");
                driver.findElements(By.cssSelector("a[href*='/parafia-']")).get(i).click();
                System.out.println("Exception, po kliku");
                w.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='prawa2']/div[2]")));
            }

            System.out.println("po click");
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
        int provincesSize = driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).size();
        List provincesList = Arrays.asList(provinces);

        int j = 0;

        for (int i = 0; i < provincesSize; i++) {

            String currentProvince = driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).get(i).getText();

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
