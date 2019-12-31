package yzy.zyuanyuz.caldavclient4j.controller;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.CalendarData;
import com.github.caldav4j.util.XMLUtils;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yzy.zyuanyuz.caldavclient4j.client.extensions.model.request.Limit;
import yzy.zyuanyuz.caldavclient4j.client.extensions.model.request.SyncCollection;
import yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalDAVManager;
import yzy.zyuanyuz.caldavclient4j.client.util.ICloudCalendarUtil;

import static yzy.zyuanyuz.caldavclient4j.client.extensions.model.request.SyncCollection.PARAM_SYNC_LEVEL_ONE;

/**
 * @author George Yu
 * @since 2019/10/29 14:56
 */
@RestController
@RequestMapping("/icloud")
public class ICloudController {
  @Autowired ICloudCalDAVManager iCloudCalDAVManager;

  @GetMapping("/get")
  public String getEvent() throws Exception {
    String eventUid = "E5DD0E30-A17A-45DA-A3ED-B1703C461378";
    VEvent event = iCloudCalDAVManager.getEventFromServer(eventUid);
    return event.toString();
  }

  @GetMapping("/resource")
  public void getResource() throws Exception {
    System.out.println(
        ICloudCalendarUtil.getAllResourceFromServer(
            iCloudCalDAVManager.getHttpClient(),
            iCloudCalDAVManager.getMethodFactory(),
            iCloudCalDAVManager.getPrincipal()));
  }

  @GetMapping("/three")
  public void getThreeDaysEvents() throws Exception {
    System.out.println(iCloudCalDAVManager.getEventsForThreeDays());
  }

  @GetMapping("/refresh")
  public void refreshEvents() throws Exception {

  }

  @GetMapping("/sync")
  public void getSyncCollectionRequest() throws Exception {
    DavPropertyNameSet davPropertySet = new DavPropertyNameSet();
    davPropertySet.add(DavPropertyName.GETETAG);
    SyncCollection syncCollection =
        new SyncCollection(
            davPropertySet,"FT=-@RU=cbff5e64-052c-4025-a68d-e9b07e0a2efe@S=79", PARAM_SYNC_LEVEL_ONE,null, new CalendarData());
    System.out.println(XMLUtils.prettyPrint(syncCollection));
    HttpCalDAVReportMethod reportMethod =
        iCloudCalDAVManager
            .getMethodFactory()
            .createCalDAVReportMethod(
                iCloudCalDAVManager.getCalFolderPath(), syncCollection, CalDAVConstants.DEPTH_0);
    HttpResponse response = iCloudCalDAVManager.getHttpClient().execute(reportMethod);
    System.out.println(EntityUtils.toString(response.getEntity()));
  }
}
