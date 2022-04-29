package ru.kudasheva.noteskeeper.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.CommentBinding;
import ru.kudasheva.noteskeeper.databinding.NoteBinding;

public class MultipleTypesAdapter extends RecyclerView.Adapter<MultipleTypesAdapter.AbstractViewHolder> {
    private List<InfoCard> dataSet = new ArrayList<>();

    void setItems(List<InfoCard> dates) {
        dataSet = dates;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == InfoCard.NOTE_ROW_TYPE) {
            NoteBinding binding = NoteBinding.inflate(inflater, parent, false);
            return new NoteViewHolder(binding.getRoot());
        }
        CommentBinding binding = CommentBinding.inflate(inflater, parent, false);
        return new CommentViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        holder.bindContent(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public abstract static class AbstractViewHolder extends RecyclerView.ViewHolder {
        public AbstractViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindContent(InfoCard item);
    }

    public static class NoteViewHolder extends AbstractViewHolder {
        NoteBinding binding;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        void bindContent(InfoCard item) {
            binding.setFullNote((NoteFullCard) item);
        }
    }

    public static class CommentViewHolder extends AbstractViewHolder {
        CommentBinding binding;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        void bindContent(InfoCard item) {
            binding.setComment((CommentInfoCard) item);
        }
    }
}
