package com.stanleyj.android.food_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;

/**
 * Created by Stanley on 2019/01/28.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    View mView;
    private ViewHolder.ClickListener mClickListener;

    public ViewHolder(View itemView) {
        super(itemView);

        mView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getAdapterPosition());

                return true;
            }
        });
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    //    set details to RecyclerView row
    public void setDetails(Context ctx, String title,
                           String image1, String image2, String image3,
                           String description, String sta, String price, String foodID) {
//        get views
        TextView tit = (TextView) mView.findViewById(R.id.VTitle);
        TextView proid = (TextView) mView.findViewById(R.id.foodID);
        TextView pri = (TextView) mView.findViewById(R.id.VPrice);
        TextView stat= (TextView) mView.findViewById(R.id.VStaus);
        TextView pDescription = (TextView) mView.findViewById(R.id.VDescription);
        ImageView rImage1 = (ImageView) mView.findViewById(R.id.Vimg1);
        ImageView rImage2 = (ImageView) mView.findViewById(R.id.Vimg2);
        ImageView rImage3 = (ImageView) mView.findViewById(R.id.Vimg3);

//
        try {
//        set data to views
            tit.setText(title);
            proid.setText(foodID);
            pri.setText("₦" + price);
            stat.setText(sta);
            pDescription.setText(description);
            rImage1.setImageBitmap(decodeFromFirebaseBase64(image1));
            rImage2.setImageBitmap(decodeFromFirebaseBase64(image2));
            rImage3.setImageBitmap(decodeFromFirebaseBase64(image3));

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Error Alert");
            alertDialog.setMessage("The following Error occurred\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }
   //    set details to RecyclerView view_item
    public void setDetailView(Context ctx, String title,String image1,String description, String price, String foodID) {
//        get views
        TextView tit = (TextView) mView.findViewById(R.id.viewTitle);
        TextView proid = (TextView) mView.findViewById(R.id.viewfoodID);
        TextView pri = (TextView) mView.findViewById(R.id.viePrice);
        TextView pDescription = (TextView) mView.findViewById(R.id.viewDes);
        ImageView rImage1 = (ImageView) mView.findViewById(R.id.viewImg);

//
        try {
//        set data to views
            tit.setText(title);
            proid.setText(foodID);
            pri.setText("₦" + price);
            pDescription.setText(description);
            rImage1.setImageBitmap(decodeFromFirebaseBase64(image1));

        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Error Alert");
            alertDialog.setMessage("The following Error occurred\n" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    // interface to send call backs
    public interface ClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
