package groupProject.tgbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TgBotApplication

fun main(args: Array<String>) {
	val ctx = SpringApplication.run(TgBotApplication::class.java, *args)
	val chromeDriverUrl = ctx.environment.getProperty("chrome_driver.url")
	chromeDriverUrl?.let {
		System.setProperty("webdriver.chrome.driver", it)
		System.setProperty("webdriver.chrome.whitelistedIps", "")
		System.setProperty("webdriver.http.factory", "jdk-http-client")
	}
}
