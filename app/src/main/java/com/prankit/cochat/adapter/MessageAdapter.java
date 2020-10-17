package com.prankit.cochat.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.prankit.cochat.R;
import com.prankit.cochat.model.Messages;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_message_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        if (fromMessageType.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(messageSenderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderTime.setVisibility(View.VISIBLE);
                holder.receiverTime.setVisibility(View.INVISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(messages.getMessage());
                holder.senderTime.setText(messages.getTime());
            }
            else {
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverTime.setVisibility(View.VISIBLE);
                holder.senderTime.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.reciever_message_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(messages.getMessage());
                holder.receiverTime.setText(messages.getTime());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView senderMessageText, receiverMessageText;
        TextView senderTime, receiverTime;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.senderMessageText);
            receiverMessageText = itemView.findViewById(R.id.recieverMessageText);
            senderTime = itemView.findViewById(R.id.senderTime);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }
}
