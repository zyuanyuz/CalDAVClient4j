package us.zoom.demo.caldavclient.client;


import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;

/**
 * @author George Yu
 * @since 2019/9/25 9:59
 */
public class BaseCalDavClient extends HttpClient {
    protected HostConfiguration hostConfig = new HostConfiguration();

}
