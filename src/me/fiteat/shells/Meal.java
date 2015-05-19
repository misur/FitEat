package me.fiteat.shells;

import java.io.Serializable;

public class Meal implements Serializable{
	
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String price;
	String calories;
	String ingredients;

	public Meal() {
		super();
	}

	public Meal(String id, String name, String price, String calories, String ingredients) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.calories = calories;
		this.ingredients = ingredients;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCalories() {
		return calories;
	}

	public void setCalories(String calories) {
		this.calories = calories;
	}

	@Override
	public String toString() {
		return "Meal [id=" + id + ", name=" + name + ", price=" + price + ", calories=" + calories + ", ingredients=" + ingredients + "]";
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

}
