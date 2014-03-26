package com.snakk.advertising.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;


/**
 * Convenience class used for passing live objects between intents
 * pulled from http://stackoverflow.com/questions/2736389/how-to-pass-object-from-one-activity-to-another-in-android#7683528
 */
public final class Sharable<T> implements Parcelable {

    private T m_object;

    /**
     * Only used for distinguishing null sharables in the debugger.
     * It's not referenced anywhere else, and wont be carried over when
     * readFromParcel is called.
     */
    private String debugTag = null;


    public static final Parcelable.Creator<Sharable> CREATOR = new  Parcelable.Creator<Sharable>() {
        public Sharable createFromParcel(Parcel in) {
            return new Sharable (in);
        }

        @Override
        public Sharable[] newArray(int size) {
            return new Sharable[size];
        }
    };

    public Sharable(final T obj, final String debugTag) {
        this.debugTag = debugTag;
        m_object = obj;
    }

    public Sharable(Parcel in) {
        readFromParcel(in);
    }

    public T obj() {
        return m_object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, int flags) {
        final long val;
        if(m_object != null) {
            val = SystemClock.elapsedRealtime() + m_object.hashCode();
        }
        else {
            val = 0;
        }
        out.writeLong(val);
        put(val, m_object);
    }

    @SuppressWarnings("unchecked")
    private void readFromParcel(final Parcel in) {
        final long val = in.readLong();
        m_object = (T)get(val);
    }

/////

    //TODO do something about cast safety here...
    private static final Map<Long, Object> s_sharableMap = new HashMap<Long, Object>(3);

    synchronized private static void put(long key, final Object obj) {
        s_sharableMap.put(key, obj);
    }

    synchronized private static Object get(long key) {
        return s_sharableMap.remove(key);
    }
}
