package com.lucio.milive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucio.milive.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: li xiao
 * created on: 2019/6/27
 */
public class ProgramsAdapter extends RecyclerView.Adapter<ProgramsAdapter.ProHolder> implements View.OnClickListener {
    private List<ProgramModel> list;
    private Context context;

    public ProgramsAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_progras_layout, parent, false);
        ProHolder holder = new ProHolder(view);
        holder.llRoot.setTag(holder);
        holder.llRoot.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProHolder holder, int position) {
        ProgramModel model = list.get(position);
        holder.tvName.setText(model.name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        ProHolder holder = (ProHolder) v.getTag();
        int position = holder.getAdapterPosition();
        if(clickCallback != null)
            clickCallback.onClick(position,list.get(position));
    }

    public void refresh(List<ProgramModel> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void refresh(){
        notifyDataSetChanged();
    }


    public class ProHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.ll_root)
        LinearLayout llRoot;

        public ProHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickCallback clickCallback;
    public void setClickCallback(OnItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }
    public interface OnItemClickCallback{
        void onClick(int position, ProgramModel bean);
    }
}
