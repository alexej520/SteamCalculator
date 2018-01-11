package quantityvalue

// The International System of Units (SI) [8th edition, 2006; updated in 2014]

data class Dimension(val L: Int = 0,
                val M: Int = 0,
                val T: Int = 0,
                val I: Int = 0,
                val O: Int = 0,
                val N: Int = 0,
                val J: Int = 0) {
    operator fun div(other: Dimension): Dimension {
        return Dimension(
                L = L - other.L,
                M = M - other.M,
                T = T - other.T,
                I = I - other.I,
                O = O - other.O,
                N = N - other.N,
                J = J - other.J)
    }

    operator fun times(other: Dimension): Dimension {
        return Dimension(
                L = L + other.L,
                M = M + other.M,
                T = T + other.T,
                I = I + other.I,
                O = O + other.O,
                N = N + other.N,
                J = J + other.J)
    }
}

internal fun checkBaseDimension(dimension: Dimension): Boolean {
    with(dimension) {
        var dimCounter = 0
        if (L == 1) dimCounter++
        else if (L != 0) return false
        if (M == 1) dimCounter++
        else if (M != 0) return false
        if (T == 1) dimCounter++
        else if (T != 0) return false
        if (I == 1) dimCounter++
        else if (I != 0) return false
        if (O == 1) dimCounter++
        else if (O != 0) return false
        if (N == 1) dimCounter++
        else if (N != 0) return false
        if (J == 1) dimCounter++
        else if (J != 0) return false
        if (dimCounter == 1) return true
        return false
    }
}
