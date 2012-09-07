package csci498.laberle.lunchlist;

public enum RestaurantType {
	
	SIT_DOWN(0),
	TAKE_OUT(1),
	DELIVERY(2);
	
	private int index;
	
	RestaurantType(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
