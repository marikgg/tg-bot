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
    val url: String? = "",

    @Column(name = "chat_id", nullable = false)
    val chatId: String? = "",

    @Column(nullable = false)
    val price: Long? = 0
)