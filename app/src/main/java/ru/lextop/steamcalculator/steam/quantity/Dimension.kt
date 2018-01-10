package ru.lextop.steamcalculator.steam.quantity

// The International System of Units (SI) [8th edition, 2006; updated in 2014]

class Dimension(val L: Int = 0,
                val M: Int = 0,
                val T: Int = 0,
                val A: Int = 0,
                val K: Int = 0,
                val mol: Int = 0,
                val cd: Int = 0) {
    operator fun div(other: Dimension): Dimension {
        return Dimension(
                L = L - other.L,
                M = M - other.M,
                T = T - other.T,
                A = A - other.A,
                K = K - other.K,
                mol = mol - other.mol,
                cd = cd - other.cd)
    }

    operator fun times(other: Dimension): Dimension {
        return Dimension(
                L = L + other.L,
                M = M + other.M,
                T = T + other.T,
                A = A + other.A,
                K = K + other.K,
                mol = mol + other.mol,
                cd = cd + other.cd)
    }
}
