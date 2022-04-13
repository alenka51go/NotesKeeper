package ru.kudasheva.noteskeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MultipleTypesAdapter extends RecyclerView.Adapter<MultipleTypesAdapter.AbstractViewHolder> {
    private final List<RowType> dataSet = new ArrayList<>();

    void setItems(List<RowType> dates) {
        dataSet.addAll(dates);
    }

    void clearItems() {
        dataSet.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == RowType.NOTE_ROW_TYPE) {
            View view = inflater.inflate(R.layout.note, parent, false);
            return new NoteViewHolder(view);
        }
        View view = inflater.inflate(R.layout.comment, parent, false);
        return new CommentViewHolder(view);
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
        public TextView userName;
        public TextView date;
        public TextView textBody;

        abstract int getUserName();
        abstract int getDate();
        abstract int getTextBody();

        public AbstractViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(getUserName());
            date = itemView.findViewById(getDate());
            textBody = itemView.findViewById(getTextBody());
        }

        abstract void bindContent(RowType item);
    }

    public static class NoteViewHolder extends AbstractViewHolder {
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void bindContent(RowType item) {
            NoteRowType actualItem = (NoteRowType)item;
            userName.setText(actualItem.getName());
            date.setText(actualItem.getDate());
            textBody.setText(actualItem.getText());
        }

        @Override
        int getUserName() {
            return R.id.user_name_browse;
        }
        @Override
        int getDate() {
            return R.id.date_browse;
        }
        @Override
        int getTextBody() {
            return R.id.note_body_browse;
        }
    }

    public static class CommentViewHolder extends AbstractViewHolder {
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        @Override
        void bindContent(RowType item) {
            CommentRowType actualItem = (CommentRowType)item;
            userName.setText(actualItem.getName());
            date.setText(actualItem.getDate());
            textBody.setText(actualItem.getText());
        }
        @Override
        int getUserName() {
            return R.id.text_comment;
        }
        @Override
        int getDate() {
            return R.id.date_comment;
        }
        @Override
        int getTextBody() {
            return R.id.name_comment;
        }
    }
}
