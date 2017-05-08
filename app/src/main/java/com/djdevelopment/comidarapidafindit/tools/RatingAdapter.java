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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dwane Jimenez on 5/2/2017.
 */

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private List<Ratings> mRatings;
    private Context mContext;
    UtilUI utilUI = new UtilUI();

    // Pass in the contact array into the constructor
    public RatingAdapter(Context context, List<Ratings> ratings) {
        mRatings = ratings;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public RatingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View ratingView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rating_detail, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(ratingView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RatingAdapter.ViewHolder holder, int position) {
        Ratings rating = mRatings.get(position);
        View view = holder.itemView;
        TextView nombreUsuarioRating = (TextView) view.findViewById(R.id.nombreUsuarioRating);
        RatingBar ratingBarRating = (RatingBar) view.findViewById(R.id.ratingBarRating);
        TextView commentRating = (TextView) view.findViewById(R.id.commentRating);
        ImageView imageViewRating = (ImageView) view.findViewById(R.id.imageViewRating);
        TextView fechaRating = (TextView) view.findViewById(R.id.fechaRating);

        Date date=new Date(Long.parseLong(rating.getFecha()));
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        String dateText = df2.format(date);

        nombreUsuarioRating.setText(rating.getUserName());
        ratingBarRating.setRating(rating.getRating());
        imageViewRating.setImageBitmap(utilUI.getBitmapFromURL(rating.getPhotoURL()));
        fechaRating.setText(dateText);
        commentRating.setText(rating.getComment());
    }

    @Override
    public int getItemCount() {
       return (mRatings.size()>=4) ? 4 :mRatings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
