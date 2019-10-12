package yzy.zyuanyuz.caldavclient4j.controller;

import net.fortuna.ical4j.model.Calendar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yzy.zyuanyuz.caldavclient4j.client.CalDavAppleManager;

/**
 * @author George Yu
 * @since 2019/9/27 14:23
 */
@RestController
@RequestMapping("/apple")
public class AppleCalendarController {

  @GetMapping("/cal")
  public String getAppleCal() throws Exception {
    CalDavAppleManager calDavAppleManager = new CalDavAppleManager();
    return calDavAppleManager.getiCloudCalendar().toString();
  }

  @GetMapping("/getReport")
  public String getReport() throws Exception {
    CalDavAppleManager calDavAppleManager = new CalDavAppleManager();
    return calDavAppleManager.getReportWithAuth().toString();
  }

  @GetMapping("/addEvent")
  public void addEvent() throws Exception {
    CalDavAppleManager calDavAppleManager = new CalDavAppleManager();
    calDavAppleManager.addEvent();
  }

  @GetMapping("/getETag")
  public String getETag() throws Exception {
    CalDavAppleManager calDavappleManager = new CalDavAppleManager();
    return calDavappleManager.getETag();
  }

  @GetMapping("/test")
  public String getTest() throws Exception{
    CalDavAppleManager calDavAppleManager = new CalDavAppleManager();
    calDavAppleManager.testSub();
    return "asd";
  }
}
