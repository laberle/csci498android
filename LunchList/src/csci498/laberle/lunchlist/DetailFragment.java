package csci498.laberle.lunchlist;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment {

	private static final String ARG_REST_ID = "csci498.lunchlist.laberle.ARG_REST_ID";

	RestaurantHelper helper;
	LocationManager locationManager;
	double latitude;
	double longitude;

	EditText name;
	EditText address;
	EditText feed;
	EditText notes;
	TextView location;
	RadioGroup types;
	DatePicker datePicker;
	String restaurantId;

	public static DetailFragment newInstance(long id) {
		DetailFragment result = new DetailFragment();
		Bundle args = new Bundle();

		args.putString(ARG_REST_ID, String.valueOf(id));
		result.setArguments(args);
		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.detail_form, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeMembers();
	}

	private RestaurantHelper getHelper() {
		if (helper == null) {
			helper = new RestaurantHelper(getActivity());
		}

		return helper;
	}

	@Override
	public void onPause() {
		save();
		getHelper().close();
		locationManager.removeUpdates(onLocationChange);

		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.details_options, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (restaurantId == null) {
			menu.findItem(R.id.location).setEnabled(false);
			menu.findItem(R.id.map).setEnabled(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.feed) {
			if (isNetworkAvailable()) {
				Intent i = new Intent(getActivity(), FeedActivity.class);
				i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
				startActivity(i);
			}
			else {
				Toast.makeText(getActivity(), "Sorry, the Internet is not available", 
					Toast.LENGTH_LONG)
					.show();
			}
			return true;
		}
		else if (item.getItemId() == R.id.location) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
			return true;
		}
		else if (item.getItemId() == R.id.map) {
			Intent i = new Intent(getActivity(), RestaurantMap.class);
			i.putExtra(RestaurantMap.EXTRA_LATITUDE, latitude);
			i.putExtra(RestaurantMap.EXTRA_LONGITUDE, longitude);
			i.putExtra(RestaurantMap.EXTRA_NAME, name.getText().toString());

			startActivity(i);
			return true;
		}
		else if (item.getItemId() == R.id.help) {
			startActivity(new Intent(getActivity(), HelpPage.class));
		}

		return super.onOptionsItemSelected(item);
	}

	LocationListener onLocationChange = new LocationListener() {

		public void onLocationChanged(Location fix) {
			getHelper().updateLocation(restaurantId, fix.getLatitude(), fix.getLongitude());
			location.setText(String.valueOf(fix.getLatitude()+ ", " + fix.getLongitude()));
			locationManager.removeUpdates(onLocationChange);

			Toast.makeText(getActivity(), "Location saved", Toast.LENGTH_LONG)
			.show();
		}

		public void onProviderDisabled(String provider) {
			//Required for interface, not used
		}

		public void onProviderEnabled(String provider) {
			//Required for interface, not used
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			//Required for interface, not used
		}

	};

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null);
	}

	private void initializeMembers() {
		name = (EditText) getView().findViewById(R.id.name);
		address = (EditText) getView().findViewById(R.id.addr);
		types = (RadioGroup) getView().findViewById(R.id.types);
		datePicker = (DatePicker) getView().findViewById(R.id.date);
		notes = (EditText) getView().findViewById(R.id.notes);
		feed = (EditText) getView().findViewById(R.id.feed);
		location = (TextView) getView().findViewById(R.id.location);

		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		latitude = 0.0d;
		longitude = 0.0d;

		Bundle args = getArguments();

		if (args != null) {
			loadRestaurant(args.getString(ARG_REST_ID));
		}
	}

	public void loadRestaurant(String restaurantId) {
		this.restaurantId = restaurantId;

		if (restaurantId != null) {
			load();
		}
	}

	private void save() {
		if (name.getText().toString().length() > 0) {
			String dateString =  ((Integer) datePicker.getMonth()).toString()
				+ " " + ((Integer) datePicker.getDayOfMonth()).toString()
				+ " " + ((Integer) datePicker.getYear()).toString();

			if (restaurantId == null) {
				getHelper().insert(name.getText().toString(),
					address.getText().toString(),
					getTypeFromRadioButtons(types).getIndex() + 1, //_id offset from getIndex() by 1
					notes.getText().toString(),
					dateString,
					feed.getText().toString());	
			}
			else {
				getHelper().update(restaurantId,
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
		Cursor c = getHelper().getById(restaurantId);

		c.moveToFirst();
		name.setText(getHelper().getName(c));
		address.setText(getHelper().getAddress(c));
		notes.setText(getHelper().getNotes(c));
		feed.setText(getHelper().getFeed(c));
		location.setText(getLocationStringFromCursor(c));
		fillDatePicker(getHelper().getDate(c));

		if (getHelper().getType(c).equals("sit_down")) {
			types.check(R.id.sit_down);
		}
		else if (getHelper().getType(c).equals("take_out")) {
			types.check(R.id.take_out);
		}
		else if (getHelper().getType(c).equals("delivery")) {
			types.check(R.id.delivery);
		}

		latitude = getHelper().getLatitude(c);
		longitude = getHelper().getLongitude(c);

		c.close();
	}

	private void fillDatePicker(String date) {
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		String[] dateString = date.split(" ");
		cal.set(Integer.parseInt(dateString[2]), 
			Integer.parseInt(dateString[0]),
			Integer.parseInt(dateString[1]));

		datePicker.init(cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH), 
			cal.get(Calendar.DATE), null);
	}

	private String getLocationStringFromCursor(Cursor c) {
		return String.valueOf(getHelper().getLatitude(c)) + ", " 
			+ String.valueOf(getHelper().getLongitude(c));
	}

}