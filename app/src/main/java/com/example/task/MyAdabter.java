package com.example.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdabter extends RecyclerView.Adapter<MyAdabter.MyViewHolder> {

    Context context;
    ArrayList<SourceLocation> sourceLocationArrayList;
    private ItemClickListener mItemClickListener;

    public MyAdabter(Context context, ArrayList<SourceLocation> sourceLocationArrayList,ItemClickListener mItemClickListener) {
        this.context = context;
        this.sourceLocationArrayList = sourceLocationArrayList;
        this.mItemClickListener=mItemClickListener;
    }



    @NonNull
    @Override
    public MyAdabter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false);


        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdabter.MyViewHolder holder, int position) {

        SourceLocation sourceLocation =sourceLocationArrayList.get(position);

        holder.sourceName.setText(sourceLocation.name);

        holder.itemView.setOnClickListener(view -> {
            mItemClickListener.onItemClick(sourceLocationArrayList.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return sourceLocationArrayList.size();
    }

    public interface ItemClickListener{
        void onItemClick(SourceLocation sourceLocation);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sourceName;
        Double latitude , longitude;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            sourceName=itemView.findViewById(R.id.tvItemName);
        }
    }
}
