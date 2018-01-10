package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.R

val Pressure = DerivedQuantity(
        "Pressure", "P", Unit,
        R.string.Pressure, R.string.P)
val Temperature = Quantity(
        "Temperature", "T", Units.Temperature,
        R.string.Temperature, R.string.T)
val SpecificEnthalpy = DerivedQuantity(
        "Specific Enthalpy", "h", Units.SpecificEnergy,
        R.string.SpecificEnthalpy, R.string.h)
val SpecificEntropy = DerivedQuantity(
        "Specific Entropy", "T", Units.SpecificHeatCapacity,
        R.string.SpecificEntropy, R.string.s)
val VapourFraction = DerivedQuantity(
        "Vapour Fraction", "x", Units.Ratio,
        R.string.VapourFraction, R.string.x)
val SpecificVolume = DerivedQuantity(
        "Specific Volume", "v", Units.SpecificVolume,
        R.string.SpecificVolume, R.string.v)
val Density = DerivedQuantity(
        "Density", "rho", Units.Density,
        R.string.Density, R.string.rho)
val SpeedOfSound = DerivedQuantity(
        "Speed of Sound", "w", Units.Speed,
        R.string.SpeedOfSound, R.string.w)
val SpecificIsobaricHeatCapacity = DerivedQuantity(
        "Specific Isobaric Heat Capacity", "cp", Units.SpecificHeatCapacity,
        R.string.SpecificIsobaricHeatCapacity, R.string.cp)
val SpecificIsochoricHeatCapacity = DerivedQuantity(
        "Specific Isochoric Heat Capacity", "cv", Units.SpecificHeatCapacity,
        R.string.SpecificIsochoricHeatCapacity , R.string.cv)
val SpecificEnthalpyOfVaporization = DerivedQuantity(
        "Specific Enthalpy of Vaporization", "hvap", Units.SpecificEnergy,
        R.string.SpecificEnthalpyOfVaporization, R.string.hvap)
val ThermalConductivity = DerivedQuantity(
        "Thermal Conductivity", "lambda", Units.ThermalConductivity,
        R.string.ThermalConductivity, R.string.lambda)
val ThermalDiffusivity = DerivedQuantity(
        "Thermal Diffusivity", "k", Units.KinematicViscosity,
        R.string.ThermalDiffusivity, R.string.k)
val PrandtlNumber = DerivedQuantity(
        "Prandtl Number", "Pr", Units.Ratio,
        R.string.PrandtlNumber, R.string.Pr)
val DynamicViscosity = DerivedQuantity(
        "Dynamic Viscosity", "eta", Units.DynamicViscosity,
        R.string.DynamicViscosity, R.string.eta)
val KinematicViscosity = DerivedQuantity(
        "Kinematic Viscosity", "nu", Units.KinematicViscosity,
        R.string.KinematicViscosity, R.string.nu)
val SurfaceTension = DerivedQuantity(
        "Surface Tension", "sigma", Units.SurfaceTension,
        R.string.SurfaceTension, R.string.sigma)
val IsobaricCubicExpansionCoefficient = DerivedQuantity(
        "Isobaric Cubic Expansion Coefficient", "av", Units.Temperature_1,
        R.string.IsobaricCubicExpansionCoefficient, R.string.av)
val IsothermalCompressibility = DerivedQuantity(
        "Isothermal Compressibility", "kT", Units.Compressibility,
        R.string.IsothermalCompressibility, R.string.kT)
val RelativePermittivity = DerivedQuantity(
        "Relative Permittivity", "epsilon", Units.Ratio,
        R.string.RelativePermittivity, R.string.epsilon)
val SpecificInternalEnergy = DerivedQuantity(
        "Specific Internal Energy", "u", Units.SpecificEnergy,
        R.string.SpecificInternalEnergy, R.string.u)
val SpecificGibbsFreeEnergy = DerivedQuantity(
        "Specific Gibbs Free Energy", "g", Units.SpecificEnergy,
        R.string.SpecificGibbsFreeEnergy, R.string.g)
