package csci498.laberle.lunchlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.RadioGroup;

@SuppressWarnings("deprecation")
public class LunchListActivity extends TabActivity {

	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	Restaurant current = null;
	RestaurantAdapter adapter = null;
	ArrayAdapter<String> addressAdapter = null;
	EditText name = null;
	EditText address = null;
	RadioGroup types = null;
	DatePicker datePicker = null;
	EditText notes = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);

		initializeMembers();

		configureSaveButton();
		configureRestaurantList();
		configureAddressField();
		configureTabs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.raise_toast) {
			displayRestaurantNotes();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayRestaurantNotes() {
		String message = "No restaurant selected!";
		if (current != null) {
			message = current.getNotes();
			if (current.getNotes().equals("")) {
				message = current.getName() + "has no notes!";
			}
		}
		AlertDialog alertDialog = new AlertDialog.Builder(this)
			.setTitle("Raise Toast")
			.setMessage(message)
			.create();
		alertDialog.show();
	}

	private void initializeMembers() {
		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.addr);
		types = (RadioGroup) findViewById(R.id.types);
		datePicker = (DatePicker) findViewById(R.id.date);
		notes = (EditText) findViewById(R.id.notes);
	}

	private void configureTabs() {
		TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
		spec.setContent(R.id.restaurants);
		spec.setIndicator("List", getResources()
			.getDrawable(R.drawable.list));
		getTabHost().addTab(spec);
		spec = getTabHost().newTabSpec("tag2");
		spec.setContent(R.id.details);
		spec.setIndicator("Details", getResources()
			.getDrawable(R.drawable.restaurant));
		getTabHost().addTab(spec);
		getTabHost().setCurrentTab(Tabs.LIST.getIndex());
	}

	private void configureAddressField() {
		List<String> addresses = new ArrayList<String>();
		for (Restaurant r : restaurants) {
			addresses.add(r.getAddress());
		}

		addressAdapter = new ArrayAdapter<String>(
			this,
			android.R.layout.simple_dropdown_item_1line,
			addresses);

		AutoCompleteTextView address = (AutoCompleteTextView)
			findViewById(R.id.addr);
		address.setAdapter(addressAdapter);
	}

	private void configureRestaurantList() {
		ListView list = (ListView) findViewById(R.id.restaurants);
		adapter = new RestaurantAdapter();
		list.setAdapter(adapter);
		list.setOnItemClickListener(onListClick);
	}

	private OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
			current = restaurants.get(position);
			name.setText(current.getName());
			address.setText(current.getAddress());
			notes.setText(current.getNotes());
			Calendar c = current.getDate();
			datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);

			switch (current.getType()) {
			case SIT_DOWN:
				types.check(R.id.sit_down);
				break;
			case TAKE_OUT:
				types.check(R.id.take_out);
				break;
			case DELIVERY:
				types.check(R.id.delivery);
			}
			getTabHost().setCurrentTab(Tabs.DETAILS.getIndex());
		}
	};

	private void configureSaveButton() {
		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(onSave);
	}

	private View.OnClickListener onSave = new OnClickListener() {

		public void onClick(View v) {
			current = new Restaurant();

			current.setName(name.getText().toString());
			current.setAddress(address.getText().toString());
			current.setType(getTypeFromRadioButtons(types));
			Calendar c = Calendar.getInstance();
			c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
			current.setDate(c);
			current.setNotes(notes.getText().toString());

			adapter.add(current);
			addressAdapter.add(current.getAddress());
		}
	};

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

	public class RestaurantAdapter extends ArrayAdapter<Restaurant> {
		RestaurantAdapter() {
			super(LunchListActivity.this,
				R.layout.row, 
				restaurants);
		}

		public int getViewTypeCount() {
			return RestaurantType.values().length;
		}

		public int getItemViewType(int position) {
			switch (restaurants.get(position).getType()) {
			case SIT_DOWN:
				return RestaurantType.SIT_DOWN.getIndex();
			case TAKE_OUT:
				return RestaurantType.TAKE_OUT.getIndex();
			case DELIVERY:
				return RestaurantType.DELIVERY.getIndex();
			default:
				return -1;
			}
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return initializeRow(position, parent, convertView);
		}

		private View initializeRow(int position, ViewGroup parent, View row) {
			RestaurantHolder holder;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();

				switch (restaurants.get(position).getType()) {
				case SIT_DOWN:
					row = inflater.inflate(R.layout.row_sit_down, parent, false);
					break;
				case TAKE_OUT:
					row = inflater.inflate(R.layout.row_take_out, parent, false);
					break;
				case DELIVERY:
					row = inflater.inflate(R.layout.row_delivery, parent, false);
					break;
				}

				holder = new RestaurantHolder(row);
				row.setTag(holder);
			}
			else {
				holder = (RestaurantHolder) row.getTag();
			}

			holder.populateFrom(restaurants.get(position));
			return row;
		}

	}

	static class RestaurantHolder {
		private TextView name = null; 
		private TextView address = null;
		private TextView date = null;

		RestaurantHolder(View row) {
			name = (TextView) row.findViewById(R.id.title);
			address = (TextView) row.findViewById(R.id.address);
			date = (TextView) row.findViewById(R.id.date_text);
		}

		void populateFrom(Restaurant rest) {
			name.setText(rest.getName());
			address.setText(rest.getAddress());

			SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
			date.setText(dateFormat.format(rest.getDate().getTime()));
		}

	}

	private enum Tabs {
		LIST(0),
		DETAILS(1);

		private int index;
		Tabs(int index) {
			this.index = index;
		}
		public int getIndex() {
			return index;
		}
	}

}
