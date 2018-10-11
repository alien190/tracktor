package com.elegion.tracktor.ui.result;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.data.model.Track;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResultViewModelTest {

    @Rule
    public MockitoRule mMockitoRule = MockitoJUnit.rule();

    @Rule
    public TestRule mTestRule = new InstantTaskExecutorRule();

    private ResultViewModel mResultViewModel;

    private List<Track> mTracks;

    @Mock
    private RealmRepository mRepository;

    @Mock
    private Observer<List<Track>> mObserver;

    @Before
    public void setUp() throws Exception {
        mTracks = new ArrayList<>();
        mTracks.add(new Track());

        when(mRepository.getAllTracks()).thenReturn(mTracks);

        mResultViewModel = new ResultViewModel(mRepository);

        mResultViewModel.getTracks().observeForever(mObserver);
    }

    @Test
    public void checkOnChangedCalled(){
       mResultViewModel.loadTracks();
       verify(mObserver, times(1)).onChanged(mTracks);
    }
}