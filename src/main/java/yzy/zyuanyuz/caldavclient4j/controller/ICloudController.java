package yzy.zyuanyuz.caldavclient4j.controller;

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
    String uuid = "EFE46473-85FB-4CD6-BF4B-A383B1F8EBBF";
    iCloudCalDAVManager.refreshAllEvents();
    // iCloudCalDAVManager.refreshEvent(uuid);
    return iCloudCalDAVManager.getETagFromServer(uuid);
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
    iCloudCalDAVManager.getEventsForThreeDays();
  }

  @GetMapping("/refresh")
  public void refreshEvents() throws Exception {
    ICloudCalDAVUtil.getEventUidList(
        "28C804FE-B39D-4CFD-B09D-0EAA4DB4E7BB",
        iCloudCalDAVManager.getHttpClient(),
        iCloudCalDAVManager.getMethodFactory());
  }
}
