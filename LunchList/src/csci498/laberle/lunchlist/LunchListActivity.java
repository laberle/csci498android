package csci498.laberle.lunchlist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.RadioGroup;

@SuppressWarnings("deprecation")
public class LunchListActivity extends TabActivity {

	Cursor model;
	RestaurantHelper helper;
	RestaurantAdapter adapter;
	EditText name;
	EditText address;
	RadioGroup types;
	DatePicker datePicker;
	EditText notes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		
		initializeMembers();

		configureSaveButton();
		configureRestaurantList();
		configureTabs();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		helper.close();
	}

	private void initializeMembers() {
		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.addr);
		types = (RadioGroup) findViewById(R.id.types);
		datePicker = (DatePicker) findViewById(R.id.date);
		notes = (EditText) findViewById(R.id.notes);
		
		helper = new RestaurantHelper(this);
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

	private void configureRestaurantList() {
		model = helper.getAll();
		startManagingCursor(model);
		adapter = new RestaurantAdapter(model);
		
		ListView list = (ListView) findViewById(R.id.restaurants);
		list.setAdapter(adapter);
		list.setOnItemClickListener(onListClick);
	}

	private OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
			model.moveToPosition(position);
			name.setText(helper.getName(model));
			address.setText(helper.getAddress(model));
			notes.setText(helper.getNotes(model));
			
			String date = helper.getDate(model);
			GregorianCalendar c = (GregorianCalendar) GregorianCalendar.getInstance();
			String[] dateString = date.split(" ");
			c.set(Integer.parseInt(dateString[2]), 
				Integer.parseInt(dateString[0]),
				Integer.parseInt(dateString[1]));
			datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
			
			if (helper.getType(model).equals("sit_down")) {
				types.check(R.drawable.sit_down);
			}
			else if (helper.getType(model).equals("take_out")) {
				types.check(R.drawable.take_out);
			}
			else if (helper.getType(model).equals("delivery")) {
				types.check(R.drawable.delivery);
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
	
			String typeString = getTypeFromRadioButtons(types).toString();
			String dateString =  ((Integer) datePicker.getMonth()).toString()
				+ " " + ((Integer) datePicker.getDayOfMonth()).toString()
				+ " " + ((Integer) datePicker.getYear()).toString();
			
			helper.insert(name.getText().toString(),
				address.getText().toString(),
				typeString,
				notes.getText().toString(),
				dateString);	
			
			model.requery();
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

	public class RestaurantAdapter extends CursorAdapter {
		RestaurantAdapter(Cursor c) {
			super(LunchListActivity.this, c);
		}

		@Override
		public void bindView(View row, Context ctx, Cursor c) {
			RestaurantHolder holder = (RestaurantHolder) row.getTag();
			holder.populateFrom(c, helper);
		}
		
		@Override
		public View newView(Context ctx, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);

			RestaurantHolder holder = new RestaurantHolder(row);
			row.setTag(holder);
			return row;
		}
	}

	static class RestaurantHolder {
		private TextView name = null; 
		private TextView address = null;
		private TextView date = null;
		private ImageView icon = null;

		RestaurantHolder(View row) {
			name = (TextView) row.findViewById(R.id.title);
			address = (TextView) row.findViewById(R.id.address);
			date = (TextView) row.findViewById(R.id.date_text);
			icon = (ImageView) row.findViewById(R.id.icon);
		}

		void populateFrom(Cursor c, RestaurantHelper helper) {
			name.setText(helper.getName(c));
			address.setText(helper.getAddress(c));

			//SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
			//date.setText(dateFormat.format(helper.getDate(c)));
			date.setText(helper.getDate(c));
			
			if (helper.getType(c).equals("sit_down")) {
				icon.setImageResource(R.drawable.sit_down);
			}
			else if (helper.getType(c).equals("take_out")) {
				icon.setImageResource(R.drawable.take_out);
			}
			else if (helper.getType(c).equals("delivery")) {
				icon.setImageResource(R.drawable.delivery);
			}
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
