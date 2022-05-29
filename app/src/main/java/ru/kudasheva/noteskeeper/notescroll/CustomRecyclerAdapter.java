package ru.kudasheva.noteskeeper.notescroll;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ru.kudasheva.noteskeeper.data.ChangeListener;
import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.databinding.ShortNoteInfoBinding;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> {
    private static final String TAG = CustomRecyclerAdapter.class.getSimpleName();

    private List<NoteShortCard> noteList = new ArrayList<>();
    private final OnNoteClickListener onNoteClickListener;
    private final Activity parentActivity;

    private int getIndexOfItemByDocumentId(String documentId) {
        for (int i = 0; i < noteList.size(); i++) {
            if (noteList.get(i).getId().equals(documentId)) {
                return i;
            }
        }
        return -1;
    }

    public CustomRecyclerAdapter(OnNoteClickListener onUserClickListener, Activity activity) {
        this.onNoteClickListener = onUserClickListener;
        parentActivity = activity;

        ChangeListener changeListener = (documentId, event, properties) -> {
            Log.d(TAG, "Document with id " + documentId + " was " + event);
            if (properties != null && Objects.equals(properties.get("type"), "comment")) {
                return;
            }
            int pos = getIndexOfItemByDocumentId(documentId);

            parentActivity.runOnUiThread(() -> {
                if (pos == -1) {
                    switch (event) {
                        case DELETED:
                            break;
                        case UPDATED:
                            noteList.add(new NoteShortCard(documentId,
                                    (String) properties.get("title"),
                                    (String) properties.get("date")));
                            notifyItemChanged(noteList.size() - 1);
                            Log.d(TAG, "Item " + documentId + " inserted to position " + (noteList.size() - 1));
                            break;
                    }
                } else {
                    switch (event) {
                        case DELETED:
                            noteList.remove(pos);
                            notifyItemRemoved(pos);
                            Log.d(TAG, "Item at position " + pos + " removed");
                            break;
                        case UPDATED:
                            noteList.set(pos, new NoteShortCard(documentId,
                                    (String) properties.get("title"),
                                    (String) properties.get("date")));
                            notifyItemChanged(pos);
                            Log.d(TAG, "Item at position " + pos + " changed");
                            break;
                    }
                }
            });
        };

        DBManager.getInstance().setNotesChangeListener(hashCode(), changeListener);
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
        NoteDiffUtilCallback noteDiffUtilCallback =
                new NoteDiffUtilCallback(noteList, (List<NoteShortCard>) notes);
        DiffUtil.DiffResult noteDiffResult = DiffUtil.calculateDiff(noteDiffUtilCallback);

        noteList = (List<NoteShortCard>) notes;
        noteDiffResult.dispatchUpdatesTo(this);
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

    public static class NoteDiffUtilCallback extends DiffUtil.Callback {
        private final List<NoteShortCard> oldList;
        private final List<NoteShortCard> newList;

        public NoteDiffUtilCallback(List<NoteShortCard> oldList, List<NoteShortCard> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            NoteShortCard oldNote = oldList.get(oldItemPosition);
            NoteShortCard newNote = newList.get(newItemPosition);
            return oldNote.getId().equals(newNote.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            NoteShortCard oldProduct = oldList.get(oldItemPosition);
            NoteShortCard newProduct = newList.get(newItemPosition);
            return oldProduct.getHeader().equals(newProduct.getHeader())
                    && oldProduct.getDate().equals(newProduct.getDate());
        }
    }
}
