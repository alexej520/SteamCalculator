package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.BTU
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.cal
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.cal15
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.calth
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.kJ
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.kcal
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.kcal15
import ru.lextop.steamcalculator.steam.quantity.Units.EnergyUnit.kcalth
import ru.lextop.steamcalculator.steam.quantity.Units.MassUnit.kg
import ru.lextop.steamcalculator.steam.quantity.Units.MassUnit.lb
import ru.lextop.steamcalculator.steam.quantity.Units.Pressure.lb_in2
import ru.lextop.steamcalculator.steam.quantity.Units.Ratio.ratio
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.BTU_lb
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.cal15_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.cal_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.calth_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.kJ_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.kcal15_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.kcal_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificEnergy.kcalth_kg
import ru.lextop.steamcalculator.steam.quantity.Units.SpecificVolume.ft3_lb
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature.K
import ru.lextop.steamcalculator.steam.quantity.Units.Temperature.Ra

object Units{
    object MassUnit : BaseUnit(kg = 1) {
        val kg = createAlias(Pair("kg", R.string.kg))
        val lb = kg / 0.45359237 addWith Pair("lb", R.string.lb)
    }

    object EnergyUnit : BaseUnit(kg = 1, m = 2, s = -2) {
        val J = createAlias(Pair("J", R.string.J))
        val kJ = k(J) addWith Pair(byDefault, R.string.kJ)
        val erg = J / 1e-7 addWith Pair("erg", R.string.erg)
        val cal = J / 4.1868 addWith Pair("cal", R.string.cal)
        val calth = J / 4.184 addWith Pair("calth", R.string.calth)
        val cal15 = J / 1.1855 addWith Pair("cal15", R.string.cal15)
        val kcal = k(cal) addWith Pair(byDefault, R.string.kcal)
        val kcalth = k(calth) addWith Pair(byDefault, R.string.kcalth)
        val kcal15 = k(cal15) addWith Pair(byDefault, R.string.kcal15)
        val BTU = J / 1055.05585262 addWith Pair("BTU", R.string.BTU)
        val kWh = J / 3.6e6 addWith Pair("kWh", R.string.kWh)
    }

    object SpecificEnergy : BaseUnit(m = 2, s = -2) {
        val J_kg = createAlias(Pair("J/kg", R.string.J_kg))
        val kJ_kg = kJ / kg addWith Pair(byDefault, R.string.kJ_kg)
        val cal_kg = cal / kg addWith Pair(byDefault, R.string.cal_kg)
        val calth_kg = calth / kg addWith Pair(byDefault, R.string.calth_kg)
        val cal15_kg = cal15 / kg addWith Pair(byDefault, R.string.cal15_kg)
        val kcal_kg = kcal / kg addWith Pair(byDefault, R.string.kcal_kg)
        val kcalth_kg = kcalth / kg addWith Pair(byDefault, R.string.kcalth_kg)
        val kcal15_kg = kcal15 / kg addWith Pair(byDefault, R.string.kcal15_kg)
        val BTU_lb = BTU / lb addWith Pair(byDefault, R.string.BTU_lb)
    }

    object SpecificHeatCapacity : BaseUnit(m = 2, K = -1, s = -2){
        val J_kgK = createAlias(Pair("J/(kg*K)", R.string.J_kgK))
        val kJ_kgK = kJ_kg / K addWith Pair("kJ/(kg*K)", R.string.kJ_kgK)
        val cal_kgK = cal_kg / K addWith Pair("cal/(kg*K)", R.string.cal_kgK)
        val calth_kgK = calth_kg / K addWith Pair("calth/(kg*K)", R.string.calth_kgK)
        val cal15_kgK = cal15_kg / K addWith Pair("cal15/(kg*K)", R.string.cal15_kgK)
        val kcal_kgK = kcal_kg / K addWith Pair("kcal/(kg*K)", R.string.kcal_kgK)
        val kcalth_kgK = kcalth_kg / K addWith Pair("kcalth/(kg*K)", R.string.kcalth_kgK)
        val kcal15_kgK = kcal15_kg / K addWith Pair("kcal15/(kg*K)", R.string.kcal15_kgK)
        val BTU_lbR = BTU_lb / Ra addWith Pair("BTU/(lb*R)", R.string.BTU_lbR)
    }

