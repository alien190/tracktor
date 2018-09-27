package com.elegion.tracktor.ui.messageTemplate.viewHolder;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate.CommonTemplateItem;
import com.elegion.tracktor.ui.messageTemplate.helper.ItemTouchViewHolder;


public abstract class CommonItemViewHolder extends RecyclerView.ViewHolder implements
        ItemTouchViewHolder {

    protected final TextView mTextView;
    protected final CardView mCardView;
    private ColorStateList mColorStateList;
    private IOnItemClickListener mIOnItemClickListener;
    private MessageTemplate.CommonTemplateItem mItem;

    public CommonItemViewHolder(final View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.tvItem);
        mCardView = itemView.findViewById(R.id.cardView);
        mCardView.setOnClickListener(view -> {
            if (mIOnItemClickListener != null) {
                mIOnItemClickListener.onItemClick(mItem);
            }
        });
    }

    @Override
    public void onItemSelected() {
        mColorStateList = mCardView.getCardBackgroundColor();
        mCardView.setCardBackgroundColor(Color.GREEN);
    }

    @Override
    public void onItemClear() {
        mCardView.setCardBackgroundColor(mColorStateList);
    }

    public void onBind(CommonTemplateItem item) {
        if (item != null) {
            mItem = item;
            mTextView.setText(item.getText());
        }
    }


    public void setIOnItemClickListener(IOnItemClickListener IOnItemClickListener) {
        mIOnItemClickListener = IOnItemClickListener;
    }

    public interface IOnItemClickListener {
        void onItemClick(CommonTemplateItem item);
    }
}
