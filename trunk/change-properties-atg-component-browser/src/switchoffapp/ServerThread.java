/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package switchoffapp;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author amohsin
 */
public class ServerThread extends Thread {

    private String server;
    private String[] ports;
    private String pass;
    private String[] properties;
    private StringBuffer logs;

    public StringBuffer getLogs(){
        return logs;
    }

    ServerThread(String server, String[] ports, String pass, String[] properties) {
        this.server = server;
        this.ports = ports;
        this.pass = pass;
        this.properties = properties;

    }

    public void run() {
        //    throw new UnsupportedOperationException("Not supported yet.");
        
         logs= new StringBuffer();
        try {
           // System.out.println("Server :" + server);


            /*
             * Default Http client
             */
            DefaultHttpClient httpclient = new DefaultHttpClient();


            for (int j = 0; j < ports.length; j++) {
                int port = Integer.parseInt(ports[j]);
                System.out.println(" ***** Server:"+server+"  Port : " + port);
                /*
                 *   setting login for the component browser
                 */
                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(server, port),
                        new UsernamePasswordCredentials("admin", pass));
                for (int k = 0; k < properties.length; k++) {


                    String[] propValues = properties[k].split(":");
                    String propBaseURL = propValues[0];
                    if (!propBaseURL.endsWith("/")) {
                        propBaseURL = propBaseURL + "/";
                    }
                    String propertyName = propValues[1];
                    String propNewValue = propValues[2];

                    //ethod GetMethod

                    // HttpGet httpget = new HttpGet(propBaseURL);
                    // HttpResponse response = httpclient.execute(new HttpHost(server, port), httpget);

                    // HttpEntity entity = response.getEntity();

                    // System.out.println("Login form get: " + response.getStatusLine());

                    // httpget.abort();


                    // HttpEntity entity =response.getEntity();
                    // System.out.println(EntityUtils.toString(entity));



                    HttpPost httpost = new HttpPost("http://" + server + ":" + port + propBaseURL + "?propertyName=" + propertyName);

                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    nvps.add(new BasicNameValuePair("propertyName", propertyName));
                    nvps.add(new BasicNameValuePair("newValue", propNewValue));
                    nvps.add(new BasicNameValuePair("change", "Change Value"));
                    //http://strat-track.intranet.point/TrackingService.asmx/TrackingEnquiry
                    httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

                    try{
                    HttpResponse response = httpclient.execute(httpost);
                    HttpEntity entity = response.getEntity();

                  //  System.out.println("Login form get: " + response.getStatusLine());
                            //+"  entity:"+EntityUtils.toString(entity));
                    // if (entity != null) {
                    //entity.consumeContent();
                    // Short Description}
                    if(EntityUtils.toString(entity).indexOf("Short Description") > -1){
                    System.out.println(" ** Done for server:" + server + " port:" + port + " BaseURL:" + propBaseURL + " Property :"+propertyName+" New value:" + propNewValue);
                       // logs.append(" \n ** Done for server:" + server + " \n\t\tport:" + port + " \n\t\tBaseURL:" + propBaseURL + " \n\t\tProperty :"+propertyName+" \n\t\tNew value:" + propNewValue);
                        // logs.append(" \n ** Done for server:" + server);
                        }
                    else{
                        System.out.println(" #### NOT DONE for server:" + server + " port:" + port + " BaseURL:" + propBaseURL + " Property:"+propertyName+" New value:" + propNewValue +" Reason: Property Not Found:"+propertyName);
                        //logs.append("\n #### NOT DONE for server:" + server + " \n\t\tport:" + port + " \n\t\tBaseURL:" + propBaseURL + " \n\t\tProperty:"+propertyName+" \n\t\tNew value:" + propNewValue +" \n\t\tReason: Property Not Found:"+propertyName);
                        logs.append("\n #### NOT DONE for server:" + server+" port:" + port +" , Reason:Property Not Found: "+propertyName);
                    }

                    // When HttpClient instance is no longer needed,
                    // shut down the connection manager to ensure
                    // immediate deallocation of all system resources
                    }
                    catch(Exception ex){
                        System.out.println(" #### NOT DONE for server:" + server + " port:" + port + " BaseURL:" + propBaseURL + " Property :"+ propertyName+" New value:" + propNewValue +" Reason:"+ex.getMessage());
                        //logs.append(" \n#### NOT DONE for server:" + server + " \n\t\tport:" + port + " \n\t\tBaseURL:" + propBaseURL + " \n\t\tProperty :"+ propertyName+" \n\t\tNew value:" + propNewValue +" \n\t\tReason:"+ex.getMessage());
                        logs.append(" \n#### NOT DONE for server:" + server+" port:" + port +" , Reason:"+ex.getMessage());
                    }
                    httpost.abort();
                }
                //httpclient.getConnectionManager().shutdown();
            }
            httpclient.getConnectionManager().shutdown();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occured:"+ex.getMessage());
        }
    }
}
