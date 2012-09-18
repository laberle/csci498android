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
				"type TEXT," +
				"notes TEXT," +
				"last_visit STRING);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Intentionally left blank - need 2nd schema before this will be called
	}
	
	public void insert(String name, String address, String type, String notes, String date) {
		ContentValues cv = new ContentValues();
		
		cv.put("name", name);
		cv.put("address", address);
		cv.put("type", type);
		cv.put("notes", notes);
		cv.put("last_visit", date);
		
		getWritableDatabase().insert("restaurants", "name", cv);
	}
	
	public Cursor getAll() {
		return getReadableDatabase()
			.rawQuery("SELECT _id, name, address, type, notes, last_visit" +
					"FROM restaurants " +
					"ORDER BY name",
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
	public String getLastVisit(Cursor c) {
		return c.getString(5);
	}
	
}