
public class Request {
	private boolean type; // true -> allocation ; false -> liberation
	private int numeral;
	private static int count = 0;
	private int ID;

	public Request (String type, int numeral) {	
		
		this.numeral = numeral;
		
		if(type.toUpperCase().equals("S")) {
			this.type = true;
			count++;
			ID = count;
		}
		
		else if(type.toUpperCase().equals("L")) {
			this.type = false;
			ID = this.numeral;
		}
		
		
	}

	public boolean getType() {
		return type;
	}	

	public int getNumeral() {
		return numeral;
	}
	
	@Override
	public String toString(){
		
		String type;
		
		if(this.type)
			type = "S";
		else
			type = "L";
		
		return type + " " + numeral;
		
	}

	public int getID() {
		return ID;
	}
	
}
	
	
