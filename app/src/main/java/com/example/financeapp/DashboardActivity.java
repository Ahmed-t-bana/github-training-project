package com.example.financeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.databinding.ActivityDashboardBinding;

import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private TransactionViewModel transactionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        final double[] incomeValue = {0.0};
        final double[] expenseValue = {0.0};

        transactionViewModel.getTotalIncome().observe(this, income -> {
            incomeValue[0] = income != null ? income : 0.0;
            binding.tvIncome.setText(String.format(Locale.getDefault(), "%.2f", incomeValue[0]));
            binding.tvBalance.setText(String.format(Locale.getDefault(), "%.2f", (incomeValue[0] - expenseValue[0])));
        });

        transactionViewModel.getTotalExpense().observe(this, expense -> {
            expenseValue[0] = expense != null ? expense : 0.0;
            binding.tvExpense.setText(String.format(Locale.getDefault(), "%.2f", expenseValue[0]));
            binding.tvBalance.setText(String.format(Locale.getDefault(), "%.2f", (incomeValue[0] - expenseValue[0])));
        });

        transactionViewModel.getLatestTransactions().observe(this, transactions -> updateRecentSection(transactions));

        binding.btnAddTransaction.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AddEditTransactionActivity.class)));

        binding.btnViewTransactions.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, TransactionListActivity.class)));

        binding.btnReports.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ReportsActivity.class)));

        binding.btnSettings.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class)));
    }

    private void updateRecentSection(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            binding.tvRecentHint.setText("No transactions yet");
        } else {
            binding.tvRecentHint.setText("Recent transactions: " + transactions.size());
        }
    }
}