package com.kts.yourweather;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mDaysOfWeek = new ArrayList<>();
    private ArrayList<String> mForecTemeperature = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<Integer>();
    private Context context;

    public RecyclerViewAdapter(ArrayList<String> mDaysOfWeek, ArrayList<String> mForecTemeperature, ArrayList<Integer> mImages, Context context) {
        this.mDaysOfWeek = mDaysOfWeek;
        this.mForecTemeperature = mForecTemeperature;
        this.mImages = mImages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewDay.setText(mDaysOfWeek.get(position));
        holder.textViewForec.setText(mForecTemeperature.get(position));
        holder.imageViewForec.setImageResource(mImages.get(position));

    }

    @Override
    public int getItemCount() {
        return mDaysOfWeek.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewDay;
        ImageView imageViewForec;
        TextView textViewForec;
        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDay = itemView.findViewById(R.id.textViewDayOfWeek);
            imageViewForec = itemView.findViewById(R.id.imageViewForecast);
            textViewForec = itemView.findViewById(R.id.textViewForecast);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
