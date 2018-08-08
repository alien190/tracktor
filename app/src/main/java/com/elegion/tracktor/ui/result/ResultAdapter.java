package com.elegion.tracktor.ui.result;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;

public class ResultAdapter extends ListAdapter<Track, ResultViewHolder> {

    private OnItemClickListener mOnItemClickListener;

    private static DiffUtil.ItemCallback<Track> DIFF_CALLBACK = new DiffUtil.ItemCallback<Track>() {
        @Override
        public boolean areItemsTheSame(Track oldItem, Track newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Track oldItem, Track newItem) {
            return oldItem.getDate() == newItem.getDate() &&
                    oldItem.getDuration() == newItem.getDuration() &&
                    oldItem.getDistance() == newItem.getDistance();
        }
    };

    public ResultAdapter(OnItemClickListener onItemClickListener) {
        super(DIFF_CALLBACK);
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_track, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.setOnClickListener(mOnItemClickListener);
    }

    interface OnItemClickListener {
        void onItemClick (long id);
    }
}
