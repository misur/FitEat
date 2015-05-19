package me.fiteat.adapters;

import java.util.ArrayList;
import java.util.List;

import me.fiteat.R;
import me.fiteat.caluclations.BurnCalories;
import me.fiteat.shells.Meal;
import me.fiteat.statics.CustomDialogs;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MealsListViewAdapter extends BaseAdapter {

	private Context context;
	List<Meal> mealsList;
	String username;
	CustomDialogs cs;

	String gender;
	double age;
	double weight;
	double height;

	private DisplayMetrics displaymetrics;

	public MealsListViewAdapter(Context context, List<Meal> mealsList, double height, double weight, double age, String gender) {
		super();

		displaymetrics = context.getResources().getDisplayMetrics();
		cs = new CustomDialogs(context, displaymetrics);

		this.height = height;
		this.weight = weight;
		this.age = age;
		this.gender = gender;

		this.context = context;
		this.mealsList = mealsList;
	}

	public int getCount() {
		// return the number of records in cursor
		return mealsList.size();

	}

	// getView method is called for each item of ListView
	public View getView(int position, View view, ViewGroup parent) {
		// inflate the layout for each item of listView
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.meals_list_item, null);

		final Meal meal = mealsList.get(position);

		// get the reference of textViews
		Button mealButton = (Button) view.findViewById(R.id.mealButton);
		TextView mealPriceTextView = (TextView) view.findViewById(R.id.mealPriceTextView);
		TextView mealKcalTextView = (TextView) view.findViewById(R.id.mealKcalTextView);

		mealButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BurnCalories bc = new BurnCalories(gender, height, weight, Double.parseDouble(meal.getCalories()), age);
				cs.showCalorieDialog(meal.getIngredients(), meal.getCalories(), bc.timeWalking() + "", bc.timeRunning() + "",
						bc.timeBicycling() + "");
			}
		});
		
		mealButton.setText(meal.getName());
		mealPriceTextView.setText(meal.getPrice() + "€");
		mealKcalTextView.setText(meal.getCalories() + "kcal");

		return view;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private String getIngridientsFromList(ArrayList<String> ingridientsList) {
		String ingridients = "";
		for (String ingridient : ingridientsList) {
			ingridients += ingridient + ", ";
		}

		if (ingridients.length() > 1) {
			ingridients = ingridients.substring(0, ingridients.length() - 2);
		}
		return ingridients;
	}

}
