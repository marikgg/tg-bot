package groupProject.tgbot.utils

import groupProject.tgbot.dto.ProductDto
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Component
import java.net.URL
import java.time.Duration


@Component
class ProductUtils {

    enum class MarketPlace(val url: String) {
        WILDBERRIES("www.wildberries.ru"),
        DNS("www.dns-shop.ru"),
        YANDEX_MARKET("market.yandex.ru")
    }

    init {
        System.setProperty("webdriver.chrome.driver", "/Users/reset/Library/chromedriver")
        System.setProperty("webdriver.chrome.whitelistedIps", "")
    }

    fun findProductInfo(url: String?): ProductDto? {
        val urlObj = URL(url)

        return when (urlObj.authority) {
            MarketPlace.WILDBERRIES.url -> this.findWildberriesProductInfo(url)
            MarketPlace.DNS.url -> this.findDnsProductInfo(url)
            MarketPlace.YANDEX_MARKET.url -> this.findEldoradoProductInfo(url)
            else -> null
        }
    }

    private fun findWildberriesProductInfo(url: String?): ProductDto? {
        val driver = ChromeDriver()
        driver.get(url)

        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-page__header")))
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("price-block__final-price")))
        } catch (e: Exception) {
            driver.quit()
            return null
        }

        val name = driver.findElement(By.className("product-page__header")).text
        val priceWithCurrency = driver.findElement(By.className("price-block__final-price")).text
        val price = priceWithCurrency.filter { it.isDigit() }.toLong()

        driver.quit()
        return ProductDto(name, price)
    }

    private fun findDnsProductInfo(url: String?): ProductDto? {
        val driver = ChromeDriver()
        driver.get(url)

        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-buy__price")))
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("product-card-top__title")))
        }
        catch (e: Exception) {
            driver.quit()
            return null
        }

        val name = driver.findElement(By.className("product-card-top__title")).text
        val priceWithCurrency = driver.findElement(By.className("product-buy__price")).text
        val price = priceWithCurrency.filter { it.isDigit() }.toLong()

        driver.quit()
        return ProductDto(name, price)
    }

    private fun findEldoradoProductInfo(url: String?): ProductDto? {
        val driver = ChromeDriver()
        driver.get(url)

        val wait = WebDriverWait(driver, Duration.ofSeconds(120))

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-meta-name=\"PriceBlock__price\"]")))
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-baobab-name=\"\$name\"]")))
        }
        catch (e: Exception) {
            driver.quit()
            return null
        }

        val name = driver.findElement(By.cssSelector("[data-baobab-name=\"\$name\"]")).text
        val priceWithCurrency = driver.findElement(By.cssSelector("[data-auto=\"mainPrice\"]")).getAttribute("data-autotest-value")
        val price = priceWithCurrency.filter { it.isDigit() }.toLong()

        driver.quit()
        return ProductDto(name, price)
    }
}