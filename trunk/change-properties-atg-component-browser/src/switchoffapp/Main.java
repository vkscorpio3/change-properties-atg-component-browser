
package switchoffapp;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *  @author Abdul Mohsin
 *   Email :abdul82@gmail.com
 */
public class Main {

    private static StringBuffer serverBuff=new StringBuffer();
    private static StringBuffer propBuff= new StringBuffer();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        Properties propSwitchOff = new Properties();
        Properties propMaster = new Properties();
        propSwitchOff.load(new FileReader(new File("SwitchoffApp.properties")));
        propMaster.load(new FileReader(new File("MasterServers.properties")));

        //propSwitchOff.load(new FileReader(new File("src/switchoffapp/SwitchoffApp.properties")));
       // propMaster.load(new FileReader(new File("src/switchoffapp/MasterServers.properties")));
        //prop.load(new FileReader(new File("SwitchoffApp.properties")));
       // File file = new File(".");
        // System.out.println(file.getAbsoluteFile());

        // String test=(String)prop.get("20.60.66.110");
        // System.out.println("******** test:"+test);
        // System.exit(0);
        /*
         * Setting system for internet connection
         */
        StringBuffer finalBuff = new StringBuffer();

        if (propMaster.getProperty("proxyRequired").equals("true")) {
            Properties systemSettings = System.getProperties();
            systemSettings.put("http.proxyHost", propMaster.getProperty("http.proxyHost"));
            systemSettings.put("http.proxyPort", propMaster.getProperty("http.proxyPort"));
        }
        String[] servers = ((String) propSwitchOff.getProperty("servers")).split(",");
        //properties_to_change
        String[] properties = ((String) propSwitchOff.getProperty("properties_to_change")).split(",");
        
        boolean confirmation = confirmation(servers,properties);

        if(!confirmation){
            System.exit(0);
        }

        ServerThread[] threads = new ServerThread[servers.length];
        ThreadGroup group = new ThreadGroup("TG");
        for (int i = 0; i < servers.length; i++) {

            String server = servers[i];
           // System.out.println("Server :" + server);
            String[] portNpass = ((String) propMaster.get(server)).split(":");
            String portsArr = portNpass[0];
            String pass = portNpass[1];
            String[] ports = portsArr.split(",");
           threads[i] = new ServerThread(server, ports, pass, properties);
           threads[i].start();

        }
       for(ServerThread t:threads){
           t.join();
           finalBuff.append(t.getLogs());
       }

       if(finalBuff.toString().trim().length()>0){
       JOptionPane.showMessageDialog(null, finalBuff.toString());
        }
        else{
          
           JOptionPane.showMessageDialog(null, " Done on all servers :"+serverBuff.toString());
        }
    }
        private static boolean confirmation(String[] servers, String[] properties){

            //StringBuffer serverBuff= new StringBuffer();
           // StringBuffer propBuff= new StringBuffer();
            for(String server:servers){
                serverBuff.append(server.substring(server.lastIndexOf(".")+1)+",");
            }
             serverBuff=serverBuff.deleteCharAt(serverBuff.length()-1);
            for(String property:properties){
                propBuff.append(property+"\n");
            }
            System.out.println(serverBuff.toString() +"\n"+ propBuff.toString());
            
           int out= JOptionPane.showConfirmDialog(null, "Confirmation ! \n\n Changing properties:\n"+propBuff+" \n For servers:\n"+serverBuff);

           if(JOptionPane.NO_OPTION ==out || JOptionPane.CANCEL_OPTION ==out || JOptionPane.CLOSED_OPTION==out){
            return false;
           }
            else return true;

        }
    
}
