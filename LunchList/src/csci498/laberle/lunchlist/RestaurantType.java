package csci498.laberle.lunchlist;

public enum RestaurantType {
	
	SIT_DOWN(0, "sit_down"),
	TAKE_OUT(1, "take_out"),
	DELIVERY(2, "delivery");
	
	private int index;
	private String name;
	
	RestaurantType(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
