package csci498.laberle.lunchlist;

import java.util.Calendar;

public class Restaurant {

	private String address = "";
	private String name = "";
	private RestaurantType type = null;
	private String notes = "";
	private Calendar date = null;

	public String toString() {
		return getName();
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RestaurantType getType() {
		return type;
	}
	public void setType(RestaurantType type) {
		this.type = type;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}


