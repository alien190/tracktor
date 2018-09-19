package com.elegion.tracktor.ui.messageTemplate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.event.MessageTemplateUpdateEvent;
import com.elegion.tracktor.ui.messageTemplate.helper.ItemTouchAdapter;
import com.elegion.tracktor.ui.messageTemplate.viewHolder.CommonItemViewHolder;
import com.elegion.tracktor.ui.messageTemplate.viewHolder.ParameterItemViewHolder;
import com.elegion.tracktor.ui.messageTemplate.viewHolder.TextItemViewHolder;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate.CommonTemplateItem;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate.ParameterTemplateItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MessageTemplateListAdapter extends RecyclerView.Adapter<CommonItemViewHolder>
        implements ItemTouchAdapter, CommonItemViewHolder.IOnItemClickListener {

    private final MessageTemplate mMessageTemplate;
    public static final int ITEM_TYPE_TEXT = 1;
    public static final int ITEM_TYPE_PARAMETER = 2;
    public static final int NEW_ITEM = -1;
    private IOnEditItemListener mIOnEditItemListener;
    private CurrentPreferences mCurrentPreferences;


    public MessageTemplateListAdapter(MessageTemplate messageTemplate, CurrentPreferences currentPreferences) {
        mMessageTemplate = messageTemplate;
        mCurrentPreferences = currentPreferences;
    }

    @Override
    public CommonItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        CommonItemViewHolder commonItemViewHolder;
        if (viewType == ITEM_TYPE_PARAMETER) {
            commonItemViewHolder = new ParameterItemViewHolder(view);
        } else {
            commonItemViewHolder = new TextItemViewHolder(view);
        }
        commonItemViewHolder.setIOnItemClickListener(this);
        return commonItemViewHolder;
    }

    @Override
    public void onBindViewHolder(CommonItemViewHolder holder, int position) {
        holder.onBind(mMessageTemplate.getItem(position));
    }

    @Override
    public void onItemDismiss(int position) {
        mMessageTemplate.removeItem(position);
        notifyItemRemoved(position);
        EventBus.getDefault().post(new MessageTemplateUpdateEvent());
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (mMessageTemplate.moveItem(fromPosition, toPosition)) {
            notifyItemMoved(fromPosition, toPosition);
            EventBus.getDefault().post(new MessageTemplateUpdateEvent());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageTemplate.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        MessageTemplate.CommonTemplateItem commonTemplateItem = mMessageTemplate.getItem(position);
        if (commonTemplateItem != null && commonTemplateItem instanceof MessageTemplate.ParameterTemplateItem) {
            return ITEM_TYPE_PARAMETER;
        } else {
            return ITEM_TYPE_TEXT;
        }
    }


    public CharSequence[] getParametersTypes() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < mCurrentPreferences.getMessageTemplateParamTypesCount(); i++) {
            list.add(mCurrentPreferences.getMessageTemplateParamName(i));
        }
        return list.toArray(new CharSequence[0]);
    }

    public boolean addItem(int type) {
        if (mIOnEditItemListener != null) {
            if (type == ITEM_TYPE_TEXT) {
                mIOnEditItemListener.onEditTextItem(NEW_ITEM);
            } else {
                mIOnEditItemListener.onEditParameterItem(NEW_ITEM);
            }
        }
        return true;
    }

    public void addTextItem(String text) {
        mMessageTemplate.addTextItem(text);
        notifyItemInserted(mMessageTemplate.getItemCount() - 1);
        EventBus.getDefault().post(new MessageTemplateUpdateEvent());
    }

    public void addParameterItem(int type) {
        mMessageTemplate.addParameterItem(type);
        notifyItemInserted(mMessageTemplate.getItemCount() - 1);
        EventBus.getDefault().post(new MessageTemplateUpdateEvent());
    }

    public String getItemText(int pos) {
        MessageTemplate.CommonTemplateItem commonTemplateItem = mMessageTemplate.getItem(pos);
        if (commonTemplateItem != null) {
            return commonTemplateItem.getText();
        } else {
            return "";
        }
    }

    public void setIOnEditItemListener(IOnEditItemListener IOnEditItemListener) {
        mIOnEditItemListener = IOnEditItemListener;
    }

    public void updateTextItem(int pos, String text) {
        CommonTemplateItem commonTemplateItem = mMessageTemplate.getItem(pos);
        if (commonTemplateItem != null) {
            commonTemplateItem.setText(text);
            notifyItemUpdate(pos);
        }
    }

    public void updateParameterItem(int pos, int type) {
        CommonTemplateItem commonTemplateItem = mMessageTemplate.getItem(pos);
        if (commonTemplateItem != null && commonTemplateItem instanceof ParameterTemplateItem) {
            ((ParameterTemplateItem) commonTemplateItem).setType(type);
            notifyItemUpdate(pos);
        }
    }

    private void notifyItemUpdate(int pos){
        notifyItemChanged(pos);
        EventBus.getDefault().post(new MessageTemplateUpdateEvent());
        mMessageTemplate.save().subscribe();
    }

    @Override
    public void onItemClick(CommonTemplateItem item) {
        if (mIOnEditItemListener != null && item != null) {
            int pos = mMessageTemplate.getItemPosition(item);
            if (item instanceof ParameterTemplateItem) {
                mIOnEditItemListener.onEditParameterItem(pos);
            } else {
                mIOnEditItemListener.onEditTextItem(pos);
            }
        }
    }

    interface IOnEditItemListener {
        void onEditTextItem(int pos);

        void onEditParameterItem(int pos);
    }


}
