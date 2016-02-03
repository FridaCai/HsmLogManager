/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import data.DesignData;

/**
 *
 * @author caiw
 * all assets needed:
 * create designGUID file folder manually and put raw-log.txt in it.
 * png screen shot as background image. get it manually.
 *
 *
 * design.txt. get data from db.
 * camera.txt. get data from log analyse.
 * cube.txt. get data from log analyse.
 * objs. get data from log anaylyse.
 */
public class AssetsManager {
    private String designGUID = "";
    
    private String root = "/Library/WebServer/FridaStation/threejs/hsmexplore/content";

    private String cubeKey = "frida test cube1: ";
    private StringBuilder cubeText = new StringBuilder();
    private String cubePath = root + "/" + designGUID + "/cube.txt" ;

    private String cameraKey = "frida test: print camera matrix: ";
    private StringBuilder cameraText = new StringBuilder();
    private String cameraPath = root + "/" + designGUID + "/camera.txt" ;

    private String objKey = "obj file of model";
    private StringBuilder objText = new StringBuilder();
    private Map<String, String> objs = new HashMap<String, String>();
    private String objPath = root + "/" + designGUID + "/obj" ;
    
    private DesignData dd;
    private String designPath = root + "/" + designGUID + "/design.txt" ;

    public static void main(String[] args){
    	if(args.length!=1){
    		System.out.println("Please enter id. For example, \"java -jar AssetsManager.jar simple_6ff53411-6513-40d6-b06e-98da825e3c7e\"");
    		return;
    	}
    		
    	String id = args[0];
        AssetsManager am = new AssetsManager(id);
        try{
            am.execute();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        
    }

    private boolean isTimeStamp(String toTest){
    	Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(.*)Homestyler(.*)");
    	Matcher matcher = pattern.matcher(toTest);
    	if(matcher.find()){
    		return true;
    	}
    	return false;
    }
    public void execute() throws Exception{
        doCheck();
        prepareDirectory();
        analyseLog();
    }
    

    public AssetsManager(String id){
        this.designGUID = id;
        this.cubePath = root + "/" + designGUID + "/cube.txt" ;
        this.cameraPath = root + "/" + designGUID + "/camera.txt" ;
        this.objPath = root + "/" + designGUID + "/obj" ;
        this.designPath = root + "/" + designGUID + "/design.txt" ;
           
    }

    public void doCheck() throws Exception{
        File rf = new File(root+"/"+ designGUID );
        if(!rf.exists()){
            throw new Exception("please create root folder");
        }

        File bgf = new File(root + "/" + designGUID + "/screenshot.png");
        if(!bgf.exists()){
            throw new Exception("please generate screenshot.png manually.");
        }

        File logf = new File(root + "/" + designGUID + "/raw-log.txt");
        if(!logf.exists()){
            throw new Exception("please generate raw-log.txt manually.");
        }
        
        //todo: if obj folder exist, delete it.
    }
    
    private void prepareDirectory() throws Exception{
        //new a directory for objs.
        File directory = new File(root+"/"+designGUID + "/" + "obj");
        if(directory.exists())
            directory.delete();

        if (directory.mkdir()) {
          System.out.println("Success mkdir");
        } else {
          if (directory.mkdirs()) {
            System.out.println("Success mkdirs");
          } else {
            throw new Exception("Create obj folder fail!");
          }
        }
    }
    
    private void goThroughLog() throws Exception{
        Scanner scanner = null;
        String log = root + "/" + designGUID + "/raw-log.txt";
        
        List<String> designDataList = new ArrayList<String>();

        scanner = new Scanner(new FileInputStream(log), "UTF-8");

        int cameraFormatIndex =0;
        while (scanner.hasNextLine()){
            String line = scanner.nextLine().trim();

            //cube data.
            int cubeIndex = line.indexOf(cubeKey);
            if(cubeIndex != -1){
                cubeText.append(line.substring(cubeIndex)).append("\n");
            }

            //camera data.
            int cameraIndex = line.indexOf(cameraKey);
            if(cameraIndex != -1){
                String[] lineEles = line.split(" ");
                cameraText.append(lineEles[lineEles.length-1]);
                cameraText.append("\t");
                cameraFormatIndex++;
                if(cameraFormatIndex%4==0)
                    cameraText.append("\n");
                if(cameraFormatIndex%16==0)
                    cameraText.append("\n\n");
            }

            

            //obj model.
            int objIndex = line.indexOf(objKey);
            if(objIndex != -1){
                String[] lineEles = line.split(" ");
                String modelId = lineEles[lineEles.length - 1];
                String ts = lineEles[0];

                line = scanner.nextLine().trim();

                objText = new StringBuilder();
                while(scanner.hasNext()){
                    line = scanner.nextLine().trim();

                    if(line.indexOf(ts) != -1)
                        break;

                    objText.append(line).append("\n");
                }
                objs.put(modelId, objText.toString());
            }
            
            if(line.indexOf("j:{")!=-1){
            	while(scanner.hasNext()){
                    line = scanner.nextLine().trim();
                    designDataList.add(line);
                    
                    if(this.isTimeStamp(line))
                        break;
	            }
	        }
        }
        if(scanner!=null){
            scanner.close();
        }
   
        
        dd = new DesignData(designDataList);
    }

    private void saveToFile() throws Exception{
        Utils.saveStrToFile(this.cubeText.toString(), this.cubePath);
        Utils.saveStrToFile(this.cameraText.toString(), this.cameraPath);
        
        Iterator it = this.objs.entrySet().iterator();
        while(it.hasNext()){
            Entry<String,String> entry = (Entry)it.next();
            Utils.saveStrToFile(entry.getValue(), this.objPath + "/" + entry.getKey()+".obj");
        }
        
        
        Utils.saveStrToFile(this.dd.toJSON().toString(), this.designPath);
    }
    
    private void analyseLog() throws Exception{
        goThroughLog();
        saveToFile();
    }
}
