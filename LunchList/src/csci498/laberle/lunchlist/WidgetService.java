package csci498.laberle.lunchlist;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;


public class WidgetService extends IntentService {
	
	public WidgetService() {
		super("WidgetService");
	}

	@Override
	public void onHandleIntent(Intent intent) {
		ComponentName componentName = new ComponentName(this, AppWidget.class);
		RemoteViews widgetViews = new RemoteViews("csci498.laberle.lunchlist", R.layout.widget);
		RestaurantHelper helper = new RestaurantHelper(this);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);

		try {
			String name = helper.getRandomRestaurantName();
				
			if (name != null) {
				widgetViews.setTextViewText(R.id.name, c.getString(1));
				
				Intent i = new Intent(this, DetailForm.class);
				i.putExtra(LunchList.ID_EXTRA, helper.getIdFromRestaurantName(name));
				PendingIntent pendingIntent = PendingIntent.getActivity(this,
					0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				
				widgetViews.setOnClickPendingIntent(R.id.name, pendingIntent);
				
				c.close();
			}
			else {
				widgetViews.setTextViewText(R.id.title, this.getString(R.string.empty));
			}
		}
		finally {
			helper.close();
		}
		
		Intent i = new Intent(this, WidgetService.class);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
		
		widgetViews.setOnClickPendingIntent(R.id.next, pendingIntent);
		manager.updateAppWidget(componentName, widgetViews);
	}
}
