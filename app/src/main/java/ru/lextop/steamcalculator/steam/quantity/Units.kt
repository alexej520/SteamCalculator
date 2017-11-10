package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.steam.quantity.EnergyUnit.kJ
import ru.lextop.steamcalculator.steam.quantity.MassUnit.kg


object MassUnit {
    val kg = BaseUnit(kg = 1) alias "kg"
}

object EnergyUnit{
    val J = BaseUnit(kg = 1, m = 2, s = -2) alias "J"
    val kJ = k(J) name byDefault
    val MJ = M(J) name byDefault
    val erg = J / 1e-7 name "erg"
    val cal = J / 4.1868 name "cal"
    val calth = J / 4.184 name "calth"
    val cal15 = J / 1.1855 name "cal15"
    val BTU = J / 1055.6 name "BTU"
    val kWh = J / 3.6e6 name "kWh"
}

object SpecificEnergyUnit{
    val J_kg = BaseUnit(m = 2, s = -2) alias "J/kg"
    init {
        kJ / kg name byDefault
    }
}

object PressureUnit{
    val Pa = BaseUnit(kg = 1, m = -1, s = -2) alias "Pa"
    val kPa = k(Pa) name byDefault
    val MPa = M(Pa) name byDefault
    val bar = Pa / 1e5 name "bar"
    val at = Pa / 98066.5 name "at"
    val atm = Pa / 101325.0 name "atm"
    val psi = Pa / 6894.76 name "psi"
    val mmHg = Pa / 133.322 name "mmHg"
    val mH2O = Pa / 9806.65 name "mH2O"

}

object TemperatureUnit{
    val K = BaseUnit(K = 1) alias "K"
    val C = K - 273.15 name "C"
    val F = 9.0 / 5.0 * C + 32.0 name "F"
    val Ra = F + 459.57 name "Ra"
    val D = (100.0 - C) * 3.0 / 2.0 name "D"
    val N = 33.0 / 100.0 * C name "N"
    val Re = 0.8 * C name "Re"
    val Ro = 21.0 / 40.0 * C + 7.5 name "Ro"

}

object RatioUnit{
    val ratio = BaseUnit() alias "_"
    val percent = ratio * 100.0 name "%"
}