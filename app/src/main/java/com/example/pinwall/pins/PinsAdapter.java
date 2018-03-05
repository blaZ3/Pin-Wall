package com.example.pinwall.pins;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.downloader.Downloader;
import com.example.pinwall.R;
import com.example.pinwall.databinding.LayoutPinItemBinding;
import com.example.pinwall.pins.pojos.Pin;
import com.example.pinwall.utils.DataBindingViewHolder;

import java.util.ArrayList;

/**
 * Created by vivek on 05/03/18.
 */

public class PinsAdapter extends RecyclerView.Adapter<PinsAdapter.PinsAdapterViewHolder>{

    private static PinsAdapterInterface pinsAdapterInterface;

    private Context context;
    private ArrayList<Pin> items;

    static class PinsAdapterViewHolder extends DataBindingViewHolder{
        LayoutPinItemBinding dataBinding;
        public PinsAdapterViewHolder(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            dataBinding = (LayoutPinItemBinding) viewDataBinding;

            dataBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pinsAdapterInterface.pinSelected(getAdapterPosition());
                }
            });
        }
    }

    public PinsAdapter(Context context, ArrayList<Pin> items, PinsAdapterInterface pinsAdapterInterface){
        this.context = context;
        this.items = items;
        this.pinsAdapterInterface = pinsAdapterInterface;
    }


    @Override
    public PinsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PinsAdapterViewHolder vh = new PinsAdapterViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_pin_item, parent,false));
        return vh;
    }

    @Override
    public void onBindViewHolder(PinsAdapterViewHolder holder, int position) {
        holder.dataBinding.setPin(items.get(position));
        Downloader.with(context)
                .download(items.get(position).getUrls().getSmall())
                .setTag(holder.dataBinding.imgPin)
                .start();
        Downloader.with(context)
                .download(items.get(position).getUser().getProfileImage().getSmall())
                .setTag(holder.dataBinding.imgPinProfile)
                .start();

        if (position > getItemCount()-2){
            pinsAdapterInterface.paginate();
        }
    }

    @Override
    public int getItemCount() {
        if (items != null){
            return items.size();
        }
        return 0;
    }

    public interface PinsAdapterInterface{
        void pinSelected(int position);
        void paginate();
    }
}
