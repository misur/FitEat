package me.fiteat.adapters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

import me.fiteat.activities.LoginSignupActivity;
import me.fiteat.activities.MainActivity;
import me.fiteat.activities.ProfileActivity;
import me.fiteat.activities.MealsActivity;
import me.fiteat.activities.SignUpActivity;
import me.fiteat.shells.Meal;
import me.fiteat.shells.Restaurants;
import me.fiteat.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RestaurantsListViewAdapter extends BaseAdapter {

	private Context context;
	// ArrayList<Game> gamesToDisplay;
	ArrayList<Restaurants[]> restaurantsList;
	List<Restaurants> restList;
	ArrayList<Meal> mealList;
	int resPos = -1;
	private Typeface type;
	boolean hadSecond = false;

	public RestaurantsListViewAdapter(Context context, ArrayList<Restaurants[]> restaurants, List<Restaurants> restList, ArrayList<Meal> mealList,
			int resPos) {
		super();
		this.context = context;
		this.restaurantsList = restaurants;
		this.restList = restList;
		this.mealList = mealList;
		this.resPos = resPos;
		this.type = Typeface.createFromAsset(context.getAssets(), "font-bold.ttf");
	}

	public int getCount() {
		// return the number of records in cursor
		return restaurantsList.size();
	}

	// getView method is called for each item of ListView
	public View getView(int position, View view, ViewGroup parent) {
		// inflate the layout for each item of listView
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.restaurants_items, null);

		final Restaurants r = restaurantsList.get(position)[0];
		final Restaurants r2 = restaurantsList.get(position)[1];
		System.out.println(r.getId());

		ImageView imgViewRestaurant1 = (ImageView) view.findViewById(R.id.imageViewRestaurant1);
		ImageView imgViewRestaurant2 = (ImageView) view.findViewById(R.id.imageViewRestaurant2);

		TextView txtViewRestaurant1 = (TextView) view.findViewById(R.id.textViewRestaurant1);
		TextView txtViewRestaurant2 = (TextView) view.findViewById(R.id.textViewRestaurant2);

		// restoran 1
		Picasso.with(context).load(r.getLogo()).placeholder(R.drawable.progress_animation).into(imgViewRestaurant1);
		txtViewRestaurant1.setTypeface(type);
		txtViewRestaurant1.setText(r.getName());

		final int pos = position;

		imgViewRestaurant1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent mealActivity = new Intent(context, MealsActivity.class);
				mealActivity.putExtra("id", r.getId());
				mealActivity.putExtra("restList", convertFromList(restList));
				if (resPos == pos) {
					mealActivity.putExtra("mealList", mealList);
				}
				mealActivity.putExtra("resPos", pos);
				((Activity) context).startActivityForResult(mealActivity, 0);
				((Activity) context).finish();

			}
		});

		// restoran2 (ako postoji)
		if (r2 != null) {
			txtViewRestaurant2.setTypeface(type);
			txtViewRestaurant2.setText(r2.getName());

			Picasso.with(context).load(r2.getLogo()).placeholder(R.drawable.progress_animation).into(imgViewRestaurant2);

			imgViewRestaurant2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent mealActivity = new Intent(context, MealsActivity.class);
					mealActivity.putExtra("id", r2.getId());
					mealActivity.putExtra("restList", convertFromList(restList));
					if (resPos == pos) {
						mealActivity.putExtra("mealList", mealList);
					}
					mealActivity.putExtra("resPos", pos);
					((Activity) context).startActivityForResult(mealActivity, 0);
					((Activity) context).finish();

				}
			});
		} else {
			txtViewRestaurant2.setVisibility(View.INVISIBLE);
			imgViewRestaurant2.setVisibility(View.INVISIBLE);

		}

		return view;
	}

	private ArrayList<Restaurants> convertFromList(List<Restaurants> list) {

		ArrayList<Restaurants> al = new ArrayList<Restaurants>();
		for (Restaurants restaurant : list) {
			al.add(restaurant);
		}
		return al;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}
