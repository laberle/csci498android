package csci498.laberle.lunchlist;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class FeedService extends IntentService {
	
	public static final String EXTRA_URL = "csci498.laberle.lunchlist.EXTRA_URL";
	
	public FeedService() {
		super("FeedService");
	}
	
	@Override
	public void onHandleIntent(Intent i) {
		RSSReader reader = new RSSReader();
		
		try {
			RSSFeed result = reader.load(i.getStringExtra(EXTRA_URL));
		} 
		catch (Exception e) {
			Log.e("LunchList", "Exception parsing feed", e);
		}
	}
	
}
