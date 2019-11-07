package com.selenium.parishes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SelectedProvinces {

    public static void main(String[] args) throws IOException {

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

        WebDriverWait w = new WebDriverWait(driver, 15);
        w.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='prawa2']/div[2]")));
        driver.findElement(By.xpath("//div[@class='prawa2']/div[2]"));
        Parish parish = new Parish(driver.findElement(By.xpath("//div[@class='item']/h1")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[2]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[4]")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[6]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[8]")).getText(), driver.findElement(By.xpath("//div[@class='prawa2']/div[10]")).getText(),
                driver.findElement(By.xpath("//div[@class='prawa2']/div[12]")).getText());
        parishesList.add(parish);
        System.out.println(parish.getName());
        driver.navigate().back();
        w.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='prawa2']/div[2]")));

    }


    //Data of parishes from one page
    public static void parishesFromOnePage(WebDriver driver, List parishesList) {

        int amountOfParishesOnPage = driver.findElements(By.cssSelector("a[href*='/parafia-']")).size();
        int i = 0;

        while (i < amountOfParishesOnPage) {
            WebDriverWait w = new WebDriverWait(driver, 15);
            w.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='/parafia-']")));
            driver.findElements(By.cssSelector("a[href*='/parafia-']")).get(i).click();

            try {
                w.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='prawa2']/div[2]")));
            } catch (Exception exception) {
                driver.findElements(By.cssSelector("a[href*='/parafia-']")).get(i).click();
                w.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='prawa2']/div[2]")));
            }
            parishData(driver, parishesList);
            i++;

        }
    }

    //Data of parishes from one province
    public static void parishesFromOneProvince(WebDriver driver, List parishesList, String currentProvince) throws IOException {

        driver.findElement(By.cssSelector("a[title='ostatnia']")).click();
        int numberOfPages = Integer.parseInt(driver.findElement(By.cssSelector("span.nawigacja_a")).getText());
        driver.findElement(By.linkText("«")).click();

        int i = 0;
        while (i < numberOfPages) {

            parishesFromOnePage(driver, parishesList);

            if (i == numberOfPages - 1) {
                try (
                        BufferedWriter writer = Files.newBufferedWriter(Paths.get("./" + currentProvince + ".csv"));
                        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Parish Name", "Address", "Phone Number", "url", "Diocese", "Decanate", "Notes"));
                ) {
                    for (int t = 0; t < parishesList.size(); t++) {
                        csvPrinter.printRecord(((Parish) parishesList.get(t)).getName(), ((Parish) parishesList.get(t)).getAddress().replaceAll("\n", " "), ((Parish) parishesList.get(t)).getPhoneNumber(),
                                ((Parish) parishesList.get(t)).getUrl(), ((Parish) parishesList.get(t)).getDiocese(), ((Parish) parishesList.get(t)).getDecanate(), ((Parish) parishesList.get(t)).getNotes().replaceAll("\n", " "));
                    }
                    csvPrinter.flush();
                }

            } else {
                driver.findElement(By.cssSelector("a[title='następna']")).click();
            }
            i++;

        }
    }


    //Data of parishes from all provinces
    public static void parishesFromAllProvince(WebDriver driver, String[] provinces, List parishesList) throws IOException {

        driver.get("http://www.plebanie.pl/");
        int provincesSize = driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).size();
        List provincesList = Arrays.asList(provinces);

        int j = 0;

        for (int i = 0; i < provincesSize; i++) {

            String currentProvince = driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).get(i).getText();

            if (provincesList.contains(currentProvince)) {
                driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).get(i).click();
                parishesFromOneProvince(driver, parishesList, currentProvince);
                j++;
                if (j == provinces.length)
                    break;
            }
        }
    }
}
