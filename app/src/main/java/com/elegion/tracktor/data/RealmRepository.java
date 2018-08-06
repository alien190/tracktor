package com.elegion.tracktor.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class RealmRepository implements IRepository{

    private AtomicLong currentId;

    public RealmRepository() {

    }

    @Override
    public long insertItem(Object o) {
        return 0;
    }

    @Override
    public Object getItem(long id) {
        return null;
    }

    @Override
    public boolean deleteItem(long id) {
        return false;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public void updateItem(Object o) {

    }
}
