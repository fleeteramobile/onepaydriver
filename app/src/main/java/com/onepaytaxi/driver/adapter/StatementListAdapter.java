package com.onepaytaxi.driver.adapter;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.data.apiData.StatementData;
import com.onepaytaxi.driver.utils.SessionSave;

import java.util.List;

/**
 * Created by developer on 1/11/16.
 * use to populate the past booking recyclerview
 */
public class StatementListAdapter extends RecyclerView.Adapter<StatementListAdapter.CustomViewHolder> {

    private final List<StatementData> data;
    private final Context mContext;

    public StatementListAdapter(Context c, List<StatementData> data) {
        this.mContext = c;
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.stat_list_item, parent, false);
      //  FontHelper.applyFont(mContext, view.findViewById(R.id.full_lay));

        return new CustomViewHolder(view);
    }

    /**
     * binds view to recyclerview
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        String currentString = data.get(position).getCreatedate();
        String[] separated = currentString.split(" ");



        holder.dateTxt.setText( separated[0]+" : "+  separated[1]);
        holder.balTxt.setText(SessionSave.getSession("site_currency", mContext) + " " +data.get(position).getBalance());
        holder.amtTxt.setText(SessionSave.getSession("site_currency", mContext) + " " +data.get(position).getAdded_amount());
        holder.descTxt.setText(data.get(position).getWallet_item() + " - " + data.get(position).getDescription());
//        if(!data.get(position).getAdded_amount().equals("0"))
//        {
//            holder.full_lay.setVisibility(View.VISIBLE);
//
//          //  holder.itemTxt.setText(data.get(position).getWallet_item());
//        }
//        else {
//            holder.full_lay.setVisibility(View.GONE);
//
//        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView dateTxt,balTxt,amtTxt,descTxt,itemTxt;
CardView full_lay;
        public CustomViewHolder(View v) {
            super(v);
            itemTxt = v.findViewById(R.id.itemTxt);
            descTxt = v.findViewById(R.id.descTxt);
            dateTxt = v.findViewById(R.id.dateTxt);
            amtTxt = v.findViewById(R.id.amtTxt);
            balTxt = v.findViewById(R.id.balTxt);
            full_lay = v.findViewById(R.id.full_lay);

        }
    }
}
