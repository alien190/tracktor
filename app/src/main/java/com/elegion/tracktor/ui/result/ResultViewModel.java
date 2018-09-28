package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.ui.common.ICommentViewModel;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.utils.StringUtils;

import java.util.List;

public class ResultViewModel extends ViewModel implements ICommentViewModel {
    public static final int SORT_ORDER_ASC = 1;
    public static final int SORT_ORDER_DESC = 2;
    public static final int SORT_BY_START_DATE = 1;
    public static final int SORT_BY_DURATION = 2;
    public static final int SORT_BY_DISTANCE = 3;

    private IRepository<Track> mRepository;
    private MutableLiveData<List<Track>> mTracks = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();
    private MutableLiveData<Integer> mSortOrder = new MutableLiveData<>();
    private MutableLiveData<Integer> mSortBy = new MutableLiveData<>();
    private int mRepositorySortOrder;
    private int mRepositorySortBy;
    private long mTrackIdForComment;
    private MessageTemplate mMessageTemplate;

    public ResultViewModel(IRepository<Track> repository) {
        mRepository = repository;
        mTracks.observeForever(list -> isEmpty.postValue(list != null && list.isEmpty()));
        mRepositorySortOrder = IRepository.SORT_ORDER_ASC;
        mRepositorySortBy = IRepository.SORT_BY_START_DATE;
        mSortOrder.postValue(SORT_ORDER_ASC);
        mSortBy.postValue(SORT_BY_START_DATE);
    }


    public void loadTracks() {
        mTracks.postValue(mRepository.getAll(mRepositorySortOrder, mRepositorySortBy));
    }

    public void changeSortOrder() {
        if (mRepositorySortOrder == IRepository.SORT_ORDER_ASC) {
            mRepositorySortOrder = IRepository.SORT_ORDER_DESC;
            mSortOrder.postValue(SORT_ORDER_DESC);
        } else {
            mRepositorySortOrder = IRepository.SORT_ORDER_ASC;
            mSortOrder.postValue(SORT_ORDER_ASC);
        }
        loadTracks();
    }

    public void changeSortBy() {
        switch (mRepositorySortBy) {
            case IRepository.SORT_BY_START_DATE: {
                mRepositorySortBy = IRepository.SORT_BY_DURATION;
                mSortBy.postValue(SORT_BY_DURATION);
                break;
            }
            case IRepository.SORT_BY_DURATION: {
                mRepositorySortBy = IRepository.SORT_BY_DISTANCE;
                mSortBy.postValue(SORT_BY_DISTANCE);
                break;
            }
            default: {
                mRepositorySortBy = IRepository.SORT_BY_START_DATE;
                mSortBy.postValue(SORT_BY_START_DATE);
                break;
            }
        }
        loadTracks();
    }

    public void deleteTrack(long trackId) {
        mRepository.deleteItem(trackId);
        loadTracks();
    }

    public String getSharingMessage(Track track) {
        return mMessageTemplate.getMessage(track);
    }

    public Track getTrack(long trackId) {
        return mRepository.getItem(trackId);
    }

    public MutableLiveData<List<Track>> getTracks() {
        return mTracks;
    }

    public MutableLiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }

    public MutableLiveData<Integer> getSortOrder() {
        return mSortOrder;
    }

    public MutableLiveData<Integer> getSortBy() {
        return mSortBy;
    }

    public void setTrackIdForComment(long trackIdForComment) {
        mTrackIdForComment = trackIdForComment;
    }

    @Override
    public MutableLiveData<String> getComment() {
        MutableLiveData<String> comment = new MutableLiveData<>();
        Track track = mRepository.getItem(mTrackIdForComment);
        if (track.getComment() == null) {
            track.setComment("");
        }
        comment.postValue(track.getComment());
        comment.observeForever(newComment -> {
            if (!track.getComment().equals(newComment)) {
                track.setComment(newComment);
                mRepository.updateItem(track);
                //todo переделать такой рефреш
                loadTracks();
            }
        });
        return comment;
    }
}
