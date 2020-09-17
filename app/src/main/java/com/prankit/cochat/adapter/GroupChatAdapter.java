package com.prankit.cochat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prankit.cochat.R;
import com.prankit.cochat.model.GroupChatInfo;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupChatViewHolder>{

    private List<GroupChatInfo> list;
    private Context context;

    public GroupChatAdapter(Context context, List<GroupChatInfo> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.group_message_view, parent, false);
        return new GroupChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.message.setText(list.get(position).getMessage());
        holder.date.setText(list.get(position).getDate());
        holder.time.setText(list.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GroupChatViewHolder extends RecyclerView.ViewHolder{
        TextView name, message, time, date;
        public GroupChatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.groupUserNameView);
            message = itemView.findViewById(R.id.groupMessageView);
            time = itemView.findViewById(R.id.groupTimeView);
            date = itemView.findViewById(R.id.groupDateView);
        }
    }
}
