package ru.kudasheva.noteskeeper.presentation.friends;

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

import ru.kudasheva.noteskeeper.databinding.FriendInfoBinding;
import ru.kudasheva.noteskeeper.presentation.models.FriendCard;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.FriendViewHolder> {
    private static final String TAG = FriendsRecyclerAdapter.class.getSimpleName();

    private List<FriendCard> friends = new ArrayList<>();

    @NonNull
    @Override
    public FriendsRecyclerAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FriendInfoBinding binding = FriendInfoBinding.inflate(inflater, parent, false);
        return new FriendViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecyclerAdapter.FriendViewHolder holder, int position) {
        FriendCard friend = friends.get(position);
        holder.binding.setFriendInfo(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setItems(Collection<FriendCard> friends) {
        FriendsRecyclerAdapter.FriendDiffUtilCallback noteDiffUtilCallback =
                new FriendsRecyclerAdapter.FriendDiffUtilCallback(this.friends, (List<FriendCard>) friends);
        DiffUtil.DiffResult noteDiffResult = DiffUtil.calculateDiff(noteDiffUtilCallback);

        this.friends = (List<FriendCard>) friends;
        noteDiffResult.dispatchUpdatesTo(this);
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        FriendInfoBinding binding;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public static class FriendDiffUtilCallback extends DiffUtil.Callback {
        private final List<FriendCard> oldList;
        private final List<FriendCard> newList;

        public FriendDiffUtilCallback(List<FriendCard> oldList, List<FriendCard> newList) {
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
            FriendCard oldNote = oldList.get(oldItemPosition);
            FriendCard newNote = newList.get(newItemPosition);
            return oldNote.getName().equals(newNote.getName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }
    }
}
