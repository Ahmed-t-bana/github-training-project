package com.example.financeapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.databinding.ActivityReportsBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private ActivityReportsBinding binding;
    private TransactionViewModel transactionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        transactionViewModel.getTotalIncome().observe(this, income -> {
            double value = income != null ? income : 0.0;
            binding.tvTotalIncome.setText(String.format(Locale.getDefault(), "Total Income: %.2f", value));
        });

        transactionViewModel.getTotalExpense().observe(this, expense -> {
            double value = expense != null ? expense : 0.0;
            binding.tvTotalExpense.setText(String.format(Locale.getDefault(), "Total Expense: %.2f", value));
        });

        loadPieChart();
        loadBarChart();
    }

    private void loadPieChart() {
        new Thread(() -> {
            List<TransactionDao.CategoryTotal> categoryTotals = transactionViewModel.getExpenseTotalsByCategorySync();
            ArrayList<PieEntry> entries = new ArrayList<>();

            if (categoryTotals != null) {
                for (TransactionDao.CategoryTotal total : categoryTotals) {
                    entries.add(new PieEntry(total.total, total.category));
                }
            }

            runOnUiThread(() -> {
                PieDataSet dataSet = new PieDataSet(entries, "Expenses by Category");
                dataSet.setColors(
                        Color.parseColor("#EF5350"),
                        Color.parseColor("#42A5F5"),
                        Color.parseColor("#66BB6A"),
                        Color.parseColor("#FFA726"),
                        Color.parseColor("#AB47BC"),
                        Color.parseColor("#26A69A")
                );
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(12f);

                PieData data = new PieData(dataSet);
                binding.pieChart.setData(data);
                binding.pieChart.getDescription().setEnabled(false);
                binding.pieChart.setCenterText("Expenses");
                binding.pieChart.setCenterTextColor(Color.BLACK);
                binding.pieChart.setEntryLabelColor(Color.BLACK);
                binding.pieChart.invalidate();

                Legend legend = binding.pieChart.getLegend();
                legend.setTextColor(Color.BLACK);
            });
        }).start();
    }

    private void loadBarChart() {
        new Thread(() -> {
            List<Transaction> expenses = transactionViewModel.getTransactionsByTypeSync("Expense");
            ArrayList<BarEntry> entries = new ArrayList<>();

            if (expenses != null) {
                for (int i = 0; i < expenses.size(); i++) {
                    entries.add(new BarEntry(i, (float) expenses.get(i).getAmount()));
                }
            }

            runOnUiThread(() -> {
                BarDataSet dataSet = new BarDataSet(entries, "Expense Entries");
                dataSet.setColor(Color.parseColor("#00897B"));
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(11f);

                BarData data = new BarData(dataSet);
                binding.barChart.setData(data);
                binding.barChart.getDescription().setEnabled(false);
                binding.barChart.getAxisLeft().setTextColor(Color.BLACK);
                binding.barChart.getAxisRight().setEnabled(false);

                XAxis xAxis = binding.barChart.getXAxis();
                xAxis.setTextColor(Color.BLACK);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                binding.barChart.getLegend().setTextColor(Color.BLACK);
                binding.barChart.invalidate();
            });
        }).start();
    }
}