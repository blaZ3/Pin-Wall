package com.example.pinwall.utils;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by vivek on 03/12/17.
 */

public class DataBindingViewHolder extends RecyclerView.ViewHolder {

    public DataBindingViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
    }
}
