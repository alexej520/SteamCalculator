package ru.lextop.steamcalculator.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import javax.inject.Singleton

@Singleton
@Dao
interface SteamDao {
    @Query("SELECT * from ${ViewUnit.TABLE_NAME}")
    fun getViewUnitsLive(): LiveData<List<ViewUnit>>

    @Update
    fun insertViewUnit(viewUnit: ViewUnit)

    @Query("SELECT * from ${EditUnit.TABLE_NAME}")
    fun getEditUnitsLive(): LiveData<List<EditUnit>>

    @Update
    fun insertEditUnit(editUnit: EditUnit)

    @Query("SELECT * from ${SelectedQuantityValue.TABLE_NAME}")
    fun getSelectedQuantityValue(): LiveData<List<SelectedQuantityValue>>

    @Update
    fun insertSelectedQuantityValue(vararg selectedQuantityValue: SelectedQuantityValue)
}