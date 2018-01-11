package ru.lextop.steamcalculator.steam.units

import ru.lextop.steamcalculator.steam.quantity.Dimension
import ru.lextop.steamcalculator.steam.unit.*
import ru.lextop.steamcalculator.steam.unit.base.K
import ru.lextop.steamcalculator.steam.unit.base.kg

// Mass Units

val lb = kg / 0.45359237 withSymbol "lb"

// Energy

val J = CoherentUnit(Dimension(M = 1, L = 2, T = -2), "Joule", "J")
val kJ = k(J) withSymbol "kJ"
val erg = J / 1e-7 withSymbol "erg"
val cal = J / 4.1868 withSymbol "cal"
val calth = J / 4.184 withSymbol "calth"
val cal15 = J / 1.1855 withSymbol "cal15"
val kcal = k(cal) withSymbol "kcal"
val kcalth = k(calth) withSymbol "kcalth"
val kcal15 = k(cal15) withSymbol "kcal15"
val BTU = J / 1055.05585262 withSymbol "BTU"
val kWh = J / 3.6e6 withSymbol "kWh"

// Specific Energy

val J_kg = CoherentUnit(Dimension(L = 2, T = -2), symbol = "J/kg")
val kJ_kg = kJ / kg withSymbol "kJ/kg"
val cal_kg = cal / kg withSymbol "cal/kg"
val calth_kg = calth / kg withSymbol "calth/kg"
val cal15_kg = cal15 / kg withSymbol "cal15/kg"
val kcal_kg = kcal / kg withSymbol "kcal/kg"
val kcalth_kg = kcalth / kg withSymbol "kcalth/kg"
val kcal15_kg = kcal15 / kg withSymbol "kcal15/kg"
val BTU_lb = BTU / lb withSymbol "BTU/lb"

// Temperature

val C = K - 273.15 withSymbol "C"
val F = 9.0 / 5.0 * C + 32.0 withSymbol "F"
val Ra = F + 459.57 withSymbol "R"
val D = (100.0 - C) * 3.0 / 2.0 withSymbol "D"
val N = 33.0 / 100.0 * C withSymbol "N"
val Re = 0.8 * C withSymbol "Re"
val Ro = 21.0 / 40.0 * C + 7.5 withSymbol "Ro"

// Specific Heat Capacity

val J_kgK = CoherentUnit(Dimension(L = 2, O = -1, T = -2), symbol = "J/(kg*K)")
val kJ_kgK = kJ_kg / K withSymbol "kJ/(kg*K)"
val cal_kgK = cal_kg / K withSymbol "cal/(kg*K)"
val calth_kgK = calth_kg / K withSymbol "calth/(kg*K)"
val cal15_kgK = cal15_kg / K withSymbol "cal15/(kg*K)"
val kcal_kgK = kcal_kg / K withSymbol "kcal/(kg*K)"
val kcalth_kgK = kcalth_kg / K withSymbol "kcalth/(kg*K)"
val kcal15_kgK = kcal15_kg / K withSymbol "kcal15/(kg*K)"
val BTU_lbR = BTU_lb / Ra withSymbol "BTU/(lb*R)"

// Pressure

val Pa = CoherentUnit(Dimension(M = 1, L = -1, T = -2), symbol = "Pa")
val kPa = k(Pa) withSymbol "kPa"
val MPa = M(Pa) withSymbol "MPa"
val bar = Pa / 1e5 withSymbol "bar"
val at = Pa / 98066.5 withSymbol "at"
val kgf_cm2 = at withSymbol "kgf/cm2"
val atm = Pa / 101325.0 withSymbol "atm"
val psi = Pa / 6894.76 withSymbol "psi"
val lb_in2 = psi withSymbol "lb/in2"
val mmHg = Pa / 133.322 withSymbol "mmHg"
val mH2O = Pa / 9806.65 withSymbol "mH2O"

// Ratio

val ratio = CoherentUnit(Dimension(), symbol = "_")
val percent = ratio * 100.0 withSymbol "%"

// Specific Volume

val m3_kg = CoherentUnit(Dimension(L = 3, M = -1), symbol = "m3/kg")
val ft3_lb = m3_kg * 16.01846353 withSymbol "ft3/lb"

// Density

val kg_m3 = CoherentUnit(Dimension(M = 1, L = -3), symbol = "kg/m3")
val lb_ft3 = ratio / ft3_lb withSymbol "lb/ft3"

// Dynamic Viscosity

val Pas = CoherentUnit(Dimension(M = 1, T = -1, L = -1), symbol = "Pa*T")
val cP = Pas * 1e3 withSymbol "cP"

// Temperature^-1

val K_1 = CoherentUnit(Dimension(O = -1), symbol = "1/K")
val R_1 = ratio / Ra withSymbol  "1/R"

// Compressibility

val Pa_1 = CoherentUnit(Dimension(M = -1, L = 1, T = 2), symbol = "1/Pa")
val MPa_1 = Pa_1 * 1e6 withSymbol "1/MPa"
val in2_lb = ratio / lb_in2 withSymbol "in2_lb"

// Kinematic Viscosity

val m2_s = CoherentUnit(Dimension(L = 2, T = -1), symbol = "m2/s")
val cSt = m2_s * 1e6 withSymbol "cSt"

// Speed

val m_s = CoherentUnit(Dimension(L = 1, T = -1), symbol = "m/s")
val ft_s = m_s / 0.3048 withSymbol "ft/s"

// Surface Tension

val N_m = CoherentUnit(Dimension(M = 1, T = -2), symbol = "N/m")
val kg_s2 = N_m withSymbol "kg/s2"
val lbf_ft = kg_s2 / 14.5939029 withName "lbf/ft"

// Thermal Conductivity

val W_mK = CoherentUnit(Dimension(M = 1, L = 1, T = -3, O = -1), symbol = "W/(m*K)")
val kW_mK = k(W_mK) withName "kW/(m*K)"
val BTU_hrftR = W_mK / 1.7295772056 withName "BTU/(hr*ft*R)"
