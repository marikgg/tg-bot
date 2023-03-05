package groupProject.tgbot.dto

data class ProductDto(val name: String? = null, val price: Long? = null) {
    override fun toString(): String {
        return "\n$name, цена = $price"
    }
}
