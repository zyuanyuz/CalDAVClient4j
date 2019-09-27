package yzy.zyuanyuz.caldavclient4j.controller;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Uid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import yzy.zyuanyuz.caldavclient4j.client.CalDAVManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author George Yu
 * @since 2019/9/25 9:32
 */
@RestController
public class ClientController {
  @GetMapping("/cal")
  public String getCalendar() throws Exception {
    CalDAVManager calDAVManager = new CalDAVManager();
    Calendar cal =
        calDAVManager.getCalendar(
            calDAVManager.getHttpClient(), "/root/68ab2a13-ee99-31d8-5baa-4594545fcd36/");

    return cal.toString();
  }

  @GetMapping("/add")
  public String putEvent() throws Exception {
    CalDAVManager calDAVManager = new CalDAVManager();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    Date start = dateFormat.parse("2019-10-22-18-00-00");
    Date end = dateFormat.parse("2019-10-22-20-00-00");
    VEvent event =
        new VEvent(
            new net.fortuna.ical4j.model.DateTime(start),
            new net.fortuna.ical4j.model.DateTime(end),
            "new day event!");
    Uid uid = new Uid("20190926T145226-1043a8e3-76b2-48a3-8a4a-e1586a6bbe7e-root");
    event.getProperties().add(uid);
    calDAVManager.setCalendarCollectionRoot("/root/68ab2a13-ee99-31d8-5baa-4594545fcd36/");

    calDAVManager.add(calDAVManager.getHttpClient(), event, null);
    Calendar cal = calDAVManager.getCalendar(calDAVManager.getHttpClient(), "/");
    System.out.println("add a event : " + cal.toString());

    //    calDAVManager.delete(calDAVManager.getHttpClient(), "VEVENT", uid.getValue());
    //    cal = calDAVManager.getCalendar(calDAVManager.getHttpClient(), "/");
    //    System.out.println("delete an exist event : " + cal.toString());

    return cal.toString();
  }

  @GetMapping("/update")
  public String getResources() throws Exception {
    CalDAVManager calDAVManager = new CalDAVManager();
    calDAVManager.setCalendarCollectionRoot("/root/68ab2a13-ee99-31d8-5baa-4594545fcd36/");
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    Date start = dateFormat.parse("2019-10-22-18-00-00");
    Date end = dateFormat.parse("2019-10-22-20-00-00");
    VEvent event =
        new VEvent(
            new net.fortuna.ical4j.model.DateTime(start),
            new net.fortuna.ical4j.model.DateTime(end),
            "update day info!");
    Uid uid = new Uid("20190926T145226-1043a8e3-76b2-48a3-8a4a-e1586a6bbe7e-root");
    event.getProperties().add(uid);

    calDAVManager.updateMasterEvent(calDAVManager.getHttpClient(), event, null);

    return "Update OK";
  }


}
