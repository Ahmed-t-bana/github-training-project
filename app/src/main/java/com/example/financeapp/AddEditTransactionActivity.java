package com.example.financeapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.databinding.ActivityAddEditTransactionBinding;

import java.util.Calendar;
import java.util.Locale;

public class AddEditTransactionActivity extends AppCompatActivity {

    private ActivityAddEditTransactionBinding binding;
    private TransactionViewModel transactionViewModel;
    private int transactionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setTitle("Add Transaction");

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        String[] types = {"Income", "Expense"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                types
        );
        binding.spinnerType.setAdapter(typeAdapter);
        binding.spinnerType.setText(types[0], false);

        String[] categories = {"Food", "Transport", "Rent", "Bills", "Salary", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                categories
        );
        binding.spinnerCategory.setAdapter(categoryAdapter);
        binding.spinnerCategory.setText(categories[0], false);

        binding.etDate.setOnClickListener(v -> showDatePicker());

        if (getIntent().hasExtra("transaction_id")) {
            transactionId = getIntent().getIntExtra("transaction_id", -1);
            binding.toolbar.setTitle("Edit Transaction");
            loadTransactionData(transactionId);
        } else {
            setTodayAsDefaultDate();
        }

        binding.btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setTodayAsDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        String today = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
        binding.etDate.setText(today);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) ->
                        binding.etDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void loadTransactionData(int id) {
        new Thread(() -> {
            Transaction transaction = transactionViewModel.getTransactionByIdSync(id);
            if (transaction != null) {
                runOnUiThread(() -> {
                    binding.etTitle.setText(transaction.getTitle());
                    binding.etAmount.setText(String.valueOf(transaction.getAmount()));
                    binding.etDate.setText(transaction.getDate());
                    binding.etNote.setText(transaction.getNote());
                    setDropdownValue(binding.spinnerType, transaction.getType());
                    setDropdownValue(binding.spinnerCategory, transaction.getCategory());
                });
            }
        }).start();
    }

    private void setDropdownValue(AutoCompleteTextView view, String value) {
        view.setText(value, false);
    }

    private void saveTransaction() {
        String title = binding.etTitle.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();
        String type = binding.spinnerType.getText().toString().trim();
        String category = binding.spinnerCategory.getText().toString().trim();
        String date = binding.etDate.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();

        if (title.isEmpty()) {
            binding.etTitle.setError("Title required");
            return;
        }

        if (amountStr.isEmpty()) {
            binding.etAmount.setError("Amount required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (Exception e) {
            binding.etAmount.setError("Invalid amount");
            return;
        }

        if (amount <= 0) {
            binding.etAmount.setError("Amount must be greater than 0");
            return;
        }

        if (date.isEmpty()) {
            binding.etDate.setError("Date required");
            return;
        }

        if (type.isEmpty()) {
            binding.spinnerType.setError("Type required");
            return;
        }

        if (category.isEmpty()) {
            binding.spinnerCategory.setError("Category required");
            return;
        }

        Transaction transaction = new Transaction(title, amount, type, category, date, note);

        if (transactionId == -1) {
            transactionViewModel.insert(transaction);
            Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show();
        } else {
            transaction.setId(transactionId);
            transactionViewModel.update(transaction);
            Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}