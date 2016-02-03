package data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class DesignData {
	public String cameraXFOV;
	public String cameraYFOV;
	public String[] cube = new String[12];
	public String globalScale;
	public float[] imageSize = new float[2];
	public String localUniqueID;
	public float[] matrix = new float[16];
	public List<ModelData> models = new ArrayList<ModelData>();
	public float orientation;
	public String sceneName;
	public String timeStamp;
	public String version;
	
	private float getFloat(String line){
		return Float.parseFloat(line.split("=")[1].replace(";","").replace("\"", "").trim());
	}
	private String getString (String value){
		return value.replaceAll("\"", "").replace(";", "").trim();
	}
	
	public JSONObject toJSON() throws Exception{
		JSONObject jo = new JSONObject();
		
		jo.put("cameraXFOV", this.cameraXFOV);
		jo.put("cameraYFOV", this.cameraYFOV);
		
		JSONArray cubeArr = new JSONArray();
		cubeArr.put(new JSONObject().put("x",this.cube[0]).put("y", this.cube[1]).put("z", this.cube[2]));
		cubeArr.put(new JSONObject().put("x",this.cube[3]).put("y", this.cube[4]).put("z", this.cube[5]));
		cubeArr.put(new JSONObject().put("x",this.cube[6]).put("y", this.cube[7]).put("z", this.cube[8]));
		cubeArr.put(new JSONObject().put("x",this.cube[9]).put("y", this.cube[10]).put("z", this.cube[11]));
		jo.put("cube", cubeArr);
		
		jo.put("globalScale", this.globalScale);
		
		jo.put("imageSize", new JSONObject().put("Height", this.imageSize[0]).put("Width",this.imageSize[1]));
		
		jo.put("localUniqueID", this.localUniqueID);
		
		jo.put("matrix",this.matrix);
		
		JSONArray models = new JSONArray();
		for(ModelData model:this.models){
			models.put(model.toJSON());
		}
		jo.put("models", models);
		
		jo.put("version",this.version);
		
		return jo;
	}
	public DesignData(List<String> initData){
		for(int i=0; i<initData.size(); i++){
			String line = initData.get(i);
			
			if(line.indexOf("=")==-1)
				continue;
			
			String key = this.getString(line.split("=")[0]);
			String value = this.getString(line.split("=")[1]);
			
			if("cameraYFOV".equals(key)){
        		this.cameraYFOV = value;
        	}
			
			if("cameraXFOV".equals(key)){
				this.cameraXFOV = value;
			}
			
			if(line.indexOf("cube")!=-1){
				int index = 0;
        		while(true){
        			i++;
                    line = initData.get(i);
                    
                    if(line.indexOf(");")!=-1)
                    	break;
                    
                    if(line.indexOf("=")==-1)
                    	continue;
                    
                    String cKey = this.getString(line.split("=")[0]);
                    String cValue = this.getString(line.split("=")[1]);
                    this.cube[index] = cValue;
                    index ++;
            	}
			}
			
			if(line.indexOf("globalScale")!=-1)
				this.globalScale = this.getString(value);
			
			if(line.indexOf("imageSize")!=-1){
				int index = 0;
        		while(true){
        			i++;
                    line = initData.get(i);
                    
                    if(line.indexOf("};")!=-1)
                    	break;
                    
                    this.imageSize[index] = this.getFloat(line);
                    index ++;
            	}
			}
			
			if(line.indexOf("localUniqueID")!=-1){
				this.localUniqueID = line.split("=")[1].replace("\"", "").replace(";","").trim();
			}
			
			if(line.indexOf("matrix")!=-1){
				int index = 0;
        		while(true){
        			i++;
                    line = initData.get(i);
                    
                    if(line.indexOf(");")!=-1)
                    	break;
                    
                    this.matrix[index] = Float.parseFloat(line.replaceAll("\"", "").replaceAll(",", "").trim());
                    index ++;
            	}
			}
			
			if(line.indexOf("models")!=-1){
        		while(true){
        			i++;
                    line = initData.get(i);
                    
                    
                    if(line.indexOf(");")!=-1)
                    	break;
                    
                    ModelData md = new ModelData();
                    while(true){
                    	i++;
                        line = initData.get(i);
                        if(line.indexOf("}")!=-1)
                        	break;
                        if(line.indexOf("=")==-1)
                        	continue;
                        
                        String mKey = this.getString(line.split("=")[0]);
                        String mValue = this.getString(line.split("=")[1]);
                        
                        if("isLocked".equals(mKey))
                        	md.isLocked = mValue.indexOf("0")==-1?true:false;
                        if("productId".equals(mKey))
                        	md.productId = mValue.replaceAll("\"","").replace(";","").trim();
                        if("scale".equals(mKey))
                        	md.scale = this.getFloat(line);
                        if("x".equals(mKey))
                        	md.x = this.getFloat(line);
                        if("y".equals(mKey))
                        	md.y = this.getFloat(line);
                        if("z".equals(mKey))
                        	md.z = this.getFloat(line);
                        if("yRot".equals(mKey))
                        	md.yRot = this.getFloat(line);
                    }
                    this.models.add(md);
            	}
				
			}
			if(line.indexOf("version")!=-1){
        		this.version = this.getString(value);
        	}
		
		}
	}
}
