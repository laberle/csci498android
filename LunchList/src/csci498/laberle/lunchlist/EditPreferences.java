package csci498.laberle.lunchlist;

import android.os.Bundle;
import android.preference.PreferenceActivity;

@SuppressWarnings("deprecation")
public class EditPreferences extends PreferenceActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
	}
}
