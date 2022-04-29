package ru.kudasheva.noteskeeper.notescroll;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.kudasheva.noteskeeper.databinding.ShortNoteInfoBinding;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> {
    private final List<NoteShortCard> noteList = new ArrayList<>();
    private final OnNoteClickListener onNoteClickListener;

    public CustomRecyclerAdapter(OnNoteClickListener onUserClickListener) {
        this.onNoteClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ShortNoteInfoBinding binding = ShortNoteInfoBinding.inflate(inflater, parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        NoteShortCard note = noteList.get(position);
        holder.binding.setShortNote(note);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void setItems(Collection<NoteShortCard> notes) {
        noteList.addAll(notes);
        notifyDataSetChanged();
    }

     class CustomViewHolder extends RecyclerView.ViewHolder {
        ShortNoteInfoBinding binding;

        public CustomViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(v -> {
                NoteShortCard note = noteList.get(getLayoutPosition());
                onNoteClickListener.onNoteClick(note);
            });
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(NoteShortCard note);
    }
}
