package groupProject.tgbot.repository

import groupProject.tgbot.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository: JpaRepository<Product, Long> {
    fun findAllByChatId(chatId: Long) : List<Product>
}