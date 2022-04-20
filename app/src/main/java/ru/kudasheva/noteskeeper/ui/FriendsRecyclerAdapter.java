package ru.kudasheva.noteskeeper.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.kudasheva.noteskeeper.R;

public class FriendsRecyclerAdapter extends RecyclerView.Adapter<FriendsRecyclerAdapter.FriendViewHolder> {
    private final List<FriendInfoCard> friends = new ArrayList<>();


    @NonNull
    @Override
    public FriendsRecyclerAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_info, parent, false);
        return new FriendsRecyclerAdapter.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecyclerAdapter.FriendViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setItems(Collection<FriendInfoCard> notes) {
        friends.addAll(notes);
        notifyDataSetChanged();
    }

    public void clearItems() {
        friends.clear();
        notifyDataSetChanged();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_friend_name);
        }

        public void bind(FriendInfoCard friendInfoCard) {
            name.setText(friendInfoCard.getName());
        }
    }
}