    object Pressure : BaseUnit(kg = 1, m = -1, s = -2) {
        val Pa = createAlias(Pair("Pa", R.string.Pa))
        val kPa = k(Pa) addWith Pair(byDefault, R.string.kPa)
        val MPa = M(Pa) addWith Pair(byDefault, R.string.MPa)
        val bar = Pa / 1e5 addWith Pair("bar", R.string.bar)
        val at = Pa / 98066.5 addWith Pair("at", R.string.at)
        val kgf_cm2 = at addWith Pair("kgf/cm2", R.string.kgf_cm2)
        val atm = Pa / 101325.0 addWith Pair("atm", R.string.atm)
        val psi = Pa / 6894.76 addWith Pair("psi", R.string.psi)
        val lb_in2 = psi addWith Pair("lb/in2", R.string.lb_in2)
        val mmHg = Pa / 133.322 addWith Pair("mmHg", R.string.mmHg)
        val mH2O = Pa / 9806.65 addWith Pair("mH2O", R.string.mH2O)
    }

    object Temperature : BaseUnit(K = 1) {
        val K = createAlias(Pair("K", R.string.K))
        val C = K - 273.15 addWith Pair("C", R.string.C)
        val F = 9.0 / 5.0 * C + 32.0 addWith Pair("F", R.string.F)
        val Ra = F + 459.57 addWith Pair("R", R.string.Ra)
        val D = (100.0 - C) * 3.0 / 2.0 addWith Pair("D", R.string.D)
        val N = 33.0 / 100.0 * C addWith Pair("N", R.string.N)
        val Re = 0.8 * C addWith Pair("Re", R.string.Re)
        val Ro = 21.0 / 40.0 * C + 7.5 addWith Pair("Ro", R.string.Ro)

    }

    object Ratio : BaseUnit() {
        val ratio = createAlias(Pair("_", R.string.ratio))
        val percent = ratio * 100.0 addWith Pair("%", R.string.percent)
    }

    object Density : BaseUnit(kg = 1, m = -3) {
        val kg_m3 = createAlias(Pair("kg/m3", R.string.kg_m3))
        val lb_ft3 = ratio / ft3_lb addWith Pair("lb/ft3", R.string.lb_ft3)
    }

    object DynamicViscosity : BaseUnit(kg = 1, s = -1, m = -1){
        val Pas = createAlias(Pair("Pa*s", R.string.Pas))
        val cP = Pas * 1e3 addWith Pair("cP", R.string.cP)
    }

    object Temperature_1 : BaseUnit(K = -1){
        val K_1 = createAlias(Pair("1/K", R.string.K_1))
        val R_1 = ratio / Ra addWith Pair("1/R", R.string.R_1)
    }

    object Compressibility : BaseUnit(kg = -1, m = 1, s = 2){
        val Pa_1 = createAlias(Pair("1/Pa", R.string.Pa_1))
        val MPa_1 = Pa_1 * 1e6 addWith Pair("1/MPa", R.string.MPa_1)
        val in2_lb = ratio / lb_in2 addWith Pair("in2_lb", R.string.in2_lb)
    }

    object KinematicViscosity : BaseUnit(m = 2, s = -1){
        val m2_s = createAlias(Pair("m2/s", R.string.m2_s))
        val cSt = m2_s * 1e6 addWith Pair("cSt", R.string.cSt)
    }

    object SpecificVolume: BaseUnit(m = 3, kg = -1){
        val m3_kg = createAlias(Pair("m3/kg", R.string.m3_kg))
        val ft3_lb = m3_kg * 16.01846353 addWith Pair("ft3/lb", R.string.ft3_lb)
    }

    object Speed: BaseUnit(m = 1, s = 1){
        val m_s = createAlias(Pair("m/s", R.string.m_s))
        val ft_s = m_s / 0.3048 addWith Pair("ft/s", R.string.ft_s)
    }

    object SurfaceTension: BaseUnit(kg = 1, s = -2){
        val N_m = createAlias(Pair("N/m", R.string.N_m))
        val kg_s2 = N_m addWith Pair("kg/s2", R.string.kg_s2)
        val lbf_ft = kg_s2 / 14.5939029 addWith Pair("lbf/ft", R.string.lbf_ft)
    }

    object ThermalConductivity: BaseUnit(kg = 1, m = 1, s = -3, K = -1){
        val W_mK = createAlias(Pair("W/(m*K)", R.string.W_mK))
        val BTU_hrftR = W_mK / 1.7295772056 addWith Pair("BTU/(hr*ft*R)", R.string.BTU_hrftR)
    }
}