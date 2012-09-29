package csci498.laberle.lunchlist;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;


public class FeedActivity extends ListActivity {

	private void goBlooey(Throwable t) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Exception!")
		.setMessage(t.toString())
		.setPositiveButton("OK", null)
		.show();
	}

	private static class FeedTask extends AsyncTask<String, Void, RSSFeed> {
		private Exception e;
		private FeedActivity activity;
		private RSSReader reader;

		FeedTask(FeedActivity activity) {
			attach(activity);
		}

		void attach(FeedActivity activity) {
			this.activity = activity;
		}
		
		void detach() {
			activity = null;
		}

		@Override
		public RSSFeed doInBackground(String... urls) {
			RSSFeed result = null;
			try {
				result = reader.load(urls[0]);
			}
			catch (Exception e) {
				this.e = e;
			}
			return result;
		}

		@Override 
		public void onPostExecute(RSSFeed feed) {
			if (e == null) {
				activity.setFeed(feed);
			}
			else {
				Log.e("LunchList", "Exception parsing feed", e);
				activity.goBlooey(e);
			}
		}
	}
}
