package ru.lextop.steamcalculator.list

interface Filter {
    fun filter(item: Any?): Boolean
}