package com.elegion.tracktor.ui.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.elegion.tracktor.R;
import com.elegion.tracktor.data.model.Track;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.utils.ScreenshotMaker;

public class TrackSharing {
    private MessageTemplate mMessageTemplate;

    public TrackSharing(MessageTemplate messageTemplate) {
        mMessageTemplate = messageTemplate;
    }

    public void doShare(Track track, Context context) {
        try {
            if (track != null && context != null) {
                String shareMessage = mMessageTemplate.getMessage(track);
                Intent intent = new Intent(Intent.ACTION_SEND);
                String imageStub = context.getString(R.string.image_stub);

                if (shareMessage.contains(imageStub)) {
                    Bitmap screenShot = ScreenshotMaker.fromBase64(track.getImage());
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                            screenShot, context.getString(R.string.share_route_title), null);
                    Uri uri = Uri.parse(path);
                    shareMessage = shareMessage.replace(imageStub, "");
                    intent.setType(context.getString(R.string.mime_image));
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                } else {
                    intent.setType(context.getString(R.string.mime_text));
                }

                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_chooser_title)));
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
