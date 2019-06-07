package com.nhinguyen.translate.ROOM

import android.arch.persistence.room.*
@Dao
interface WordDAO {
    @Query("SELECT * FROM word")
    fun getAll():List<Word>

    @Query("SELECT * FROM word WHERE id=:id")
    fun findById(id: Int): Word

    @Insert
    fun insertAll(vararg todo: Word) : List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: Word):Long

    @Delete
    fun delete(toto: Word)

    @Update
    fun update(task : Word)

    @Query("DELETE FROM word")
    fun deleteAllTask()
}