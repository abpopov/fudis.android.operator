package thapl.com.fudis.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import thapl.com.fudis.data.local.model.OrderLocal

@Database(entities = [OrderLocal::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: MutableList<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}

@Dao
interface OrderDao : BaseDao<OrderLocal> {

    @Query("SELECT * FROM orders ORDER BY order_date DESC")
    fun getOrdersLive(): LiveData<MutableList<OrderLocal>>

    @Query("SELECT * FROM orders ORDER BY order_date DESC")
    fun getOrders(): MutableList<OrderLocal>

    @Query("DELETE FROM orders")
    fun clearData()
}