package csci498.laberle.lunchlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DetailForm extends Activity {
	
	RestaurantHelper helper;
	EditText name;
	EditText address;
	RadioGroup types;
	DatePicker datePicker;
	EditText notes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);
		
		initializeMembers();
		configureSaveButton();
	}
	
	private void initializeMembers() {
		name = (EditText) findViewById(R.id.name);
		address = (EditText) findViewById(R.id.addr);
		types = (RadioGroup) findViewById(R.id.types);
		datePicker = (DatePicker) findViewById(R.id.date);
		notes = (EditText) findViewById(R.id.notes);
		
		helper = new RestaurantHelper(this);
	}
	
	private void configureSaveButton() {
		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(onSave);
	}

	private View.OnClickListener onSave = new OnClickListener() {

		public void onClick(View v) {
	
			RestaurantType type = getTypeFromRadioButtons(types);
			//TODO: Sanitize database input
			/*String dateString =  ((Integer) datePicker.getMonth()).toString()
				+ " " + ((Integer) datePicker.getDayOfMonth()).toString()
				+ " " + ((Integer) datePicker.getYear()).toString();
			
			helper.insert(name.getText().toString(),
				address.getText().toString(),
				getTypeFromRadioButtons(types).getIndex() + 1, //_id offset from getIndex() by 1
				notes.getText().toString(),
				dateString);	
			
			model.requery();*/
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
