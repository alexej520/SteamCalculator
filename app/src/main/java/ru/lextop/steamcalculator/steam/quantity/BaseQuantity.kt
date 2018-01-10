package ru.lextop.steamcalculator.steam.quantity

import ru.lextop.steamcalculator.steam.unit.CoherentUnt

class BaseQuantity internal constructor(name: String, symbol: String, coherentUnit: CoherentUnt) :
        Quantity(name, symbol, coherentUnit)
