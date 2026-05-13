package com.example.financeapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE title LIKE '%' || :query || '%' ORDER BY id DESC")
    LiveData<List<Transaction>> searchTransactions(String query);

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY id DESC")
    LiveData<List<Transaction>> getTransactionsByTypeLive(String type);

    @Query("SELECT * FROM transactions WHERE title LIKE '%' || :query || '%' AND type = :type ORDER BY id DESC")
    LiveData<List<Transaction>> searchTransactionsByType(String query, String type);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income'")
    LiveData<Double> getTotalIncome();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense'")
    LiveData<Double> getTotalExpense();

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    Transaction getTransactionById(int id);

    @Query("SELECT * FROM transactions WHERE type = :type")
    List<Transaction> getTransactionsByType(String type);

    @Query("SELECT * FROM transactions ORDER BY id DESC LIMIT 5")
    LiveData<List<Transaction>> getLatestTransactions();

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'Expense' GROUP BY category")
    List<CategoryTotal> getExpenseTotalsByCategory();

    class CategoryTotal {
        public String category;
        public float total;
    }
}