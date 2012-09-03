package csci498.laberle.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class LunchListActivity extends Activity {

	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	ArrayAdapter<Restaurant> adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);

		configureButton();
		configureRestaurantList();
		//addRadioButtons();
	}

	private void configureRestaurantList() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		adapter = new ArrayAdapter<Restaurant>(
				this, 
				android.R.layout.simple_spinner_item, 
				restaurants);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		/*ListView list = (ListView) findViewById(R.id.restaurants);
		adapter = new ArrayAdapter<Restaurant>
				(this, 
				android.R.layout.simple_list_item_1, 
				restaurants);
		list.setAdapter(adapter);*/
	}

	private void addRadioButtons() {
		RadioGroup types = (RadioGroup) findViewById(R.id.types);
		RadioButton button = new RadioButton(this);
		button.setText("Drive-Thru");
		types.addView(button);
		button = new RadioButton(this);
		button.setText("Fancy Dining");
		types.addView(button);
		button = new RadioButton(this);
		button.setText("Self-Serve");
		types.addView(button);
		button = new RadioButton(this);
		button.setText("Drive In");
		types.addView(button);
		button = new RadioButton(this);
		button.setText("Take and Bake");
		types.addView(button);
	}

	private void configureButton() {
		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(onSave);
	}

	private View.OnClickListener onSave = new OnClickListener() {
		public void onClick(View v) {
			Restaurant restaurant = new Restaurant();

			EditText name = (EditText) findViewById(R.id.name);
			EditText address = (EditText) findViewById(R.id.addr);
			RadioGroup types = (RadioGroup) findViewById(R.id.types);

			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());
			restaurant.setType(getTypeFromRadioButtons(types));

			adapter.add(restaurant);
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
}
