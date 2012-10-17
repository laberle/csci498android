package csci498.laberle.lunchlist;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DetailForm extends Activity {

	RestaurantHelper helper;
	EditText name;
	EditText address;
	EditText feed;
	EditText notes;
	RadioGroup types;
	DatePicker datePicker;
	String restaurantId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);

		initializeMembers();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}
	
	@Override
	public void onPause() {
		save();
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);

		String dateString =  ((Integer) datePicker.getMonth()).toString()
			+ " " + ((Integer) datePicker.getDayOfMonth()).toString()
			+ " " + ((Integer) datePicker.getYear()).toString();

		state.putString("name", name.getText().toString());
		state.putString("address", address.getText().toString());
		state.putString("notes", notes.getText().toString());
		state.putInt("type", types.getCheckedRadioButtonId());
		state.putString("date", dateString);
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);

		name.setText(state.getString("name"));
		address.setText(state.getString("address"));
		notes.setText(state.getString("notes"));
		types.check(state.getInt("type"));

		String date = state.getString("date");
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		String[] dateString = date.split(" ");
		cal.set(Integer.parseInt(dateString[2]), 
			Integer.parseInt(dateString[0]),
			Integer.parseInt(dateString[1]));
		datePicker.init(cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH), 
			cal.get(Calendar.DATE), null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.details_options, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.feed) {
			if (isNetworkAvailable()) {
				Intent i = new Intent(this, FeedActivity.class);
				i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
				startActivity(i);
			}
			else {
				Toast.makeText(this, "Sorry, the Internet is not available", 
					Toast.LENGTH_LONG)
					.show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null);
	}

	private void initializeMembers() {
		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.addr);
		types = (RadioGroup) findViewById(R.id.types);
		datePicker = (DatePicker) findViewById(R.id.date);
		notes = (EditText) findViewById(R.id.notes);
		feed = (EditText) findViewById(R.id.feed);

		helper = new RestaurantHelper(this);
		restaurantId = getIntent().getStringExtra(LunchList.ID_EXTRA);
		if (restaurantId != null) {
			load();
		}
	}

	private void save() {

		if (name.getText().toString().length() > 0) {
			RestaurantType type = getTypeFromRadioButtons(types);
			String dateString =  ((Integer) datePicker.getMonth()).toString()
				+ " " + ((Integer) datePicker.getDayOfMonth()).toString()
				+ " " + ((Integer) datePicker.getYear()).toString();

			if (restaurantId == null) {
				helper.insert(name.getText().toString(),
					address.getText().toString(),
					getTypeFromRadioButtons(types).getIndex() + 1, //_id offset from getIndex() by 1
					notes.getText().toString(),
					dateString,
					feed.getText().toString());	
			}
			else {
				helper.update(restaurantId,
					name.getText().toString(),
					address.getText().toString(),
					getTypeFromRadioButtons(types).getIndex() + 1, //_id offset from getIndex() by 1
					notes.getText().toString(),
					dateString,
					feed.getText().toString());
			}
		}

	}

	private RestaurantType getTypeFromRadioButtons(RadioGroup types) {
		switch (types.getCheckedRadioButtonId()) {
		case R.id.sit_down:
			return RestaurantType.SIT_DOWN;
		case R.id.take_out:
			return RestaurantType.TAKE_OUT;
		case R.id.delivery:
			return RestaurantType.DELIVERY;
		default:
			return null;
		}
	}

	private void load() {
		Cursor c = helper.getById(restaurantId);

		c.moveToFirst();
		name.setText(helper.getName(c));
		address.setText(helper.getAddress(c));
		notes.setText(helper.getNotes(c));
		feed.setText(helper.getFeed(c));
		String date = helper.getDate(c);

		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		String[] dateString = date.split(" ");
		cal.set(Integer.parseInt(dateString[2]), 
			Integer.parseInt(dateString[0]),
			Integer.parseInt(dateString[1]));
		datePicker.init(cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH), 
			cal.get(Calendar.DATE), null);

		if (helper.getType(c).equals("sit_down")) {
			types.check(R.id.sit_down);
		}
		else if (helper.getType(c).equals("take_out")) {
			types.check(R.id.take_out);
		}
		else if (helper.getType(c).equals("delivery")) {
			types.check(R.id.delivery);
		}

		c.close();
	}

}
