package ru.kudasheva.noteskeeper.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.kudasheva.noteskeeper.R;
import ru.kudasheva.noteskeeper.databinding.FriendInfoBinding;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.FriendViewHolder> {
    private List<FriendInfoCard> friends = new ArrayList<>();

    @NonNull
    @Override
    public FriendsRecyclerAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FriendInfoBinding binding = FriendInfoBinding.inflate(inflater, parent, false);
        return new FriendViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecyclerAdapter.FriendViewHolder holder, int position) {
        FriendInfoCard friend = friends.get(position);
        holder.binding.setFriendInfo(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setItems(Collection<FriendInfoCard> notes) {
        friends = (List<FriendInfoCard>) notes;
        notifyDataSetChanged();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        FriendInfoBinding binding;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
