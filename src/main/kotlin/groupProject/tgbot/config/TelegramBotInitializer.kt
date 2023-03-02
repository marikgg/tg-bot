package groupProject.tgbot.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.BotSession
import org.telegram.telegrambots.meta.generics.LongPollingBot
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Stream


/**
 * Receives all beand which are #LongPollingBot and #WebhookBot and register them in #TelegramBotsApi.
 */
class TelegramBotInitializer(
    telegramBotsApi: TelegramBotsApi,
    longPollingBots: List<LongPollingBot>,
) : InitializingBean {
    private val telegramBotsApi: TelegramBotsApi
    private val longPollingBots: List<LongPollingBot>

    init {
        Objects.requireNonNull(telegramBotsApi)
        Objects.requireNonNull(longPollingBots)
        this.telegramBotsApi = telegramBotsApi
        this.longPollingBots = longPollingBots
    }

    override fun afterPropertiesSet() {
        try {
            for (bot in longPollingBots) {
                val session = telegramBotsApi.registerBot(bot)
                handleAfterRegistrationHook(bot, session)
            }
        } catch (e: TelegramApiException) {
            throw RuntimeException(e)
        }
    }

    private fun handleAnnotatedMethod(bot: Any, method: Method, session: BotSession) {
        try {
            if (method.parameterCount > 1) {
                log.warn(
                    String.format(
                        "Method %s of Type %s has too many parameters",
                        method.name, method.declaringClass.canonicalName
                    )
                )
                return
            }
            if (method.parameterCount == 0) {
                method.invoke(bot)
                return
            }
            if (method.parameterTypes[0] == BotSession::class.java) {
                method.invoke(bot, session)
                return
            }
            log.warn(
                String.format(
                    "Method %s of Type %s has invalid parameter type",
                    method.name, method.declaringClass.canonicalName
                )
            )
        } catch (e: InvocationTargetException) {
            log.error(
                String.format(
                    "Couldn't invoke Method %s of Type %s",
                    method.name, method.declaringClass.canonicalName
                )
            )
        } catch (e: IllegalAccessException) {
            log.error(
                String.format(
                    "Couldn't invoke Method %s of Type %s",
                    method.name, method.declaringClass.canonicalName
                )
            )
        }
    }

    private fun handleAfterRegistrationHook(bot: Any, botSession: BotSession) {
        Stream.of(*bot.javaClass.methods)
            .filter { method: Method ->
                method.getAnnotation(
                    AfterBotRegistration::class.java
                ) != null
            }
            .forEach { method: Method ->
                handleAnnotatedMethod(
                    bot,
                    method,
                    botSession
                )
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramBotInitializer::class.java)
    }
}