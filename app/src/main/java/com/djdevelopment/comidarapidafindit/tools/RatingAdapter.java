package com.djdevelopment.comidarapidafindit.tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.djdevelopment.comidarapidafindit.R;
import com.djdevelopment.comidarapidafindit.data.Ratings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dwane Jimenez on 5/2/2017.
 */

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private List<Ratings> ratings;
    private Context context;
    private boolean isShowMore;

    // Pass in the contact array into the constructor
    public RatingAdapter(Context context, List<Ratings> ratings, boolean isShowMore) {
        this.ratings = ratings;
        this.context = context;
        this.isShowMore = isShowMore;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public RatingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View ratingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating_detail, parent, false);

        // Return a new holder instance
        return new ViewHolder(ratingView);
    }

    @Override
    public void onBindViewHolder(RatingAdapter.ViewHolder holder, int position) {
        holder.bindView(ratings.get(position));
    }

    @Override
    public int getItemCount() {
        if(ratings.size()>=4 && !isShowMore){
            return 4;
        }
        else if(ratings.size()>=4 && isShowMore){
            return ratings.size();
        }
        else{
            return ratings.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUsuarioRating;
        RatingBar ratingBarRating;
        TextView commentRating;
        ImageView imageViewRating;
        TextView fechaRating;
        UtilUI utilUI = new UtilUI();

        public ViewHolder(View itemView) {
            super(itemView);
            this.nombreUsuarioRating = (TextView) itemView.findViewById(R.id.nombreUsuarioRating);
            this.ratingBarRating = (RatingBar) itemView.findViewById(R.id.ratingBarRating);
            this.commentRating = (TextView) itemView.findViewById(R.id.commentRating);
            this.imageViewRating = (ImageView) itemView.findViewById(R.id.imageViewRating);
            this.fechaRating = (TextView) itemView.findViewById(R.id.fechaRating);
        }
        public void bindView(Ratings ratings){

            Date date=new Date(Long.parseLong(ratings.getFecha()));
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String dateText = df2.format(date);

            nombreUsuarioRating.setText(ratings.getUserName());
            ratingBarRating.setRating(ratings.getRating());
            imageViewRating.setImageBitmap(utilUI.getBitmapFromURL(ratings.getPhotoURL()));
            fechaRating.setText(dateText);
            commentRating.setText(ratings.getComment());
        }
    }
}
