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

public class MijnAdapter extends RecyclerView.Adapter<MijnAdapter.ViewHolderFiliaalNamen> {
    private static final String TAG = "CustomAdapter";

    private String[] filiaalNamen;
    private String[] addresLijst;
    EigenClickListener listener;

    public static class ViewHolderFiliaalNamen extends RecyclerView.ViewHolder {
        private final TextView textViewMain;
        private final TextView textViewSub;
        public LinearLayout linearLayout;

        public ViewHolderFiliaalNamen(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    Context context = v.getContext();
                    Intent i = new Intent(context, SettingsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("filiaalId", getAdapterPosition());
                    i.putExtras(bundle);
                    context.startActivity(i);
                }
            });
            textViewMain = (TextView) v.findViewById(R.id.textViewMain);
            textViewSub = (TextView) v.findViewById(R.id.textViewSub);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_rb);
        }

        public TextView getTextViewMain() {
            return textViewMain;
        }

        public TextView getTextViewSub(){
            return textViewSub;
        }
    }

    public MijnAdapter(String[] filiaalNamen, String[] addresLijst) {
        this.filiaalNamen = filiaalNamen;
        this.addresLijst = addresLijst;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolderFiliaalNamen onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.section, viewGroup, false);

        return new ViewHolderFiliaalNamen(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderFiliaalNamen viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        viewHolder.getTextViewMain().setText(filiaalNamen[position]);
        viewHolder.getTextViewSub().setText(addresLijst[position]);
    }

    @Override
    public int getItemCount() {
        if(filiaalNamen==null) return 0;
        return filiaalNamen.length;
    }
}