package com.djdevelopment.comidarapidafindit.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.Restaurants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dwane Jimenez on 5/10/2017.
 */

public class ValuedPlacesAdapter extends RecyclerView.Adapter<ValuedPlacesAdapter.ViewHolder>{
    private List<Restaurants> restaurants;
    private OnItemClickLister itemClickLister;
    private float rating = 0;
    private ArrayList<String> ratingList = new ArrayList<>();

    public ValuedPlacesAdapter(List<Restaurants> restaurants, OnItemClickLister itemClickLister) {
        this.restaurants = restaurants;
        this.itemClickLister = itemClickLister;
    }

    @Override
    public ValuedPlacesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ValuedPlacesAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_valued_places, parent, false));
    }

    @Override
    public void onBindViewHolder(ValuedPlacesAdapter.ViewHolder holder, int position) {
        holder.bindView(restaurants.get(position),itemClickLister);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtValuedPlaces;
        RatingBar ratingBarValuedPlaces;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.txtValuedPlaces = (TextView) itemView.findViewById(R.id.txtValuedPlaces);
            this.ratingBarValuedPlaces = (RatingBar) itemView.findViewById(R.id.ratingBarValuedPlaces);
        }
        public void bindView(final Restaurants restaurants, final OnItemClickLister listener){
            try {

                txtValuedPlaces.setText(restaurants.getRestName());
                ratingBarValuedPlaces.setRating(getRatingList(restaurants));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnItemClick(restaurants,getAdapterPosition());
                    }
                });
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public interface OnItemClickLister{
        void OnItemClick(Restaurants name, int position);
    }

    public float getRatingList(Restaurants restaurants){
        try {
            if (ratingList.size() != 0) {
                rating = 0;
                ratingList.clear();
            }
            for (String restRating : restaurants.getRating().values()) {
                try {
                    JSONObject jObj = new JSONObject(restRating);
                    ratingList.add(jObj.getString("rating"));
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            int i;
            for (i = 0; i < ratingList.size(); i++) {
                rating = Float.parseFloat(ratingList.get(i)) + rating;

            }
            rating = rating / i;

            return rating = Math.round(rating * 100.0f) / 100.0f;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

}
