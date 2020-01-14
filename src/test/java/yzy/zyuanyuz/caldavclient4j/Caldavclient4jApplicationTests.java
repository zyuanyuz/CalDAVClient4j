package yzy.zyuanyuz.caldavclient4j;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Uid;
import org.junit.Before;
import org.junit.Test;
import yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalDAVManager;
import yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Caldavclient4jApplicationTests {

  public static final String appleId = "zyuanyuz@icloud.com";

  private static final String appPass = "ieix-obgf-gxmi-oiws";

  ICloudCalendar calendar = null;

  @Before
  public void initCalendar() throws Exception {
    this.calendar = ICloudCalendar.builder().setAppleIdAndPwd(appleId, appPass).build();
  }

  @Test
  public void testSyncToken1() throws Exception {
    DateTime startTime = new DateTime();
    DateTime endTime = new DateTime(startTime.getTime() + 3 * 24 * 60 * 60 * 1000);
    ICloudCalendar.IEvent event =
        calendar
            .iEvent()
            .list("work")
            .setSingleEvent(true)
            .setSingleEventStartTime(startTime)
            .setSingleEventEndDateTime(endTime)
            .setDebugMode(true)
            .setSyncToken(null)
            .execute();
    System.out.println(event.getEventItems());
  }

  @Test
  public void testSyncToken2() throws Exception {
    // FT=-@RU=cbff5e64-052c-4025-a68d-e9b07e0a2efe@S=87
    // 71661FED-45EF-424C-BD0F-76211813AB68
    ICloudCalendar.IEvent iEvent =
        calendar.iEvent().list("work").setSyncToken(null).setDebugMode(true).execute();
    System.out.println(iEvent.getEventItems());
    System.out.println(iEvent.getNextSyncToken());
    System.out.println(iEvent.getUidToDelete());
  }

  @Test
  public void testCalendarQuery1() throws Exception {
    ICloudCalendar.IEvent iEvent = calendar.iEvent().list("work").setDebugMode(true).execute();
    System.out.println(iEvent.getEventItems());
  }

  @Test
  public void testCalendarQuery2() throws Exception {
    DateTime startDateTime = new DateTime();
    DateTime endDateTime = new DateTime(startDateTime.getTime() + 24 * 60 * 60 * 1000);
    ICloudCalendar.IEvent iEvent =
        calendar
            .iEvent()
            .list("work")
            .setSingleEvent(true)
            .setSingleEventStartTime(startDateTime)
            .setSingleEventEndDateTime(endDateTime)
            .setDebugMode(true)
            .execute();
    System.out.println(iEvent.getEventItems());
  }

  @Test
  public void testCalendarQuery3() throws Exception {
    DateTime startDateTime = new DateTime();
    DateTime endDateTime = new DateTime(startDateTime.getTime() + 24 * 60 * 60 * 1000);
    ICloudCalendar.IEvent iEvent =
        calendar
            .iEvent()
            .list("work")
            .setStartDateTime(startDateTime)
            .setEndDateTime(endDateTime)
            .setSingleEvent(true)
            .setSingleEventStartTime(startDateTime)
            .setSingleEventEndDateTime(endDateTime)
            .setDebugMode(true)
            .execute();
    System.out.println(iEvent.getEventItems());
  }

  @Test
  public void testGetCalendarResource() throws Exception {
    ICloudCalendar.IResource iResource = calendar.iResource().list().execute();
    System.out.println(iResource.getResourceEntries());
  }

  @Test
  public void testICloudCalDAVManager() throws Exception {
    ICloudCalDAVManager manager = new ICloudCalDAVManager(appleId, appPass, "work");
    System.out.println(manager.getEventsForNDays(3));
  }

  @Test
  public void testUpdateCalendar() throws Exception {
    ICloudCalDAVManager calDAVManager = new ICloudCalDAVManager(null, null, null);
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
  }

  @Test
  public void testAddCalendarEvent() throws Exception {
    ICloudCalDAVManager calDAVManager = new ICloudCalDAVManager(null, null, null);
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
  }
}
