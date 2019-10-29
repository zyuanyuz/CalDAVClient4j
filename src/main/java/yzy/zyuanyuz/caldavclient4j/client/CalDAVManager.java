package yzy.zyuanyuz.caldavclient4j.client;

import com.github.caldav4j.CalDAVCollection;
import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpPropFindMethod;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.Namespace;

/**
 * @author George Yu
 * @since 2019/9/26 9:20
 */
public class CalDAVManager extends CalDAVCollection {

  protected HttpClient httpClient = null;

  public CalDAVManager() {
    HttpHost target = new HttpHost("localhost", 5232, "http");
    CredentialsProvider provider = new BasicCredentialsProvider();
    provider.setCredentials(
            new AuthScope(target.getHostName(), target.getPort()),
            new UsernamePasswordCredentials("root", "root"));
    httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).build();
    setMethodFactory(new CalDAV4JMethodFactory());
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void testSubscribe() throws Exception {
    DavPropertyNameSet nameSet = new DavPropertyNameSet();
    nameSet.add(
            DavPropertyName.create(
                    "xmpp-server", Namespace.getNamespace("http://calendarserver.org/ns/")));
    nameSet.add(
            DavPropertyName.create(
                    "pushkey", Namespace.getNamespace("http://calendarserver.org/ns/")));
    nameSet.add(
            DavPropertyName.create(
                    "xmpp-heartbeat-uri", Namespace.getNamespace("http://calendarserver.org/ns/")));
    nameSet.add(DavPropertyName.create("sync-token"));
    nameSet.add(
            DavPropertyName.create(
                    "calendar-timezone", Namespace.getNamespace("urn:ietf:params:xml:ns:caldav")));
    HttpPropFindMethod propFindMethod =
            methodFactory.createPropFindMethod(
                    "http://localhost:5232/root/68ab2a13-ee99-31d8-5baa-4594545fcd36/",
                    nameSet,
                    CalDAVConstants.DEPTH_0);
    HttpResponse response = httpClient.execute(propFindMethod);

    System.out.println(EntityUtils.toString(response.getEntity()));
  }
}
