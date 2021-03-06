package steam.quantities

import quantityvalue.Quantity
import steam.units.*

val Pressure = Quantity("Pressure", "P", Pa.dimension)
val Temperature = quantityvalue.quantities.base.Temperature
val SpecificEnthalpy = Quantity("Specific Enthalpy", "h", J_kg.dimension)
val SpecificEntropy = Quantity("Specific Entropy", "s", J_kgK.dimension)
val VapourFraction = Quantity("Vapour Fraction", "x", ratio.dimension)
val SpecificVolume = Quantity("Specific Volume", "v", m3_kg.dimension)
val Density = Quantity("Density", "rho", kg_m3.dimension)
val SpeedOfSound = Quantity("Speed of Sound", "w", m_s.dimension)
val SpecificIsobaricHeatCapacity = Quantity("Specific Isobaric Heat Capacity", "cp", J_kgK.dimension)
val SpecificIsochoricHeatCapacity = Quantity("Specific Isochoric Heat Capacity", "cv", J_kgK.dimension)
val SpecificEnthalpyOfVaporization = Quantity("Specific Enthalpy of Vaporization", "hvap", J_kg.dimension)
val ThermalConductivity = Quantity("Thermal Conductivity", "lambda", W_mK.dimension)
val ThermalDiffusivity = Quantity("Thermal Diffusivity", "k", m2_s.dimension)
val PrandtlNumber = Quantity("Prandtl Number", "Pr", ratio.dimension)
val DynamicViscosity = Quantity("Dynamic Viscosity", "eta", Pas.dimension)
val KinematicViscosity = Quantity("Kinematic Viscosity", "nu", m2_s.dimension)
val SurfaceTension = Quantity("Surface Tension", "sigma", N_m.dimension)
val IsobaricCubicExpansionCoefficient = Quantity("Isobaric Cubic Expansion Coefficient", "av", K_1.dimension)
val IsothermalCompressibility = Quantity("Isothermal Compressibility", "kT", Pa_1.dimension)
val RelativePermittivity = Quantity("Relative Permittivity", "epsilon", ratio.dimension)
val SpecificInternalEnergy = Quantity("Specific Internal Energy", "u", J_kg.dimension)
val SpecificGibbsFreeEnergy = Quantity("Specific Gibbs Free Energy", "g", J_kg.dimension)
val Wavelength = Quantity("Wavelength", "lambda", m.dimension)
val RefractiveIndex = Quantity("Refractive Index", "n", ratio.dimension)
