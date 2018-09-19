package com.elegion.tracktor.ui.messageTemplate.helper;


public interface ItemTouchAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
