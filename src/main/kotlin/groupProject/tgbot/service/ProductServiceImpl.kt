package groupProject.tgbot.service

import groupProject.tgbot.dto.ProductDto
import groupProject.tgbot.repository.ProductRepository
import groupProject.tgbot.utils.ProductUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.net.URL

@Service
class ProductServiceImpl: ProductService {

    enum class MarketPlace(val url: String) {
        WILDBERRIES("www.wildberries.ru"),
        OZON("ozon.ru")
    }

    @Autowired
    lateinit var productRepository: ProductRepository

    override fun saveNewProduct(chatId: Long, url: String): ProductDto {
        val urlObj = URL(url)

        val product = when (urlObj.authority) {
            MarketPlace.WILDBERRIES.url -> ProductUtils.findWildberriesProductInfo(url)
            MarketPlace.OZON.url -> ProductUtils.findOzonProductInfo(url)
            else -> throw RuntimeException()
        }

        return product
    }

}