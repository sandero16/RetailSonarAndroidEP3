package be.retailsonar.retailsonar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MijnAdapterTaken extends RecyclerView.Adapter<MijnAdapterTaken.ViewHolderTaken> {
    private static final String TAG = "CustomAdapter";

    private String[] taken;

    public static class ViewHolderTaken extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolderTaken(View v) {
            super(v);

            textView = v.findViewById(R.id.textViewTaken);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public MijnAdapterTaken(String[] taken) {
        this.taken = taken;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolderTaken onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.section_taken, viewGroup, false);

        return new ViewHolderTaken(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderTaken viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.getTextView().setText(taken[position]);
    }

    @Override
    public int getItemCount() {
        return taken.length;
    }
}