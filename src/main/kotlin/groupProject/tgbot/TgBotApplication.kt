package groupProject.tgbot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TgBotApplication

fun main(args: Array<String>) {
	SpringApplication.run(TgBotApplication::class.java, *args)
}
