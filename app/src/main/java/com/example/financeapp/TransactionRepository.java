package com.example.financeapp;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpense;
    private final LiveData<List<Transaction>> latestTransactions;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
        totalIncome = transactionDao.getTotalIncome();
        totalExpense = transactionDao.getTotalExpense();
        latestTransactions = transactionDao.getLatestTransactions();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getLatestTransactions() {
        return latestTransactions;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<List<Transaction>> searchTransactions(String query) {
        return transactionDao.searchTransactions(query);
    }

    public LiveData<List<Transaction>> getTransactionsByTypeLive(String type) {
        return transactionDao.getTransactionsByTypeLive(type);
    }

    public LiveData<List<Transaction>> searchTransactionsByType(String query, String type) {
        return transactionDao.searchTransactionsByType(query, type);
    }

    public void insert(Transaction transaction) {
        executorService.execute(() -> transactionDao.insert(transaction));
    }

    public void update(Transaction transaction) {
        executorService.execute(() -> transactionDao.update(transaction));
    }

    public void delete(Transaction transaction) {
        executorService.execute(() -> transactionDao.delete(transaction));
    }

    public Transaction getTransactionByIdSync(int id) {
        try {
            return executorService.submit(() -> transactionDao.getTransactionById(id)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TransactionDao.CategoryTotal> getExpenseTotalsByCategorySync() {
        try {
            return executorService.submit(transactionDao::getExpenseTotalsByCategory).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transaction> getTransactionsByTypeSync(String type) {
        try {
            return executorService.submit(() -> transactionDao.getTransactionsByType(type)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}