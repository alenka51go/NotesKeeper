package ru.kudasheva.noteskeeper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> {
    private final List<NoteShortCard> noteList = new ArrayList<>();
    private final OnNoteClickListener OnNoteClickListener;

    public CustomRecyclerAdapter(OnNoteClickListener onUserClickListener) {
        this.OnNoteClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.short_note_info, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bind(noteList.get(position));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void setItems(Collection<NoteShortCard> notes) {
        noteList.addAll(notes);
        notifyDataSetChanged();
    }

    public void clearItems() {
        noteList.clear();
        notifyDataSetChanged();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private final TextView notesHeader;
        private final TextView date;

        public CustomViewHolder(View itemView) {
            super(itemView);
            notesHeader = itemView.findViewById(R.id.notes_header);
            date = itemView.findViewById(R.id.notes_date);

            itemView.setOnClickListener(v -> {
                NoteShortCard note = noteList.get(getLayoutPosition());
                OnNoteClickListener.onNoteClick(note);
            });
        }

        public void bind(NoteShortCard noteShortCard) {
            notesHeader.setText(noteShortCard.getHeader());
            date.setText(noteShortCard.getDate());
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(NoteShortCard note);
    }
}
