package groupProject.tgbot.schedulingtasks

import groupProject.tgbot.bot.PriceCheckerBot
import groupProject.tgbot.repository.ProductRepository
import groupProject.tgbot.utils.ProductUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CheckPricesJob constructor(
    private val productRepository: ProductRepository,
    private val priceCheckerBot: PriceCheckerBot,
    private val productUtils: ProductUtils
) {

    companion object {
        private val log = LoggerFactory.getLogger(CheckPricesJob::class.java)
    }

    @Scheduled(fixedRateString = "\${bot.checkPricesFixedRate}")
    fun checkPrices() {
        log.info("Starts checking prices")
        val products = productRepository.findAll()
        products.parallelStream().forEach {
            val updatedProduct = productUtils.findProductInfo(it.url)
            if (updatedProduct != null) {
                if (updatedProduct.price != it.price) {
                    priceCheckerBot.sendNotification(it.chatId,
                        "Цена на товар \"${it.name}\" изменилась.\n" +
                                "Была - ${it.price}. Стала - ${updatedProduct.price}")
                    it.price = updatedProduct.price
                    productRepository.save(it)
                }
            }
        }
    }
}