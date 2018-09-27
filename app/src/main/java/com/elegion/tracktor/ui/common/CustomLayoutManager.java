package com.elegion.tracktor.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CustomLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        detachAndScrapAttachedViews(recycler);

        int pos = 0;
        int itemCount = getItemCount();
        int viewHeight;
        int viewWidth;

        int left;
        int top;
        int right;
        int bottom;

        int prevTop = 0;
        int prevRight = 0;
        int prevBottom = 0;

        while (pos < itemCount) {
            View view = recycler.getViewForPosition(pos);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            addView(view);
            measureChildWithMargins(view, 0, 0);

            if (prevTop == 0) {
                prevTop = lp.topMargin;
            }
            viewWidth = getDecoratedMeasuredWidth(view);
            viewHeight = getDecoratedMeasuredHeight(view);

            if (prevRight + viewWidth + lp.leftMargin < getWidth()) {
                left = prevRight + lp.leftMargin;
                top = prevTop;
                right = left + viewWidth;
                bottom = top + viewHeight;
            } else {
                left = lp.leftMargin;
                top = prevBottom + lp.topMargin;
                right = left + viewWidth;
                bottom = top + viewHeight;
            }
            layoutDecorated(view, left, top, right, bottom);
            prevTop = top;
            prevRight = right;
            prevBottom = bottom;
            pos++;
        }
    }

}
