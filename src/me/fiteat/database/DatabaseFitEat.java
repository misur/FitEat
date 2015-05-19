package me.fiteat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseFitEat extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "fiteat.db";
	private static final int DATABASE_VERSION = 1;

	// ************************************************GAME***************************************************************//
	public static final String TABLE_GAME = "game";
	public static final String GAME_ID = "id";
	public static final String GAME_NAME = "name";
	public static final String GAME_DESCRIPTION = "description";
	public static final String GAME_GAME_NUMBER = "game_number";
	public static final String GAME_VALID_FROM = "valid_from";
	public static final String GAME_VALID_TO = "valid_to";
	public static final String GAME_IS_OUTDOORS = "is_outdoors";
	public static final String GAME_IS_TIME_ATTACK = "is_time_attack";

	private static final String CREATE_TABLE_GAME = "create table if not exists "
			+ TABLE_GAME + "(" 
			+ GAME_ID
			+ " integer primary key autoincrement, "
			+ GAME_NAME
			+ " text not null, " 
			+ GAME_DESCRIPTION
			+ " text not null, "
			+ GAME_GAME_NUMBER
			+ " integer not null, "
			+ GAME_VALID_FROM
			+ " text not null, "
			+ GAME_VALID_TO
			+ " text not null, "
			+ GAME_IS_OUTDOORS
			+ " integer not null, "
			+ GAME_IS_TIME_ATTACK
			+ " integer not null);";
	
	// ************************************************CLUE***************************************************************//
	
	public static final String TABLE_CLUE = "clue";
	public static final String CLUE_ID = "id";
	public static final String CLUE_LAT = "lat";
	public static final String CLUE_LNG = "lng";
	public static final String CLUE_RIDDLE = "riddle";
	public static final String CLUE_RADIUS = "radius";
	public static final String CLUE_C_LAT = "c_lat";
	public static final String CLUE_C_LON = "c_lon";
	public static final String CLUE_INDOOR_CODE = "indoor_code";
	public static final String CLUE_CLUE_NUMBER = "clue_number";
	public static final String CLUE_FK_GAME_ID = "fk_game_id";
	
	private static final String CREATE_TABLE_CLUE = "create table if not exists "
			+ TABLE_CLUE + "(" 
			+ CLUE_ID
			+ " integer primary key autoincrement, "
			+ CLUE_LAT
			+ " text not null, " 
			+ CLUE_LNG
			+ " text not null, "
			+ CLUE_RIDDLE
			+ " text not null, "
			+ CLUE_RADIUS
			+ " text not null, "
			+ CLUE_C_LAT
			+ " text not null, "
			+ CLUE_C_LON
			+ " text not null, "
			+ CLUE_INDOOR_CODE
			+ " text, "
			+ CLUE_CLUE_NUMBER
			+ " int, "
			+ CLUE_FK_GAME_ID
			+ " integer not null, "
			+ "FOREIGN KEY(" + CLUE_FK_GAME_ID + ") REFERENCES " + TABLE_GAME + "(" + GAME_ID + ")"
			+ ");";

	// ************************************************SETTINGS***************************************************************//
	
		public static final String TABLE_SETTINGS = "settings";
		public static final String SETTINGS_ID = "id";
		public static final String SETTINGS_USER_ID = "user_id";
		public static final String SETTINGS_USERNAME = "username";
		public static final String SETTINGS_EMAIL = "email";
		public static final String SETTINGS_HEIGHT = "height";
		public static final String SETTINGS_WEIGHT = "weight";
		public static final String SETTINGS_GENDER = "gender";
		public static final String SETTINGS_BIRTHDAY = "birthday";
		

		
		private static final String CREATE_TABLE_SETTINGS = "create table if not exists "
				+ TABLE_SETTINGS + "(" 
				+ SETTINGS_ID
				+ " integer primary key autoincrement, "
				+ SETTINGS_USER_ID
				+ " integer, "
				+ SETTINGS_USERNAME
				+ " text, "
				+ SETTINGS_EMAIL
				+ " text, "
				+ SETTINGS_HEIGHT
				+ " text, "
				+ SETTINGS_WEIGHT
				+ " text, "
				+ SETTINGS_GENDER
				+ " text, "
				+ SETTINGS_BIRTHDAY
				+ " text );";

	// ********************************************USER_TABLE*************************************************************************//
		
		
			public static final String TABLE_USER_TABLE = "user_table";
			public static final String USER_TABLE_ID = "id";
			public static final String USER_TABLE_USERNAME = "username";
			public static final String USER_TABLE_PASSWORD = "password";

			
			private static final String CREATE_TABLE_USER_TABLE = "create table if not exists "
					+ TABLE_USER_TABLE + "(" 
					+ USER_TABLE_ID
					+ " integer primary key autoincrement, "
					+ USER_TABLE_USERNAME
					+ " text not null, "
					+ USER_TABLE_PASSWORD
					+ " text not null);";

	// *************************************************USER_TABLE_GAME**************************************************************************//
			
			public static final String TABLE_USER_TABLE_GAME = "user_table_game";
			public static final String USER_TABLE_GAME_ID = "id";
			public static final String USER_TABLE_GAME_STATUS = "status";
			public static final String USER_TABLE_GAME_ACTIVE_CLUE_NUMBER = "active_clue_number";
			public static final String USER_TABLE_GAME_FK_USER_ID = "fk_user_id";
			public static final String USER_TABLE_GAME_FK_GAME_ID = "fk_game_id";

			private static final String CREATE_TABLE_USER_TABLE_GAME = "create table if not exists "
					+ TABLE_USER_TABLE_GAME + "(" 
					+ USER_TABLE_GAME_ID
					+ " integer primary key autoincrement, "
					+ USER_TABLE_GAME_STATUS
					+ " int not null, "
					+ USER_TABLE_GAME_ACTIVE_CLUE_NUMBER
					+ " integer, "
					+ USER_TABLE_GAME_FK_USER_ID
					+ " integer not null, "
					+ USER_TABLE_GAME_FK_GAME_ID
					+ " integer not null, "
					+ "FOREIGN KEY(" + USER_TABLE_GAME_FK_USER_ID + ") REFERENCES " + TABLE_USER_TABLE + "(" + USER_TABLE_ID + "), "
					+ "FOREIGN KEY(" + USER_TABLE_GAME_FK_GAME_ID + ") REFERENCES " + TABLE_GAME + "(" + GAME_ID + ")"
					+ ");";
			
	// *******************************************************************************************************************************************//
	
	public DatabaseFitEat(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_GAME);
		database.execSQL(CREATE_TABLE_CLUE);
		database.execSQL(CREATE_TABLE_SETTINGS);
		database.execSQL(CREATE_TABLE_USER_TABLE);
		database.execSQL(CREATE_TABLE_USER_TABLE_GAME);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLUE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_TABLE_GAME);
		onCreate(db);
	}
	
	public void deleteSettings(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
	}

}
