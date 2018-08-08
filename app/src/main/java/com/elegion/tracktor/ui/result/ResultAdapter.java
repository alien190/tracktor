package com.elegion.tracktor.ui.result;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.ViewGroup;

import com.elegion.tracktor.data.model.Track;

public class ResultAdapter extends ListAdapter<Track, ResultViewHolder> {

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

    public ResultAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {

    }

    interface OnItemClickListener {
        void onItemClick (long id);
    }
}
