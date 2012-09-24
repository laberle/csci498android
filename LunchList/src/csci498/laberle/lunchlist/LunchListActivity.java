package csci498.laberle.lunchlist;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class LunchListActivity extends ListActivity {

	Cursor model;
	RestaurantHelper helper;
	RestaurantAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);

		configureList();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}

	private void configureList() {
		helper = new RestaurantHelper(this);
		model = helper.getAll();
		startManagingCursor(model);
		adapter = new RestaurantAdapter(model);
		setListAdapter(adapter);
	}

	private OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
			
			Intent i = new Intent(LunchListActivity.this, DetailForm.class);
			startActivity(i);
			/*model.moveToPosition(position);
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
			
			getTabHost().setCurrentTab(Tabs.DETAILS.getIndex());*/
		}
	};

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

}
