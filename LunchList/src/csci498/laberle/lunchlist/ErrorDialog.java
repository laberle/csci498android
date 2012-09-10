package csci498.laberle.lunchlist;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;


public class ErrorDialog extends AlertDialog {
	
	private AlertDialog.Builder builder = null;
	private String message = "Unspecified Error!";
	private String tag;
	private static final String TITLE = "Error!";

	protected ErrorDialog(Context context) {
		super(context);
		builder = new AlertDialog.Builder(context)
			.setTitle(TITLE);
		tag = context.getClass().toString();
	}
	
	public void showDialogAndLogError() {
		Log.e(tag, message);
		builder.setMessage(message);
		builder.create().show();
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
