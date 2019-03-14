package uk.co.kubatek94.airmouse;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kubatek94 on 21/03/16.
 */
public abstract class HashMapAdapter<T> extends BaseAdapter {
    protected final Context context;
    private final Object lock = new Object();

    private final HashMap<Integer, T> items;
    private List<Integer> lookup = new ArrayList<Integer>();

    private int latestCount = 0;

    public HashMapAdapter(Context context) {
        this.context = context;
        items = new HashMap<Integer, T>();
    }

    public void addAll(Collection<T> collection) {
        synchronized (lock) {
            for (T item : collection) {
                int hash = item.hashCode();

                items.put(hash, item);
                lookup.add(hash);
                latestCount++;
            }
        }

        super.notifyDataSetChanged();
    }

    public void set(Collection<T> collection) {
        synchronized (lock) {
            lookup.clear();
            items.clear();

            for (T item : collection) {
                int hash = item.hashCode();

                items.put(hash, item);
                lookup.add(hash);
            }

            latestCount = collection.size();
        }

        super.notifyDataSetChanged();
    }

    public void add(T item) {
        int hash = item.hashCode();

        synchronized (lock) {
            items.put(hash, item);
            lookup.add(hash);
            latestCount++;
        }

        super.notifyDataSetChanged();
    }

    public void remove(T item) {
        int hash = item.hashCode();

        synchronized (lock) {
            items.remove(hash);
            lookup.remove(hash);
            latestCount++;
        }

        super.notifyDataSetChanged();
    }

    public void addIfNotExist(T item) {
        int hash = item.hashCode();

        synchronized (lock) {
            if (!items.containsKey(hash)) {
                items.put(hash, item);
                lookup.add(hash);
                latestCount++;

                super.notifyDataSetChanged();
            }
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public int getLatestCount() {
        int c = latestCount;
        latestCount = 0;
        return c;
    }

    @Override
    public T getItem(int position) {
        int hash = lookup.get(position);
        return items.get(hash);
    }

    @Override
    public long getItemId(int position) {
        return lookup.get(position);
    }
}
