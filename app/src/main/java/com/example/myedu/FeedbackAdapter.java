package com.example.myedu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FeedbackAdapter extends FirestoreRecyclerAdapter<Feedback, FeedbackAdapter.FeedbackViewHolder> {

    private final OnItemClickListener mListener;

    public FeedbackAdapter(FirestoreRecyclerOptions<Feedback> options, OnItemClickListener listener) {
        super(options);
        mListener = listener;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position, @NonNull Feedback feedback) {
        // Optional: Set document ID in model if needed
        // feedback.setId(getSnapshots().getSnapshot(position).getId());
        holder.bind(feedback, position);
    }

    public interface OnItemClickListener {
        void onItemClick(String feedbackId, String comment, float rating);
        void onItemDelete(String feedbackId);
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private final TextView userNameTextView;
        private final TextView commentTextView;
        private final RatingBar ratingBar;
        private final Button editButton;
        private final Button deleteButton;
        private final CardView feedbackCard;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
            commentTextView = itemView.findViewById(R.id.textViewFeedback);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
            feedbackCard = itemView.findViewById(R.id.cardViewFeedback);
        }

        public void bind(Feedback feedback, int position) {
            if (feedback != null) {
                userNameTextView.setText(feedback.getUserName());
                commentTextView.setText(feedback.getComment());
                ratingBar.setRating(feedback.getRating());

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // Edit button logic
                editButton.setOnClickListener(v -> {
                    if (mListener != null && currentUser != null && currentUser.getUid().equals(feedback.getUserId())) {
                        String feedbackId = getSnapshots().getSnapshot(position).getId();
                        mListener.onItemClick(feedbackId, feedback.getComment(), feedback.getRating());
                    } else {
                        showNotOwnerDialog(v);
                    }
                });

                // Delete button logic
                deleteButton.setOnClickListener(v -> {
                    if (mListener != null && currentUser != null && currentUser.getUid().equals(feedback.getUserId())) {
                        String feedbackId = getSnapshots().getSnapshot(position).getId(); // Fixed here
                        mListener.onItemDelete(feedbackId);
                    } else {
                        showNotOwnerDialog(v);
                    }
                });
            }
        }

        private void showNotOwnerDialog(View v) {
            Toast.makeText(v.getContext(), "You can only edit or delete your own feedback.", Toast.LENGTH_SHORT).show();
        }
    }
}
