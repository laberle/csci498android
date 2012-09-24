package csci498.laberle.lunchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "lunchlist.db";
	private static final int SCHEMA_VERSION = 1;

	public RestaurantHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE restaurants (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"name TEXT," +
			"address TEXT," +
			"restaurant_type_id INTEGER," +
			"notes TEXT," +
			"date TEXT);");
		db.execSQL("CREATE TABLE restaurant_types (" +
			"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"name TEXT);");

		initializeRestaurantTypesTable(db);
	}

	private void initializeRestaurantTypesTable(SQLiteDatabase db) {
		
		ContentValues cv = new ContentValues();
		cv.put("name", "sit_down");
		db.insert("restaurant_types", null, cv);
		cv.put("name", "take_out");
		db.insert("restaurant_types", null, cv);
		cv.put("name", "delivery");
		db.insert("restaurant_types", null, cv);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Intentionally left blank - need 2nd schema before this will be called
	}

	public void insert(String name, String address, int typeId, String notes, String date) {

		ContentValues cv = new ContentValues();

		cv.put("name", name);
		cv.put("address", address);
		cv.put("restaurant_type_id", typeId);
		cv.put("notes", notes);
		cv.put("date", date);

		if (cv.size() > 0) {
			getWritableDatabase().insert("restaurants", null, cv);
		}
	}

	public Cursor getAll() {
		return getReadableDatabase()
			.rawQuery("SELECT restaurants._id, restaurants.name, address, " +
					"restaurant_types.name, notes, date " +
				"FROM restaurants, restaurant_types " +
				"WHERE restaurants.restaurant_type_id = restaurant_types._id " +
				"ORDER BY restaurants.name",
				null);
	}

	public String getName(Cursor c) {
		return c.getString(1);
	}
	public String getAddress(Cursor c) {
		return c.getString(2);
	}
	public String getType(Cursor c) {
		return c.getString(3);
	}
	public String getNotes(Cursor c) {
		return c.getString(4);
	}
	public String getDate(Cursor c) {
		return c.getString(5);
	}

}