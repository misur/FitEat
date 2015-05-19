package me.fiteat.caluclations;

public class BurnCalories {
	double calories;

	String gender;
	double height;
	double weight;
	double age;

	double runningMET = 8.3;
	double walkingMET = 2.8;
	double bicyclingMET = 7.5;

	double BMR = 0.0;

	public double getBMR() {
		if (gender.equals("m")) {

			return 13.75 * weight + 5 * height - 6.76 * age + 66;

		} else if (gender.equals("f")) {
			return 9.56 * weight + 1.85 * height - 4.68 * age + 655;
		}
		return 0;
	}

	public int timeRunning() {

		return (int) Math.round((calories / ((getBMR() / 24) * runningMET)) * 60);
	}

	public int timeWalking() {
		return (int) Math.round( calories / ((getBMR() / 24) * walkingMET) * 60);
	}

	public int timeBicycling() {
		return (int) Math.round(calories / ((getBMR() / 24) * bicyclingMET) * 60);
	}

	public BurnCalories(String gender, double height, double weight, double calories, double age) {
		super();
		this.gender = gender;
		this.height = height;
		this.weight = weight;
		this.age = age;
		this.calories = calories;
	}

	public BurnCalories() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}

	public double getRunningMET() {
		return runningMET;
	}

	public void setRunningMET(double runningMET) {
		this.runningMET = runningMET;
	}

	public double getWalkingMET() {
		return walkingMET;
	}

	public void setWalkingMET(double walkingMET) {
		this.walkingMET = walkingMET;
	}

	public void setBMR(double bMR) {
		BMR = bMR;
	}

	public double getCalories() {
		return calories;
	}

	public void setCalories(double calories) {
		this.calories = calories;
	}

	public double getBicyclingMET() {
		return bicyclingMET;
	}

	public void setBicyclingMET(double bicyclingMET) {
		this.bicyclingMET = bicyclingMET;
	}

}
