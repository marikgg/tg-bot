package groupProject.tgbot.bot

import groupProject.tgbot.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class PriceCheckerBot : TelegramLongPollingBot() {

    @Autowired
    lateinit var productService: ProductService

    val description =
        "Данный бот служит для отслеживания цены продуктов Ozon, Wildberries"

    @Value("\${telegram.token}")
    private var token: String = ""

    @Value("\${telegram.botName}")
    private val botName: String = ""

    override fun getBotToken(): String = token

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        if (update!!.hasMessage()) {
            val message = update.message
            val chatId = message.chatId
            val responseText = if (message.hasText()) {
                val messageText = message.text
                if (messageText == "/start") {
                    description
                }
                else {
                    productService.saveNewProduct(chatId, messageText)
                    "Вы написали: *$messageText*"
                }
            } else {
                "Я понимаю только текст"
            }
            sendNotification(chatId, responseText)
        }
    }

    fun sendNotification(chatId: Long, text: String) {
        val sendMessage: SendMessage = SendMessage.builder().chatId(chatId.toString()).text(text).build()
        execute(sendMessage)
    }
}