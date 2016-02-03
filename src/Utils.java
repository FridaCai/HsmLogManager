

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/



import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.httpclient.NameValuePair;
import com.autodesk.dragonfly.framework.cache.MCClient;
import com.autodesk.dragonfly.framework.util.Logger;
import com.autodesk.framework.util.HTTPConnectionUtil;
import org.apache.commons.httpclient.HttpClient;
import com.autodesk.dragonfly.framework.util.StringUtil;
import com.autodesk.dragonfly.struts.api.APIErrorCodes;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import com.autodesk.dragonfly.framework.util.PropertiesManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
/**
*
* @author huangda
*/
public class Utils {

    private static BasicDataSource hsmDataSource;

    private final static String DB_HSM_ALPHA = "jdbc:mysql://hsmob-dev-mysql-1.cbdlnsoovrgv.us-east-1.rds.amazonaws.com:3306/hsmdb?useUnicode=true&characterEncoding=UTF-8";
    private final static String DBUSER_HSM_ALPHA = "hsmroot";
    private final static String DBPW_HSM_ALPHA = "hsm123**";

    private static String DB = DB_HSM_ALPHA;
    private static String DBUSER = DBUSER_HSM_ALPHA;
    private static String DBPW = DBPW_HSM_ALPHA;
   
    public static List<Map<String, Object>> runQuery(String queryString, Object[] param){
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        QueryRunner mobileRun = new QueryRunner(getHSMDataSource(DB, DBUSER, DBPW));
        try{
            result = mobileRun.query(queryString, new MapListHandler(), param);
            
       }catch(Exception e){
            System.err.println(e.getMessage());
       }finally{
            return result;
       }
    }

    public static void saveStrToFile(String txt, String filePath) throws Exception{
        File designfile = new File(filePath);
        if(designfile.exists())
            designfile.delete();

        FileWriter fw = new FileWriter(designfile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(txt);
        bw.close();
    }
    
    private static DataSource getHSMDataSource(String url, String user, String pw){
        if(hsmDataSource != null){
           return hsmDataSource;
        }
        hsmDataSource = new BasicDataSource();
        hsmDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        hsmDataSource.setUsername(user);


        //qa
        hsmDataSource.setUrl(url);
        hsmDataSource.setPassword(pw);

        /***
         * The maximum number of connections that can remain idle in the pool,
         * without extra ones being released, or negative for no limit.
         **/
        hsmDataSource.setMaxIdle(1);

        /**
         * The minimum number of active connections that can remain idle in the
         * pool, without extra ones being created, or 0 to create none.
         **/
        hsmDataSource.setMinIdle(1);

        /**
         * The SQL query that will be used to validate connections from this
         * pool before returning them to the caller. If specified, this query
         * <strong>MUST</strong> be an SQL SELECT statement that returns at
         * least one row.
         */
        //ds.setValidationQuery(validationQuery);

        /**
         * The maximum number of milliseconds that the pool will wait (when
         * there are no available connections) for a connection to be returned
         * before throwing an exception, or -1 to wait indefinitely.
         **/
        hsmDataSource.setMaxWait(1);

        /**
         *Sets the {@link #testOnBorrow} property. This property determines
         * whether or not the pool will validate objects before they are
         * borrowed from the pool. For a <code>true</code> value to have any
         * effect, the <code>validationQuery</code> property must be set to a
         * non-null string.
         **/
        hsmDataSource.setTestOnBorrow(true);

        /**
         *<p>
         * Sets whether to pool statements or not.
         * </p>
         *<p>
         * Note: this method currently has no effect once the pool has been
         * initialized. The pool is initialized the first time one of the
         * following methods is invoked: <code>getConnection, setLogwriter,
         * setLoginTimeout, getLoginTimeout, getLogWriter.</code>
         * </p>
         **/
        hsmDataSource.setPoolPreparedStatements(true);

        /**
         * The initial number of connections that are created when the pool is
         * started.
         */
        hsmDataSource.setInitialSize(1);

        /**
         * The max active connections number.
         */
        hsmDataSource.setMaxActive(1);

        return hsmDataSource;

    }

}