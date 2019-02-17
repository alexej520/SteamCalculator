package ru.lextop.steamcalculator.list

interface Adapter<T> {
    fun getItem(position: Int): T
}