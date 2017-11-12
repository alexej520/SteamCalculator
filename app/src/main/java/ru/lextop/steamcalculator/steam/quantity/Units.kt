package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.kJ
import ru.lextop.steamcalculator.steam.quantity.Units.MassUnit.kg

object Units{
    object MassUnit : BaseUnit(kg = 1) {
        val kg = createAlias(Pair("kg", R.string.kg))
    }

    object EnergyUnit : BaseUnit(kg = 1, m = 2, s = -2) {
        val J = createAlias(Pair("J", R.string.J))
        val kJ = k(J) addWith Pair(byDefault, R.string.kJ)
        val MJ = M(J) addWith Pair(byDefault, R.string.MJ)
        val erg = J / 1e-7 addWith Pair("erg", R.string.erg)
        val cal = J / 4.1868 addWith Pair("cal", R.string.cal)
        val calth = J / 4.184 addWith Pair("calth", R.string.calth)
        val cal15 = J / 1.1855 addWith Pair("cal15", R.string.cal15)
        val BTU = J / 1055.6 addWith Pair("BTU", R.string.BTU)
        val kWh = J / 3.6e6 addWith Pair("kWh", R.string.kWh)
    }

    object SpecificEnergyUnit : BaseUnit(m = 2, s = -2) {
        val J_kg = createAlias(Pair("J/kg", R.string.J_kg))
        val kJ_kg = kJ / kg addWith Pair(byDefault, R.string.kJ_kg)
    }

    object PressureUnit : BaseUnit(kg = 1, m = -1, s = -2) {
        val Pa = createAlias(Pair("Pa", R.string.Pa))
        val kPa = k(Pa) addWith Pair(byDefault, R.string.kPa)
        val MPa = M(Pa) addWith Pair(byDefault, R.string.MPa)
        val bar = Pa / 1e5 addWith Pair("bar", R.string.bar)
        val at = Pa / 98066.5 addWith Pair("at", R.string.at)
        val atm = Pa / 101325.0 addWith Pair("atm", R.string.atm)
        val psi = Pa / 6894.76 addWith Pair("psi", R.string.psi)
        val mmHg = Pa / 133.322 addWith Pair("mmHg", R.string.mmHg)
        val mH2O = Pa / 9806.65 addWith Pair("mH2O", R.string.mH2O)

    }

    object TemperatureUnit : BaseUnit(K = 1) {
        val K = createAlias(Pair("K", R.string.K))
        val C = K - 273.15 addWith Pair("C", R.string.C)
        val F = 9.0 / 5.0 * C + 32.0 addWith Pair("F", R.string.F)
        val Ra = F + 459.57 addWith Pair("Ra", R.string.Ra)
        val D = (100.0 - C) * 3.0 / 2.0 addWith Pair("D", R.string.D)
        val N = 33.0 / 100.0 * C addWith Pair("N", R.string.N)
        val Re = 0.8 * C addWith Pair("Re", R.string.Re)
        val Ro = 21.0 / 40.0 * C + 7.5 addWith Pair("Ro", R.string.Ro)

    }

    object RatioUnit : BaseUnit() {
        val ratio = createAlias(Pair("_", R.string.ratio))
        val percent = ratio * 100.0 addWith Pair("%", R.string.percent)
    }
}