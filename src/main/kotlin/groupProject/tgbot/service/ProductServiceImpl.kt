package groupProject.tgbot.service

import groupProject.tgbot.dto.ProductDto
import groupProject.tgbot.entity.Product
import groupProject.tgbot.repository.ProductRepository
import groupProject.tgbot.utils.ProductUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl constructor(
    private val productRepository: ProductRepository,
    private val productUtils: ProductUtils): ProductService {

    companion object {
        private val log = LoggerFactory.getLogger(ProductService::class.java)
    }


    override fun saveNewProduct(chatId: Long, url: String): ProductDto? {
        try {
            val productDto = productUtils.findProductInfo(url) ?: return null

            val product = Product()
            product.url = url
            product.chatId = chatId
            product.name = productDto.name
            product.price = productDto.price
            productRepository.save(product)
            return productDto
        } catch (e : Exception) {
            log.error(e.message)
            return null
        }
    }

    override fun findAllProducts(chatId: Long): List<ProductDto> {
        val productDtos = arrayListOf<ProductDto>()

        val productList = productRepository.findAllByChatId(chatId)
        productList.forEach {
            val productDto = ProductDto(it.name, it.price)
            productDtos.add(productDto)
        }

        return productDtos
    }

    override fun deleteOneByChatIdAndNumber(chatId: Long, number: Long): String {
        val productList = productRepository.findAllByChatId(chatId)
        var result = "Товар успешно удален из списка"
        try {
            val product = productList[number.toInt() - 1]
            productRepository.delete(product)
        } catch (e: Exception) {
            result = "Вы ввели неправильный номер"
        }

        return result
    }
}