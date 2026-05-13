package com.example.financeapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.databinding.ItemTransactionBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList != null ? transactionList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Transaction getTransactionAt(int position) {
        return transactionList.get(position);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.bind(transactionList.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        public TransactionViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(transactionList.get(position));
                }
            });
        }

        void bind(Transaction transaction) {
            binding.tvTitle.setText(transaction.getTitle());
            binding.tvCategory.setText(transaction.getCategory());
            binding.tvDate.setText(transaction.getDate());
            binding.tvType.setText(transaction.getType());

            String amountText = String.format(Locale.getDefault(), "%s %.2f",
                    transaction.getType().equals("Income") ? "+" : "-",
                    transaction.getAmount());
            binding.tvAmount.setText(amountText);

            if ("Income".equals(transaction.getType())) {
                binding.tvAmount.setTextColor(Color.parseColor("#2E7D32"));
                binding.tvType.setBackgroundResource(R.drawable.bg_chip_income);
            } else {
                binding.tvAmount.setTextColor(Color.parseColor("#C62828"));
                binding.tvType.setBackgroundResource(R.drawable.bg_chip_expense);
            }
        }
    }
}