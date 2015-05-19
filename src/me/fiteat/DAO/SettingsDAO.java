package me.fiteat.DAO;

import me.fiteat.database.DatabaseFitEat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SettingsDAO {
	private SQLiteDatabase database;
	private DatabaseFitEat dbHelper;
	private String[] allColumns = { DatabaseFitEat.SETTINGS_ID,DatabaseFitEat.SETTINGS_USER_ID,
			DatabaseFitEat.SETTINGS_USERNAME, DatabaseFitEat.SETTINGS_EMAIL, DatabaseFitEat.SETTINGS_HEIGHT,
			DatabaseFitEat.SETTINGS_WEIGHT, DatabaseFitEat.SETTINGS_GENDER, DatabaseFitEat.SETTINGS_BIRTHDAY};

	public SettingsDAO(Context context) {
		dbHelper = new DatabaseFitEat(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public boolean deleteAllData() {
		open();
		database.delete(DatabaseFitEat.TABLE_SETTINGS,null,null);
		close();
		return true;
	}

	public boolean saveToDatabase(int user_id,String username, String email, 
			String height,String weight, String gender, String birthday) {
		open();
		ContentValues values = new ContentValues();
		values.put(DatabaseFitEat.SETTINGS_USER_ID	, user_id);
		values.put(DatabaseFitEat.SETTINGS_USERNAME, username);
		values.put(DatabaseFitEat.SETTINGS_EMAIL, email);
		values.put(DatabaseFitEat.SETTINGS_HEIGHT, height);
		values.put(DatabaseFitEat.SETTINGS_WEIGHT, weight);
		values.put(DatabaseFitEat.SETTINGS_GENDER, gender);
		values.put(DatabaseFitEat.SETTINGS_BIRTHDAY, birthday);
		database.insert(DatabaseFitEat.TABLE_SETTINGS, null, values);
		close();
		return true;
	}

	private boolean updateValue(String value, String column) {
		String id = getId();
		open();
		ContentValues values = new ContentValues();
		values.put(column, value);
		database.update(DatabaseFitEat.TABLE_SETTINGS, values, "id=?",
				new String[] { id });
		close();
		return true;
	}

	private String getValue(int columnNumber) {
		open();
		Cursor cursor = database.query(DatabaseFitEat.TABLE_SETTINGS,
				allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		String value = cursor.getString(columnNumber);
		cursor.close();
		close();
		return value;
	}
	
	public String getId() {
		return getValue(0);
	}		

	public String getUserID() {
		return getValue(1);
	}		


	public String getUsername() {
		return getValue(2);
	}
	public String getEmail() {
		return getValue(3);
	}
	public String getHeight() {
		return getValue(4);	
	}
	public String getWeight() {
		return getValue(5);
	}
	public String getGender() {
		return getValue(6);
	}
	public String getBirthday() {
		return getValue(7);
	}
	
	public void updateSettings(String height,String weight,String gender,String birthday){
		updateValue(height, DatabaseFitEat.SETTINGS_HEIGHT);
		updateValue(weight,DatabaseFitEat.SETTINGS_WEIGHT);
		updateValue(gender, DatabaseFitEat.SETTINGS_GENDER);
		updateValue(birthday, DatabaseFitEat.SETTINGS_BIRTHDAY);
	}
	

	

//	public boolean saveUsername(String username) {
//		String oldUsername = "";
//		try {
//			oldUsername = getUsername();
//		} catch (Exception e) {
//
//		}
//		if (oldUsername == null) {
//			oldUsername = "";
//		}
//		boolean sucessful = false;
//		if (oldUsername.equals("")) {
//			sucessful = saveToDatabase(username, "empty", "empty");
//		} else {
//			sucessful = updateValue(username, DatabaseGeostep.SETTINGS_USERNAME);
//		}
//		return sucessful;
//	}

//	public boolean saveLanguage(String language) {
//		String oldLanguage = "";
//		try {
//			oldLanguage = getLanguage();
//		} catch (Exception e) {
//
//		}
//		if (oldLanguage == null) {
//			oldLanguage = "";
//		}
//		boolean sucessful = false;
//		if (oldLanguage.equals("")) {
//			sucessful = saveToDatabase("empty", language, "empty");
//		} else {
//			sucessful = updateValue(language, DatabaseGeostep.SETTINGS_LANGUAGE);
//		}
//		return sucessful;
//	}
	
//	public boolean saveGameNumber(String gameNumber) {
//		String oldGameNumber = "";
//		try {
//			oldGameNumber = getGameNumber();
//		} catch (Exception e) {
//
//		}
//		if (oldGameNumber == null) {
//			oldGameNumber = "";
//		}
//		boolean sucessful = false;
//		if (oldGameNumber.equals("")) {
//			sucessful = saveToDatabase("empty", "empty", gameNumber);
//		} else {
//			sucessful = updateValue(gameNumber, DatabaseGeostep.SETTINGS_GAME_NUMBER);
//		}
//		return sucessful;
//	}
}
