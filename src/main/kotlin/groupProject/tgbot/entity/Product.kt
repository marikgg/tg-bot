package groupProject.tgbot.entity

import jakarta.persistence.*

@Entity
@Table(name = "tg_bot_product")
data class Product (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String? = "",

    @Column(nullable = false)
    var url: String? = "",

    @Column(name = "chat_id", nullable = false)
    var chatId: Long? = 0,

    @Column(nullable = false)
    var price: Long? = 0
)