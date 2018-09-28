package com.elegion.tracktor.di.application;

import com.elegion.tracktor.ui.common.TrackSharing;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;

import javax.inject.Inject;
import javax.inject.Provider;

public class TrackSharingProvider implements Provider<TrackSharing> {

    private MessageTemplate mMessageTemplate;

    @Inject
    public TrackSharingProvider(MessageTemplate messageTemplate) {
        mMessageTemplate = messageTemplate;
    }

    @Override
    public TrackSharing get() {
        return new TrackSharing(mMessageTemplate);
    }
}
