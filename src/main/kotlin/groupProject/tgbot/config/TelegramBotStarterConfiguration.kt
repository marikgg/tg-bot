package groupProject.tgbot.config

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


/**
 * #TelegramBotsApi added to spring context as well
 */
@Configuration
class TelegramBotStarterConfiguration {
    @Bean
    @ConditionalOnMissingBean(TelegramBotsApi::class)
    @Throws(TelegramApiException::class)
    fun telegramBotsApi(): TelegramBotsApi {
        return TelegramBotsApi(DefaultBotSession::class.java)
    }

    @Bean
    @ConditionalOnMissingBean
    fun telegramBotInitializer(
        telegramBotsApi: TelegramBotsApi?,
        longPollingBots: ObjectProvider<List<LongPollingBot>>,
    ): TelegramBotInitializer {
        return TelegramBotInitializer(
            telegramBotsApi!!,
            longPollingBots.getIfAvailable { emptyList() })
    }
}