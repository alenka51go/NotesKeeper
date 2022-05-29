package ru.kudasheva.noteskeeper.notebrowse;

import static ru.kudasheva.noteskeeper.notebrowse.InfoCard.NOTE_ROW_TYPE;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.kudasheva.noteskeeper.data.ChangeListener;
import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.databinding.CommentBinding;
import ru.kudasheva.noteskeeper.databinding.NoteBinding;
import ru.kudasheva.noteskeeper.notescroll.CustomRecyclerAdapter;
import ru.kudasheva.noteskeeper.notescroll.NoteShortCard;

public class MultipleTypesAdapter extends RecyclerView.Adapter<MultipleTypesAdapter.AbstractViewHolder> {
    private static final String TAG = MultipleTypesAdapter.class.getSimpleName();

    private List<InfoCard> dataSet = new ArrayList<>();

    public MultipleTypesAdapter(Activity activity) {
        ChangeListener changeListener = (documentId, event, properties) -> {
            Log.d(TAG, "Document with id " + documentId + " was " + event);

            String currentNoteId = getCurrentNoteId();
            if (properties != null) {
                if (Objects.equals(properties.get("type"), "note")) {
                    // не подразумевается, что заметка может обновиться
                    return;
                }
                if (!Objects.equals(properties.get("noteId"), currentNoteId)) {
                    // не инетересуют комменты не по нашей заметке
                    return;
                }
            }

            int pos = getIndexOfItemByDocumentId(documentId);

            activity.runOnUiThread(() -> {
                if (pos == -1) {
                    switch (event) {
                        case DELETED:
                            break;
                        case UPDATED:
                            dataSet.add(new CommentInfoCard(documentId,
                                    (String) properties.get("text"),
                                    (String) properties.get("text"),
                                    (String) properties.get("userId")));
                            notifyItemChanged(dataSet.size() - 1);
                            Log.d(TAG, "Item " + documentId + " inserted to position " + (dataSet.size() - 1));
                            break;
                    }
                } else {
                    switch (event) {
                        case DELETED:
                            dataSet.remove(pos);
                            notifyItemRemoved(pos);
                            Log.d(TAG, "Item at position " + pos + " removed");
                            break;
                        case UPDATED:
                            dataSet.set(pos, new CommentInfoCard(documentId,
                                    (String) properties.get("text"),
                                    (String) properties.get("text"),
                                    (String) properties.get("userId")));
                            notifyItemChanged(pos);
                            Log.d(TAG, "Item at position " + pos + " changed");
                            break;
                    }
                }
            });
        };

        DBManager.getInstance().setNotesChangeListener(hashCode(), changeListener);
    }

    private String getCurrentNoteId() {
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getType() == NOTE_ROW_TYPE) {
                return dataSet.get(i).getId();
            }
        }
        return null;
    }

    private int getIndexOfItemByDocumentId(String documentId) {
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getId().equals(documentId)) {
                return i;
            }
        }
        return -1;
    }

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
        if (viewType == NOTE_ROW_TYPE) {
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
