/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView Wrapper
 *
 * @author yangzc
 */
public class SimpleRecycleView extends RecyclerView {

    private List<View> mHeaders = new ArrayList<View>();
    private List<View> mFooters = new ArrayList<View>();

    public SimpleRecycleView(Context context, AttributeSet attrs, int arg2) {
        super(context, attrs, arg2);
    }

    public SimpleRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleRecycleView(Context context) {
        super(context);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    @Override
    public void setItemAnimator(ItemAnimator animator) {
        super.setItemAnimator(animator);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!mHeaders.isEmpty() || !mFooters.isEmpty()) {
            super.setAdapter(new InternalAdapter(adapter, mHeaders, mFooters));
        } else {
            super.setAdapter(adapter);
        }
    }

    public void addHeader(View header) {
        mHeaders.add(header);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void removeHeader(View header) {
        mHeaders.remove(header);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getHeaderCount() {
        if (mHeaders != null)
            return mHeaders.size();
        return 0;
    }

    public void addFooter(View footer) {
        mFooters.add(footer);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void removeFooter(View footer) {
        mFooters.remove(footer);
        Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getFooterCount() {
        if (mFooters != null)
            return mFooters.size();
        return 0;
    }

    private class InternalAdapter extends RecyclerView.Adapter {

        private List<View> mHeaders;
        private List<View> mFooters;
        private Adapter mAdapter;

        public InternalAdapter(Adapter adapter, List<View> headers, List<View> footers) {
            this.mAdapter = adapter;
            this.mHeaders = headers;
            this.mFooters = footers;
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (viewType < getHeaderCount()) {
                //header
                View header = mHeaders.get(viewType);
                return new SingleViewHolder(header);
            } else if (viewType < getHeaderCount() + getFooterCount()) {
                //footer
                View footer = mFooters.get(viewType - getHeaderCount());
                return new SingleViewHolder(footer);
            } else {
                return mAdapter.onCreateViewHolder(viewGroup, viewType - getHeaderCount());
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            if (position < getHeaderCount()) {
                //header
            } else if (position < getHeaderCount() + mAdapter.getItemCount()) {
                mAdapter.onBindViewHolder(viewHolder, position - getHeaderCount());
            } else {
                //footer
            }
        }

        @Override
        public int getItemViewType(int position) {
            int headerCnt = getHeaderCount();
            int footerCnt = getFooterCount();
            if (position < headerCnt) {
                return position;
            } else if(position < headerCnt + mAdapter.getItemCount()) {
                return mAdapter.getItemViewType(position) + headerCnt + footerCnt;
            } else {
                return position - mAdapter.getItemCount();
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            int itemCount = 0;
            if (mHeaders != null)
                itemCount += mHeaders.size();
            if (mFooters != null)
                itemCount += mFooters.size();
            if (mAdapter != null)
                itemCount += mAdapter.getItemCount();
            return itemCount;
        }

        private int getHeaderCount() {
            if (mHeaders != null) {
                return mHeaders.size();
            }
            return 0;
        }

        private int getFooterCount() {
            if (mFooters != null) {
                return mFooters.size();
            }
            return 0;
        }

        private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition + getHeaderCount(), toPosition + getHeaderCount());
            }
        };
    }

    private class SingleViewHolder extends RecyclerView.ViewHolder {

        public SingleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
