package com.elegion.tracktor.ui.messageTemplate;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MessageTemplate {
    private static final String KEY = "MessageTemplateKey";
    private List<CommonTemplateItem> mItems;
    private List<String> mParameterTypesName;
    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    @SuppressLint("CheckResult")
    public MessageTemplate(SharedPreferences sharedPreferences, Gson gson) {
        mSharedPreferences = sharedPreferences;
        mGson = gson;
    }

    public void setParameterTypesName(List<String> parameterTypesName) {
        mParameterTypesName = parameterTypesName;
    }

    public String getMessage(List<String> parameterValues) {
        String ret = "";
        if (parameterValues != null && mParameterTypesName != null &&
                parameterValues.size() == mParameterTypesName.size() && mItems != null) {
            for (CommonTemplateItem item : mItems) {
                if (!ret.isEmpty() && !ret.endsWith(" ")) {
                    ret = ret + " ";
                }
                if (item instanceof TextTemplateItem) {
                    ret = ret + item.getText();
                } else if (item instanceof ParameterTemplateItem) {
                    ret = ret + parameterValues.get(((ParameterTemplateItem) item).mType);
                }
            }
        }
        return ret;
    }

    public Single<Boolean> load() {
        return Single.fromCallable(() -> {
            mItems = new ArrayList<>();
            if (mSharedPreferences != null && mGson != null) {
                try {
                    String loadedJSON = mSharedPreferences.getString(KEY, "");
                    String[] list = loadedJSON.split("\\|");
                    for (String item : list) {
                        if (!item.isEmpty()) {
                            if (item.startsWith("1")) {
                                mItems.add(mGson.fromJson(item.substring(1), TextTemplateItem.class));
                            } else if (item.startsWith("2")) {
                                mItems.add(mGson.fromJson(item.substring(1), ParameterTemplateItem.class));
                            }
                        }
                    }
                } catch (Throwable t){
                }
            }
            return true;
        }).subscribeOn(Schedulers.io());
    }

    public Single<Boolean> save() {
        return Single.fromCallable(() -> {
            if (mSharedPreferences != null && mGson != null && mItems != null) {
                String writeJSON = "";
                for (CommonTemplateItem item : mItems) {
                    if (!writeJSON.isEmpty()) {
                        writeJSON = writeJSON + "|";
                    }
                    if (item instanceof TextTemplateItem) {
                        writeJSON = writeJSON + "1";
                    } else {
                        writeJSON = writeJSON + "2";
                    }
                    writeJSON = writeJSON + mGson.toJson(item);
                }
                mSharedPreferences.edit().putString(KEY, writeJSON).apply();
            }
            return true;
        }).subscribeOn(Schedulers.io());
    }

    public void addTextItem(String text) {
        mItems.add(new TextTemplateItem(text));
        save().subscribe();
    }

    public void addParameterItem(int type) {
        mItems.add(new ParameterTemplateItem(type));
        save().subscribe();
    }

    public CommonTemplateItem getItem(int pos) {
        if (pos >= 0 && pos < mItems.size()) {
            return mItems.get(pos);
        } else return null;
    }

    public int getItemPosition(CommonTemplateItem item) {
        return mItems.indexOf(item);
    }

    public CommonTemplateItem removeItem(int pos) {
        CommonTemplateItem ret = null;
        if (pos >= 0 && pos < mItems.size()) {
            ret = mItems.get(pos);
            mItems.remove(pos);
            save().subscribe();
        }
        return ret;
    }

    public boolean moveItem(int fromPosition, int toPosition) {
        boolean ret = false;
        CommonTemplateItem prev = removeItem(fromPosition);
        if (prev != null) {
            //mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            mItems.add(toPosition, prev);
            save().subscribe();
            ret = true;
        }
        return ret;
    }

    public int getParameterItemTypesCount() {
        return mParameterTypesName != null ? mParameterTypesName.size() : 0;
    }

    public String getParameterItemTypeName(int id) {
        if (mParameterTypesName != null && id >= 0 && id < mParameterTypesName.size()) {
            return mParameterTypesName.get(id);
        }
        return "";
    }

    public int getItemCount() {
        return mItems.size();
    }

    public class CommonTemplateItem {
        private String mText;

        public CommonTemplateItem(String text) {
            mText = text;
        }

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            mText = text;
        }
    }

    public class TextTemplateItem extends CommonTemplateItem {
        public TextTemplateItem(String text) {
            super(text);
        }
    }

    public class ParameterTemplateItem extends CommonTemplateItem {
        private int mType;

        public ParameterTemplateItem(int type) {
            super(getParameterItemTypeName(type));
            setType(type);
        }

        public void setType(int type) {
            mType = type;
            setText(getParameterItemTypeName(type));
        }
    }

    public interface IOnReadyCallBack {
        void onReady();
    }
}
