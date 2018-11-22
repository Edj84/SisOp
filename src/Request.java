
public class Request {
	private boolean type; // true -> allocation ; false -> liberation
	private int numeral;

	public Request (String type, int numeral) {	
		
		if(type.toUpperCase().equals("S"))
			this.type = true;
		else if(type.toUpperCase().equals("L"))
			this.type = false;
		
		this.numeral = numeral;
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
	
}
	
	
