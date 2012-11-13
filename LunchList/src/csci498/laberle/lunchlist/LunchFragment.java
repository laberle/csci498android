package csci498.laberle.lunchlist;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LunchFragment extends ListFragment {

	public final static String ID_EXTRA = "csci498.laberle.lunchlist._ID";
	Cursor model;
	RestaurantHelper helper;
	RestaurantAdapter adapter;
	SharedPreferences prefs;
	OnRestaurantListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		configurePreferences();
		configureList();
	}

	@Override
	public void onPause() {
		helper.close();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.close();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.option, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add) {
			startActivity(new Intent(getActivity(), DetailForm.class));
			return true;
		}
		else if (item.getItemId() == R.id.prefs) {
			startActivity(new Intent(getActivity(), EditPreferences.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void configurePreferences() {
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		prefs.registerOnSharedPreferenceChangeListener(prefListener);
	}

	private SharedPreferences.OnSharedPreferenceChangeListener prefListener =
		new SharedPreferences.OnSharedPreferenceChangeListener() {

		public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
			if (key.equals("sort_order")) {
				initList();
			}
		}

	};

	private void configureList() {
		helper = new RestaurantHelper(getActivity());
		initList();
	}

	private void initList() {
		if (model != null) {
			model.close();
		}
		model = helper.getAll(prefs.getString("sort_order", "name"));
		adapter = new RestaurantAdapter(model);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView list, View view, 
		int position, long id) {

		if (listener != null) {
			listener.onRestaurantSelected(id);
		}
	}

	public interface OnRestaurantListener {
		void onRestaurantSelected(long id);
	}

	public void setOnRestaurantListener(OnRestaurantListener listener) {
		this.listener = listener;
	}

	@SuppressWarnings("deprecation")
	public class RestaurantAdapter extends CursorAdapter {
		RestaurantAdapter(Cursor c) {
			super(getActivity(), c);
		}

		@Override
		public void bindView(View row, Context ctx, Cursor c) {
			RestaurantHolder holder = (RestaurantHolder) row.getTag();
			holder.populateFrom(c, helper);
		}

		@Override
		public View newView(Context ctx, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);

			RestaurantHolder holder = new RestaurantHolder(row);
			row.setTag(holder);
			return row;
		}

	}

	static class RestaurantHolder {

		private TextView name; 
		private TextView address;
		private TextView date;
		private ImageView icon;

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
