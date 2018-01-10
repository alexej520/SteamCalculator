package ru.lextop.steamcalculator.steam.quantity

// The International System of Units (SI) [8th edition, 2006; updated in 2014]

class Dimension(val L: Int = 0,
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
