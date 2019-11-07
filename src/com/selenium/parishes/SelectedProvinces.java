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
        List<Parish> parishesList = new LinkedList<>(); // inicjalizację tej listy możemy przenieść do parishesFromAllProvince


        //Array of provinces
        String[] provinces = {"Dolnośląskie", "Kujawsko-Pomorskie", "Lubelskie", "Lubuskie", "Łódzkie", "Małopolskie",
                "Mazowieckie", "Opolskie", "Podkarpackie", "Podlaskie", "Pomorskie", "Śląskie", "Świętokrzyskie",
                "Warmińsko-Mazurskie", "Wielkopolskie", "Zachodniopomorskie"};

        parishesFromAllProvince(driver, provinces, parishesList);

    }

    //Data of selected parish
    public static void parishData(WebDriver driver, List parishesList) {

        WebDriverWait w = new WebDriverWait(driver, 15);
        w.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='prawa2']/div[2]"))); //to jest niepotrzebne, i zobacz, że ten div ogólnie nie jest klikalny
        driver.findElement(By.xpath("//div[@class='prawa2']/div[2]")); //ale już to jest potrzebne
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
            } catch (Exception exception) { //myślę, że w momencie kiedy już dowiedzieliśmy się, że odpowiednim checkiem jest `elementToBeClickable`
                driver.findElements(By.cssSelector("a[href*='/parafia-']")).get(i).click();
                w.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='prawa2']/div[2]")));
            }
            parishData(driver, parishesList);
            i++;

        }
    }

    //Data of parishes from one province
    public static void parishesFromOneProvince(WebDriver driver, List parishesList, String currentProvince) throws IOException {

        driver.findElement(By.cssSelector("a[title='ostatnia']")).click(); //tu też powinniśmy najpier explicite poczekać na klikalność tego elemetnu zanim klikniemy
        int numberOfPages = Integer.parseInt(driver.findElement(By.cssSelector("span.nawigacja_a")).getText());
        driver.findElement(By.linkText("«")).click(); //j.w.

        int i = 0;
        while (i < numberOfPages) {

            parishesFromOnePage(driver, parishesList);

            if (i == numberOfPages - 1) {
                //całą zawartość tego bloku powinniśmy wyeksportować do funkcji exportParishesToCsv(parishesList, csvFileName)
                try (
                        BufferedWriter writer = Files.newBufferedWriter(Paths.get("./" + currentProvince + ".csv"));
                        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Parish Name", "Address", "Phone Number", "url", "Diocese", "Decanate", "Notes"));
                ) {
                    for (int t = 0; t < parishesList.size(); t++) {
                        csvPrinter.printRecord(((Parish) parishesList.get(t)).getName(), ((Parish) parishesList.get(t)).getAddress().replaceAll("\n", " "), ((Parish) parishesList.get(t)).getPhoneNumber(),
                                ((Parish) parishesList.get(t)).getUrl(), ((Parish) parishesList.get(t)).getDiocese(), ((Parish) parishesList.get(t)).getDecanate(), ((Parish) parishesList.get(t)).getNotes().replaceAll("\n", " "));
                    }
                    csvPrinter.flush(); //wydaje mi się, że to jest zbędne
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
        List provincesList = Arrays.asList(provinces); //tą konwersję z array na list możemy zrobić poziom wyżej w mainie i do tej metody parishesFromAllProvince przekazać już listę

        int j = 0;

        for (int i = 0; i < provincesSize; i++) {

            String currentProvince = driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).get(i).getText();

            if (provincesList.contains(currentProvince)) {
                driver.findElements(By.cssSelector("div.item_left a[href*='/wojewodztwo/']")).get(i).click(); //w sumie dla 100% pewnosci powinnićmy najpierw poczekać na klikalność tego elementu zanim klikniemy
                parishesFromOneProvince(driver, parishesList, currentProvince);
                j++;
                if (j == provinces.length) //moim zdaniem wprowadzenie parametru j tylko po to, żeby po przerobieniu ostatniej z zadany prowincji od razu skończyć pętlę to zbędna optymalizacja. Wyrzucając zmienną j bardzo uprościmy kod
                    break;
            }
        }
    }
}
