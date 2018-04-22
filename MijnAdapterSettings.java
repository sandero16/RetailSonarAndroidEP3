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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import be.retailsonar.be.retailsonar.objects.CustomEigenschapIngevuld;
import be.retailsonar.be.retailsonar.objects.EigenschappenIngevuld;

public class MijnAdapterSettings extends RecyclerView.Adapter<MijnAdapterSettings.ViewHolderEigenschappen> {

    private static final String TAG = "CustomAdapter";
    private List<EigenschappenIngevuld> eigenschappenIngevuldLijst;
    private List<CustomEigenschapIngevuld> customEigenschapIngevuldLijst;
    private List<ViewHolderEigenschappen> viewholders;


    public static class ViewHolderEigenschappen extends RecyclerView.ViewHolder {
        private final TextView textViewMain;
        private final TextView textViewSub;
        private final EditText editText;
        public LinearLayout linearLayout;

        public ViewHolderEigenschappen(View v) {
            super(v);
            textViewMain = (TextView) v.findViewById(R.id.textViewSettingsMain);
            textViewSub = (TextView) v.findViewById(R.id.textViewSettingsSub);
            editText = (EditText) v.findViewById(R.id.editText_settings);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_rb);
        }

        public TextView getTextViewMain() {
            return textViewMain;
        }

        public TextView getTextViewSub(){
            return textViewSub;
        }

        public EditText getEditText() {
            return editText;
        }
    }

    public MijnAdapterSettings(List<EigenschappenIngevuld> eigenschappenIngevuldLijst, List<CustomEigenschapIngevuld> customEigenschapIngevuldLijst) {
        this.eigenschappenIngevuldLijst = eigenschappenIngevuldLijst;
        this.customEigenschapIngevuldLijst = customEigenschapIngevuldLijst;
        this.viewholders = new LinkedList<ViewHolderEigenschappen>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolderEigenschappen onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.section_settings, viewGroup, false);

        ViewHolderEigenschappen vh = new ViewHolderEigenschappen(v);
        viewholders.add(vh);
        return vh;
    }

    public List<EigenschappenIngevuld> getEigenschappenIngevuldLijst() {
        return eigenschappenIngevuldLijst;
    }

    public List<CustomEigenschapIngevuld> getCustomEigenschapIngevuldLijst() {
        return customEigenschapIngevuldLijst;
    }

    public String[] getInhoudEditTextViewHolders(){
        String[] result = new String[getItemCount()];
        int i = 0;

        for(ViewHolderEigenschappen vh : viewholders){
            String text = vh.editText.getText().toString();
            if(text==null) text="";
            result[i] = text;
            i++;
        }
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolderEigenschappen viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        int eigenschappenSize = 0;
        if(eigenschappenIngevuldLijst!=null) eigenschappenSize = eigenschappenIngevuldLijst.size();

        String eenheid = "";
        String type = "";

        if(position<eigenschappenSize){
            viewHolder.getTextViewMain().setText(eigenschappenIngevuldLijst.get(position).getNaam());
            if(eigenschappenIngevuldLijst!=null) {
                if (eigenschappenIngevuldLijst.get(position).getEenheid() != null && !eigenschappenIngevuldLijst.get(position).getEenheid().equals("")) {
                    eenheid = "eenheid: " + eigenschappenIngevuldLijst.get(position).getEenheid();
                }
                if (eigenschappenIngevuldLijst.get(position).getType() != null && !eigenschappenIngevuldLijst.get(position).getType().equals("")) {
                    type = ", type: " + eigenschappenIngevuldLijst.get(position).getType();
                }
            }
            viewHolder.getTextViewSub().setText(eenheid + type);
        }
        else{
            viewHolder.getTextViewMain().setText(customEigenschapIngevuldLijst.get(position-eigenschappenSize).getNaam());
            if(customEigenschapIngevuldLijst!=null) {
                if (customEigenschapIngevuldLijst.get(position - eigenschappenSize).getEenheid() != null && !customEigenschapIngevuldLijst.get(position - eigenschappenSize).getEenheid().equals("")) {
                    eenheid = "eenheid: " + customEigenschapIngevuldLijst.get(position - eigenschappenSize).getEenheid();
                }
                if (customEigenschapIngevuldLijst.get(position - eigenschappenSize).getType() != null && !customEigenschapIngevuldLijst.get(position - eigenschappenSize).getType().equals("")) {
                    type = ", type: " + customEigenschapIngevuldLijst.get(position - eigenschappenSize).getType();
                }
            }
            viewHolder.getTextViewSub().setText(eenheid + type);
        }
    }

    @Override
    public int getItemCount() {
        int eigenschappenSize = 0;
        int customEigenschappenSize = 0;
        if(eigenschappenIngevuldLijst!=null) eigenschappenSize = eigenschappenIngevuldLijst.size();
        if(customEigenschapIngevuldLijst!=null) customEigenschappenSize = customEigenschapIngevuldLijst.size();
        return (eigenschappenSize + customEigenschappenSize);
    }
}