package yzy.zyuanyuz.caldavclient4j.controller;

import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalDAVManager;
import yzy.zyuanyuz.caldavclient4j.client.util.ICloudCalDAVUtil;

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
        ICloudCalDAVUtil.getAllResourceFromServer(
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
    ICloudCalDAVUtil.getEventUidList(
        "28C804FE-B39D-4CFD-B09D-0EAA4DB4E7BB",
        iCloudCalDAVManager.getHttpClient(),
        iCloudCalDAVManager.getMethodFactory());
  }
}
