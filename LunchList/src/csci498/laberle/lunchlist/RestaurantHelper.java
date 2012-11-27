package csci498.laberle.lunchlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RestaurantHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "lunchlist.db";
	private static final int SCHEMA_VERSION = 4;

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
			"date TEXT," +
			"feed TEXT," +
			"latitude REAL," +
			"longitude REAL," +
			"phone TEXT);");
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
		if (oldVersion < 2) {
			db.execSQL("ALTER TABLE restaurants ADD COLUMN feed TEXT");
		}
		
		if (oldVersion < 3) {
			db.execSQL("ALTER TABLE restaurants ADD COLUMN latitude REAL");
			db.execSQL("ALTER TABLE restaurants ADD COLUMN longitude REAL");
		}
		
		if (oldVersion < 4) {
			db.execSQL("ALTER TABLE restaurants ADD COLUMN phone TEXT");
		}
	}

	public void insert(String name, String address,
		int typeId, String notes, String date, String feed, String phone) {

		ContentValues cv = new ContentValues();
		
		cv.put("name", name);
		cv.put("address", address);
		cv.put("restaurant_type_id", typeId);
		cv.put("notes", notes);
		cv.put("date", date);
		cv.put("feed", feed);
		cv.put("phone", phone);

		if (cv.size() > 0) {
			getWritableDatabase().insert("restaurants", null, cv);
		}
	}
	
	public void update(String id, String name, String address, 
		int typeId, String notes, String date, String feed, String phone) {

		ContentValues cv = new ContentValues();
		String[] args = {id};

		cv.put("name", name);
		cv.put("address", address);
		cv.put("restaurant_type_id", typeId);
		cv.put("notes", notes);
		cv.put("date", date);
		cv.put("feed", feed);
		cv.put("phone", phone);

		if (cv.size() > 0) {
			getWritableDatabase().update("restaurants", cv, "_id=?", args);
		}
	}
	
	public void updateLocation(String id, double lat, double lon) {
		ContentValues cv = new ContentValues();
		String[] args = {id};
		
		cv.put("latitude", lat);
		cv.put("longitude", lon);
		
		if (cv.size() > 0) {
			getWritableDatabase().update("restaurants", cv, "_id=?", args);
		}
	}

	public Cursor getAll(String orderBy) {
		return getReadableDatabase()
			.rawQuery("SELECT restaurants._id, restaurants.name as name, address, " +
					  "restaurant_types.name, notes, date, feed, latitude, longitude, phone " +
					  "FROM restaurants, restaurant_types " +
					  "WHERE restaurants.restaurant_type_id = restaurant_types._id " +
					  "ORDER BY " + orderBy, 
					  null);
	}
	
	public Cursor getAllRestaurantNames() {
		return getReadableDatabase().rawQuery("SELECT _ID, name FROM restaurants", null);
	}
	
	public String getRandomRestaurantName() {
		String name;
		
		Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM restaurants", null);
		c.moveToFirst();
		int count = c.getInt(0);
		c.close();
		
		if (count > 0) {
			int offset = (int) (count * Math.random());
			String args[] = {String.valueOf(offset)};
	
			c = getReadableDatabase().rawQuery("SELECT _ID, name FROM restaurants LIMIT 1 OFFSET ?", args);
			c.moveToFirst();
			c.close();
			name = getName(c);
		}
		else {
			name = null;
		}
		return name;
	}
	
	public String getIdFromRestaurantName(String name) {
		String args[] = {name};
		
		Cursor c = getReadableDatabase().rawQuery("SELECT _ID FROM restaurants WHERE name = ?", args );
		c.moveToFirst();
		String id = c.getString(0);
		c.close();
		return id;	
	}
	
	public Cursor getById(String id) {
		String[] args = {id};
		
		return getReadableDatabase()
			.rawQuery("SELECT restaurants._id, restaurants.name, address, " +
					  "restaurant_types.name, notes, date, feed, latitude, longitude, phone " +
					  "FROM restaurants, restaurant_types " +
					  "WHERE restaurants.restaurant_type_id = restaurant_types._id " +
					  "AND restaurants._id = ? " +
					  "ORDER BY restaurants.name",
					  args);
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
	public String getFeed(Cursor c) {
		return c.getString(6);
	}
	public double getLatitude(Cursor c) {
		return c.getDouble(7);
	}
	public double getLongitude(Cursor c) {
		return c.getDouble(8);
	}
	public String getPhone(Cursor c) {
		return c.getString(9);
	}
}