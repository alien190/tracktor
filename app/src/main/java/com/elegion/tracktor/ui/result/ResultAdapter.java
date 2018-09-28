package com.elegion.tracktor.ui.result;

import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.ShowResultDetailEvent;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.utils.IDistanceConverter;

import org.greenrobot.eventbus.EventBus;

public class ResultAdapter extends ListAdapter<Track, ResultViewHolder> {

    private CurrentPreferences mCurrentPreferences;
    private IDistanceConverter mDistanceConverter;

    private static DiffUtil.ItemCallback<Track> DIFF_CALLBACK = new DiffUtil.ItemCallback<Track>() {
        @Override
        public boolean areItemsTheSame(Track oldItem, Track newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Track oldItem, Track newItem) {
            return oldItem.equals(newItem);
        }
    };

    public ResultAdapter(CurrentPreferences currentPreferences, IDistanceConverter distanceConverter) {
        super(DIFF_CALLBACK);
        this.mCurrentPreferences = currentPreferences;
        this.mDistanceConverter = distanceConverter;

    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_track, parent, false);
        return new ResultViewHolder(view, mCurrentPreferences, mDistanceConverter);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
