package groupProject.tgbot.config

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Indicated that the Method of a Class extending [LongPollingBot] will be called after the bot was registered
 * If the Method has a single Parameter of type [BotSession], the method get passed the bot session the bot was registered with
 * <br></br><br></br>
 *
 * The bot session passed is the ones returned by [TelegramBotsApi.registerBot]
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(RetentionPolicy.RUNTIME)
annotation class AfterBotRegistration
