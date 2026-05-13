package com.example.financeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.databinding.ActivityTransactionListBinding;

import java.util.List;

public class TransactionListActivity extends AppCompatActivity {

    private ActivityTransactionListBinding binding;
    private TransactionViewModel transactionViewModel;
    private TransactionAdapter adapter;
    private LiveData<List<Transaction>> currentSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new TransactionAdapter();
        binding.recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewTransactions.setHasFixedSize(true);
        binding.recyclerViewTransactions.setAdapter(adapter);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Income", "Expense"}
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilterType.setAdapter(filterAdapter);

        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        observeCurrentFilter();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                observeCurrentFilter();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.spinnerFilterType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                observeCurrentFilter();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(TransactionListActivity.this, AddEditTransactionActivity.class)));

        adapter.setOnItemClickListener(transaction -> {
            Intent intent = new Intent(TransactionListActivity.this, AddEditTransactionActivity.class);
            intent.putExtra("transaction_id", transaction.getId());
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Transaction transaction = adapter.getTransactionAt(viewHolder.getAdapterPosition());
                transactionViewModel.delete(transaction);
            }
        }).attachToRecyclerView(binding.recyclerViewTransactions);
    }

    private void observeCurrentFilter() {
        if (currentSource != null) {
            currentSource.removeObservers(this);
        }

        String query = binding.etSearch.getText().toString().trim();
        String type = binding.spinnerFilterType.getSelectedItem().toString();

        if (type.equals("All") && query.isEmpty()) {
            currentSource = transactionViewModel.getAllTransactions();
        } else if (type.equals("All")) {
            currentSource = transactionViewModel.searchTransactions(query);
        } else if (query.isEmpty()) {
            currentSource = transactionViewModel.getTransactionsByTypeLive(type);
        } else {
            currentSource = transactionViewModel.searchTransactionsByType(query, type);
        }

        currentSource.observe(this, transactions -> {
            adapter.setTransactionList(transactions);
            updateEmptyState(transactions);
        });
    }

    private void updateEmptyState(List<Transaction> transactions) {
        boolean isEmpty = transactions == null || transactions.isEmpty();
        binding.recyclerViewTransactions.setVisibility(isEmpty ? android.view.View.GONE : android.view.View.VISIBLE);
        binding.layoutEmpty.setVisibility(isEmpty ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}