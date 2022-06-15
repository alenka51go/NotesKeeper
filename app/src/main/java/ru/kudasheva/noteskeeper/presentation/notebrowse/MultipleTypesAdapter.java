package ru.kudasheva.noteskeeper.presentation.notebrowse;

import static ru.kudasheva.noteskeeper.presentation.models.InfoCard.COMMENT_ROW_TYPE;
import static ru.kudasheva.noteskeeper.presentation.models.InfoCard.NOTE_ROW_TYPE;

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
import java.util.List;
import java.util.Objects;

import ru.kudasheva.noteskeeper.data.ChangeListener;
import ru.kudasheva.noteskeeper.data.DBManager;
import ru.kudasheva.noteskeeper.databinding.CommentBinding;
import ru.kudasheva.noteskeeper.databinding.NoteBinding;
import ru.kudasheva.noteskeeper.presentation.models.InfoCard;

public class MultipleTypesAdapter extends RecyclerView.Adapter<MultipleTypesAdapter.AbstractViewHolder> {
    private static final String TAG = MultipleTypesAdapter.class.getSimpleName();

    private List<InfoCard> dataSet = new ArrayList<>();

    public MultipleTypesAdapter(Activity activity) {
        ChangeListener changeListener = (documentId, event, properties) -> {
            Log.d(TAG, "Document with id " + documentId + " was " + event);

            String currentNoteId = getCurrentNoteId();
            if (properties != null) {
                if (Objects.equals(properties.get("type"), "note")) {  // не подразумевается, что заметка может обновиться
                    return;
                }
                if (!Objects.equals(properties.get("noteId"), currentNoteId)) {  // не инетересуют комменты не по нашей заметке
                    return;
                }
            }

            activity.runOnUiThread(() -> {
                int pos = getIndexOfItemByDocumentId(documentId);
                if (pos == -1) {
                    switch (event) {
                        case DELETED:
                            break;
                        case UPDATED:
                            DBManager.getInstance().getUser((String) properties.get("userId"), (user) -> {
                                activity.runOnUiThread(() -> {
                                    dataSet.add(new InfoCard(documentId,
                                            (String) properties.get("text"),
                                            user.getFullName(),
                                            (String) properties.get("date"),
                                            COMMENT_ROW_TYPE));
                                    notifyItemChanged(dataSet.size() - 1);
                                    Log.d(TAG, "Item " + documentId + " inserted to position " + (dataSet.size() - 1));
                                });
                            });
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
                            DBManager.getInstance().getUser((String) properties.get("userId"), (user) -> {
                                activity.runOnUiThread(() -> {
                                    dataSet.set(pos, new InfoCard(documentId,
                                            (String) properties.get("text"),
                                            user.getFullName(),
                                            (String) properties.get("date"),
                                            COMMENT_ROW_TYPE));
                                    notifyItemChanged(pos);
                                    Log.d(TAG, "Item at position " + pos + " changed");
                                });
                            });
                            break;
                    }
                }
            });
        };
        DBManager.getInstance().setNotesChangeListener(hashCode(), changeListener);
    }

    public void setItems(List<InfoCard> dates) {
        MultipleTypesAdapter.MultiDiffUtilCallback cardDiffUtilCallback =
                new MultipleTypesAdapter.MultiDiffUtilCallback(dataSet, dates);
        DiffUtil.DiffResult cardDiffResult = DiffUtil.calculateDiff(cardDiffUtilCallback);

        dataSet = dates;
        cardDiffResult.dispatchUpdatesTo(this);
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
            binding.setFullNote(item);
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
            binding.setCommentDocument(item);
        }

    }

    public static class MultiDiffUtilCallback extends DiffUtil.Callback {

        private final List<InfoCard> oldList;
        private final List<InfoCard> newList;

        public MultiDiffUtilCallback(List<InfoCard> oldList, List<InfoCard> newList) {
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
            InfoCard oldCard = oldList.get(oldItemPosition);
            InfoCard newCard = newList.get(newItemPosition);
            return oldCard.getId().equals(newCard.getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            InfoCard oldCard = oldList.get(oldItemPosition);
            InfoCard newCard = newList.get(newItemPosition);

            return oldCard.getType() == newCard.getType()
                    && oldCard.getDate().equals(newCard.getDate())
                    && oldCard.getName().equals(newCard.getName())
                    && oldCard.getText().equals(newCard.getText());
        }

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
}
