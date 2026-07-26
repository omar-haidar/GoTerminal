package dev.omar.goterminal.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.termux.terminal.TerminalSession;
import java.util.ArrayList;
import java.util.List;
import dev.omar.goterminal.R;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessionViewHolder> {

    private final List<TerminalSession> sessions = new ArrayList<>();
    private final OnSessionClickListener listener;
    private int selectedPosition = 0;

    public interface OnSessionClickListener {
        void onSessionClick(int position);
        void onSessionDelete(TerminalSession session);
    }

    public SessionListAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<TerminalSession> newSessions) {
        sessions.clear();
        sessions.addAll(newSessions);
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previous = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previous);
        notifyItemChanged(selectedPosition);
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        TerminalSession session = sessions.get(position);
        holder.title.setText(session.mSessionName != null ? session.mSessionName : "Session " + (position + 1));
        
        boolean isSelected = position == selectedPosition;
        
        int bgColor = isSelected ? 
                MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorSecondaryContainer) :
                android.graphics.Color.TRANSPARENT;
        
        int strokeColor = isSelected ? 
                MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorSecondaryContainer) :
                MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorOutlineVariant);

        holder.card.setCardBackgroundColor(bgColor);
        holder.card.setStrokeColor(strokeColor);

        holder.itemView.setOnClickListener(v -> listener.onSessionClick(position));
        holder.deleteBtn.setOnClickListener(v -> listener.onSessionDelete(session));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView title;
        View deleteBtn;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.text1);
            deleteBtn = itemView.findViewById(R.id.img_delete);
        }
    }
}
