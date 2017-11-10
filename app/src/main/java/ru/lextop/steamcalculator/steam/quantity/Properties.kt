package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.steam.quantity.PressureUnit.Pa
import ru.lextop.steamcalculator.steam.quantity.RatioUnit.ratio
import ru.lextop.steamcalculator.steam.quantity.SpecificEnergyUnit.J_kg
import ru.lextop.steamcalculator.steam.quantity.TemperatureUnit.K

val SpecificEnthalpy = Property("Specific Enthalpy", "h", J_kg.baseUnit)
val SpecificEntropy = Property("Specific Entropy", "s", J_kg.baseUnit)
val Pressure = Property("Pressure", "P", Pa.baseUnit)
val VapourFraction = Property("Vapour Fraction", "x", ratio.baseUnit)
val Temperature = Property("Temperature", "T", K.baseUnit)