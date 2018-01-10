package ru.lextop.steamcalculator.steam.quantity

class Dimension(val m: Int = 0,
                val kg: Int = 0,
                val s: Int = 0,
                val A: Int = 0,
                val K: Int = 0,
                val mol: Int = 0,
                val cd: Int = 0) {
    operator fun div(other: Dimension): Dimension {
        return Dimension(
                m = m - other.m,
                kg = kg - other.kg,
                s = s - other.s,
                A = A - other.A,
                K = K - other.K,
                mol = mol - other.mol,
                cd = cd - other.cd)
    }

    operator fun times(other: Dimension): Dimension {
        return Dimension(
                m = m + other.m,
                kg = kg + other.kg,
                s = s + other.s,
                A = A + other.A,
                K = K + other.K,
                mol = mol + other.mol,
                cd = cd + other.cd)
    }
}
