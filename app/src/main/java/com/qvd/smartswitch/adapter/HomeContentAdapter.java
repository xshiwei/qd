package com.qvd.smartswitch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qvd.smartswitch.R;
import com.qvd.smartswitch.model.home.Footer;
import com.qvd.smartswitch.model.home.Header;
import com.qvd.smartswitch.model.home.Test1Vo;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class HomeContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Object> data;
    private int ITEM_HEADER = 1, ITEM_CONTENT = 2;

    public HomeContentAdapter(Context context, List<Object> data) {
        this.context = context;
        this.data = data;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClickListener(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_HEADER) {
            view = LayoutInflater.from(context).inflate(R.layout.item_home_content, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == ITEM_CONTENT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_home_content_item, parent, false);
            return new ViewHolderContent(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            Header header = (Header) data.get(position);
            ((MyViewHolder) holder).tv_room.setText(header.getText());
        } else if (holder instanceof ViewHolderContent) {
            Footer footer = (Footer) data.get(position);
            ((ViewHolderContent) holder).tv_text.setText(footer.getDevice());
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Header) {
            return ITEM_HEADER;
        } else if (data.get(position) instanceof Footer) {
            return ITEM_CONTENT;
        }
        return ITEM_CONTENT;
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 1 : data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_room;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_room = itemView.findViewById(R.id.tv_room);
        }
    }

    public class ViewHolderContent extends RecyclerView.ViewHolder {
        TextView tv_text;

        public ViewHolderContent(View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }
}

