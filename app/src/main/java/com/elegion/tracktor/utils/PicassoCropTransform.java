package com.elegion.tracktor.utils;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class PicassoCropTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap croppedBitmap;
        try {
            int[] pixels = new int[source.getWidth() * source.getWidth()];
            source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
            Crop crop = new Crop(pixels, source.getWidth(), source.getHeight());
            int[] croppedPixels = cropPixels(pixels, source.getWidth(), crop);
            croppedBitmap = Bitmap.createBitmap(croppedPixels, crop.getWidth(), crop.getHeight(), source.getConfig());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return source;
        }
        if (croppedBitmap != source) {
            source.recycle();
        }
        return croppedBitmap;
    }

    private int[] cropPixels(int[] pixels, int width, Crop crop) {

        int[] cropPixels = new int[crop.getWidth() * crop.getHeight()];
        for (int row = 0; row < crop.getHeight(); row++) {
            for (int col = 0; col < crop.getWidth(); col++) {
                cropPixels[row * crop.getWidth() + col] =
                        pixels[(row + crop.getTop()) * width + col + crop.getLeft()];
            }
        }
        return cropPixels;
    }

    @Override
    public String key() {
        return "PicassoCropTransform";
    }


    private class Crop {
        private int mTop;
        private int mBottom;
        private int mLeft;
        private int mRight;
        private int mWidth;
        private int mHeight;

        public Crop(int[] pixels, int width, int height) {
            mTop = getCropTop(pixels, width, height);
            mBottom = getCropBottom(pixels, width, height);
            mLeft = getCropLeft(pixels, width, height);
            mRight = getCropRight(pixels, width, height);
            mWidth = mRight - mLeft + 1;
            mHeight = mBottom - mTop + 1;
        }

        private int getCropTop(int[] pixels, int width, int height) {
            int cropTop = -1;
            int pixel;
            for (int row = 0; row < height; row++) {
                boolean isEmptyRow = true;
                for (int col = 0; col < width; col++) {
                    pixel = pixels[row * width + col];
                    if (pixel != 0) {
                        isEmptyRow = false;
                    }
                }
                if (!isEmptyRow && cropTop == -1) {
                    cropTop = row;
                }
            }
            return cropTop;
        }

        private int getCropBottom(int[] pixels, int width, int height) {
            int cropBottom = -1;
            int pixel;
            for (int row = height - 1; row >= 0; row--) {
                boolean isEmptyRow = true;
                for (int col = 0; col < width; col++) {
                    pixel = pixels[row * width + col];
                    if (pixel != 0) {
                        isEmptyRow = false;
                    }
                }
                if (!isEmptyRow && cropBottom == -1) {
                    cropBottom = row;
                }
            }
            return cropBottom;
        }

        private int getCropLeft(int[] pixels, int width, int height) {
            int cropLeft = -1;
            int pixel;
            for (int col = 0; col < width; col++) {
                boolean isEmptyCol = true;
                for (int row = 0; row < height; row++) {
                    pixel = pixels[row * width + col];
                    if (pixel != 0) {
                        isEmptyCol = false;
                    }
                }
                if (!isEmptyCol && cropLeft == -1) {
                    cropLeft = col;
                }
            }
            return cropLeft;
        }

        private int getCropRight(int[] pixels, int width, int height) {
            int cropRight = -1;
            int pixel;
            for (int col = width - 1; col >= 0; col--) {
                boolean isEmptyCol = true;
                for (int row = 0; row < height; row++) {
                    pixel = pixels[row * width + col];
                    if (pixel != 0) {
                        isEmptyCol = false;
                    }
                }
                if (!isEmptyCol && cropRight == -1) {
                    cropRight = col;
                }
            }
            return cropRight;
        }

        public int getTop() {
            return mTop;
        }

        public int getBottom() {
            return mBottom;
        }

        public int getLeft() {
            return mLeft;
        }

        public int getRight() {
            return mRight;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }
    }
}
