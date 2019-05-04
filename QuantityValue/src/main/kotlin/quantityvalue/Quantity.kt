package quantityvalue

data class Quantity(
        val name: String = "AnonymousQuantity",
        val symbol: String = "?",
        val dimension: Dimension) {
    fun equals(other: Quantity, ignoreName: Boolean = false, ignoreSymbol: Boolean = false): Boolean {
        if (this === other) return true

        if (dimension != other.dimension) return false
        if (!ignoreName && name != other.name) return false
        if (!ignoreSymbol && symbol != other.symbol) return false

        return true
    }
}
