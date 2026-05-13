package com.example.financeapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<List<Transaction>> latestTransactions;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpense;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
        latestTransactions = repository.getLatestTransactions();
        totalIncome = repository.getTotalIncome();
        totalExpense = repository.getTotalExpense();
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
        return repository.searchTransactions(query);
    }

    public LiveData<List<Transaction>> getTransactionsByTypeLive(String type) {
        return repository.getTransactionsByTypeLive(type);
    }

    public LiveData<List<Transaction>> searchTransactionsByType(String query, String type) {
        return repository.searchTransactionsByType(query, type);
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void update(Transaction transaction) {
        repository.update(transaction);
    }

    public void delete(Transaction transaction) {
        repository.delete(transaction);
    }

    public Transaction getTransactionByIdSync(int id) {
        return repository.getTransactionByIdSync(id);
    }

    public List<TransactionDao.CategoryTotal> getExpenseTotalsByCategorySync() {
        return repository.getExpenseTotalsByCategorySync();
    }

    public List<Transaction> getTransactionsByTypeSync(String type) {
        return repository.getTransactionsByTypeSync(type);
    }
}