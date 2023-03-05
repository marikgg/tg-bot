package groupProject.tgbot.bot

import groupProject.tgbot.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import java.net.MalformedURLException
import java.net.URL

@Component
class PriceCheckerBot : TelegramLongPollingBot() {

    @Autowired
    lateinit var productService: ProductService

    val description =
        "Данный бот служит для отслеживания цены продуктов DNS, Wildberries"

    val newProductMessage =
        "Вставьте ссылку на продукт"

    val errorNewProductMessage =
        "Данный продукт не удалось добавить в отслеживаемые"

    val successNewProductMessage =
        "Вы добавили новый товар в отслеживаемые - \n"

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
                when {
                    messageText.equals("/start") -> description
                    messageText.equals("Добавить новый продукт") -> newProductMessage
                    checkUrlIsCorrect(messageText) ->  {
                        val product = productService.saveNewProduct(chatId, messageText)
                        if (product == null)  errorNewProductMessage
                        else successNewProductMessage + product.toString()
                    }
                    messageText.equals("Показать все отслеживаемые продукты") -> {
                        productService.findAllProducts(chatId).toString()
                    }

                    else -> {"Вы ввели несуществующую команду"}
                }

            } else {
                "Я понимаю только текст"
            }
            sendNotification(chatId, responseText)
        }
    }

    fun sendNotification(chatId: Long?, text: String) {
        val sendMessage: SendMessage = SendMessage.builder().chatId(chatId.toString()).text(text).build()
        sendMessage.enableMarkdown(true)
        sendMessage.replyMarkup = getReplyMarkup(
            listOf(
                listOf("Добавить новый продукт", "Показать все отслеживаемые продукты"))
        )
        execute(sendMessage)
    }

    private fun getReplyMarkup(allButtons: List<List<String>>): ReplyKeyboardMarkup {
        val markup = ReplyKeyboardMarkup()
        markup.keyboard = allButtons.map { rowButtons ->
            val row = KeyboardRow()
            rowButtons.forEach { rowButton -> row.add(rowButton) }
            row
        }
        return markup
    }

    private fun checkUrlIsCorrect(value: String) : Boolean {
        return try {
            URL(value)
            true
        } catch (var5: MalformedURLException) {
            false
        }
    }
}