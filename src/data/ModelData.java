package data;

import org.json.simple.JSONObject;

public class ModelData {
	public boolean isLocked;
	public String productId;
	public float scale;
	public float x;
	public float y;
	public float z;
	public float yRot;
	
	public JSONObject toJSON() throws Exception{
		JSONObject jo = new JSONObject();
		
		jo.put("isLocked", this.isLocked);
		jo.put("productId", this.productId);
		jo.put("scale", this.scale);
		jo.put("x", this.x);
		jo.put("y", this.y);
		jo.put("z", this.z);
		jo.put("yRot", this.yRot);
		
		return jo;
	}
}
