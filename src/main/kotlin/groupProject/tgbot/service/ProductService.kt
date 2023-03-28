package groupProject.tgbot.service

import groupProject.tgbot.dto.ProductDto

interface ProductService {
    fun saveNewProduct(chatId: Long, url: String): ProductDto?

    fun findAllProducts(chatId: Long): List<ProductDto>

    fun deleteOneByChatIdAndNumber(chatId: Long, number: Long): String
}