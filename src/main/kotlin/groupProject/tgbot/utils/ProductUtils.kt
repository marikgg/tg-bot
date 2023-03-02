package groupProject.tgbot.utils

import groupProject.tgbot.dto.ProductDto
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration


class ProductUtils {
    companion object {
        fun findWildberriesProductInfo(url: String): ProductDto {

            val urlObj = URL(url)

            System.setProperty("webdriver.chrome.driver", "/Users/reset/Library/chromedriver")
            System.setProperty("webdriver.chrome.whitelistedIps", "");
            val driver = ChromeDriver()
            driver.get(url)

            val wait = WebDriverWait(driver, Duration.ofSeconds(15))

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-page__header")))
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("price-block__final-price")))

            val name = driver.findElement(By.className("product-page__header")).text
            val priceWithCurrency = driver.findElement(By.className("price-block__final-price")).text
            val price = priceWithCurrency.filter { it.isDigit() }.toLong()

            return ProductDto(name, price)
        }

        fun findOzonProductInfo(url: String): ProductDto {

            val urlObj = URL(url)

            System.setProperty("webdriver.chrome.driver", "/Users/reset/Library/chromedriver")
            System.setProperty("webdriver.chrome.whitelistedIps", "");
            val driver = ChromeDriver()
            driver.get(url)

            val wait = WebDriverWait(driver, Duration.ofSeconds(15))

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("my1 m0y")))

            //val name = driver.findElement(By.className("product-page__header")).text
            val priceWithCurrency = driver.findElement(By.className("my1 m0y")).text
            val price = priceWithCurrency.filter { it.isDigit() }.toLong()

            return ProductDto(null, price)
        }
    }
}