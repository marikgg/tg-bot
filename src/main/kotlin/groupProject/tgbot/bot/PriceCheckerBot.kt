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
import java.util.*

@Component
class PriceCheckerBot : TelegramLongPollingBot() {

    @Autowired
    lateinit var productService: ProductService

    val description =
        "Данный бот служит для отслеживания цены продуктов DNS, Wildberries"

    val newProductMessage =
        "Вставьте ссылку на продукт."

    val errorNewProductMessage =
        "Данный продукт не удалось добавить в отслеживаемые."

    val successNewProductMessage =
        "Вы добавили новый товар в отслеживаемые - \n"

    val enterNumberToDelete =
        "Введите \"Удалить (№ товара)\""

    val pleaseWaitMsg =
        "Пожалуйста, подождите..."

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
                        sendNotification(chatId, pleaseWaitMsg)
                        val product = productService.saveNewProduct(chatId, messageText)
                        if (product == null)  errorNewProductMessage
                        else successNewProductMessage + product.toString()
                    }
                    messageText.equals("Показать все отслеживаемые продукты") -> {
                        this.composeAllProductsMessage(chatId)
                    }
                    messageText.equals("Удалить продукт из отслеживаемых") -> {
                        sendNotification(chatId, enterNumberToDelete)
                        this.composeAllProductsMessage(chatId)
                    }
                    messageText.lowercase(Locale.getDefault()).startsWith("удалить ") && messageText.last().isDigit()-> {
                        productService.deleteOneByChatIdAndNumber(chatId, messageText.filter { it.isDigit() }.toLong())
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
                listOf("Добавить новый продукт", "Показать все отслеживаемые продукты", "Удалить продукт из отслеживаемых"))
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
        markup.resizeKeyboard = true
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

    private fun composeAllProductsMessage(chatId: Long) : String {
        var msg = ""
        val products = productService.findAllProducts(chatId)
        if (products.isEmpty()) {
            return "Список отслеживаемых продуктов пуст."
        }
        products.forEachIndexed{i, product ->
            run {
                val index = i + 1
                msg += "$index) $product"
            }
        }
        return msg
    }
}