package quantityvalue.units.temperature

import quantityvalue.*
import quantityvalue.units.base.K

val C = K withName "C"
val F = 9.0 / 5.0 * K withName "F"
val R = F withName "R"
val D = -3.0 / 2.0 * K withName "D"
val N = 33.0 / 100.0 * K withName "N"
val Re = 0.8 * K withName "Re"
val Ro = 21.0 / 40.0 * K withName "Ro"

val absoluteC: UnitConverter = OffsetConverter(C, -273.15)
val absoluteF: UnitConverter = OffsetConverter(F, -273.15 * 9.0 / 5.0 + 32.0)
val absoluteD: UnitConverter = OffsetConverter(D, 373.15 * (3.0 / 2.0))
val absoluteN: UnitConverter = OffsetConverter(N, -273.15 * 33.0 / 100.0)
val absoluteRe: UnitConverter = OffsetConverter(Re, -273.15 * 0.8)
val absoluteRo: UnitConverter = OffsetConverter(Ro, -273.15 * 21.0 / 40.0 + 7.5)