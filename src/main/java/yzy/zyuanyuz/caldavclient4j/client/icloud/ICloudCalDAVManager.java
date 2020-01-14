package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.CalDAVResource;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.*;
import com.github.caldav4j.model.response.CalendarDataProperty;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.ICalendarUtils;
import com.github.caldav4j.util.XMLUtils;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yzy.zyuanyuz.caldavclient4j.client.AbstractCalDAVManager;
import yzy.zyuanyuz.caldavclient4j.client.commons.EventEntry;
import yzy.zyuanyuz.caldavclient4j.client.extensions.model.request.SyncCollection;
import yzy.zyuanyuz.caldavclient4j.client.util.ICloudCalendarUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static net.fortuna.ical4j.model.Calendar.VCALENDAR;
import static net.fortuna.ical4j.model.Component.VEVENT;
import static yzy.zyuanyuz.caldavclient4j.client.commons.ICloudCalDAVConstants.ICLOUD_CALDAV_HOST_STR;

/**
 * resource
 *
 * @author zyuanyuz
 * @since 2019/10/15 20:59
 */
public class ICloudCalDAVManager extends AbstractCalDAVManager {
  private static final Logger logger = LoggerFactory.getLogger(ICloudCalDAVManager.class);

  private String principal;

  private Map<String /*event uuid*/, EventEntry> eventsMap;

  private String calFolderPath; /*resource url*/

  private static final ICloudCalendarDataProperty iCloudCalendarDataProperty =
      new ICloudCalendarDataProperty(); /*used to invoke the static code*/

  public ICloudCalDAVManager(String appleId, String password, String calName) throws Exception {
    super.setMethodFactory(new CalDAV4JMethodFactory());
    this.calName = calName;
    this.eventsMap = new ConcurrentHashMap<>();
    this.httpClient = ICloudCalendarUtil.createHttpClient(appleId, password);
    this.principal = ICloudCalendarUtil.getPrincipalId(this.httpClient, this.methodFactory);
    this.calFolderPath = ICLOUD_CALDAV_HOST_STR + this.principal + "/calendars/" + this.calName + "/";
    this.setCalendarCollectionRoot(this.calFolderPath);
  }

  /**
   * * get etag from server by event uuid, TODO test network fail need handle
   *
   * @param uuid
   * @return
   */
  public String getETagFromServer(String uuid) throws CalDAV4JException {
    String pathGetETag = ICloudCalendarUtil.pathToCalendarPath(this.principal, this.calName, uuid);
    return getETagbyMultiget(this.httpClient, pathGetETag);
  }

  /** TODO test */
  public void refreshAllCachedEvents() throws CalDAV4JException {
    List<String> calendarUris = new ArrayList<>(this.eventsMap.keySet());

    CalendarMultiget query = new CalendarMultiget();

    query.addProperty(CalDAVConstants.DNAME_GETETAG);
    query.setCalendarDataProp(new CalendarData());
    query.setHrefs(calendarUris);

    HttpCalDAVReportMethod reportMethod = null;
    try {
      reportMethod =
          methodFactory.createCalDAVReportMethod(
              getCalendarCollectionRoot(), query, CalDAVConstants.DEPTH_1);
      HttpResponse httpResponse =
          httpClient.execute(getDefaultHttpHost(reportMethod.getURI()), reportMethod);

      MultiStatusResponse[] responses =
          reportMethod.getResponseBodyAsMultiStatus(httpResponse).getResponses();
      Set<String> uuidsFromServer = new HashSet<>();
      for (MultiStatusResponse response : responses) {
        if (response.getStatus()[0].getStatusCode() == CalDAVStatus.SC_OK) { // Status is OK
          VEvent event =
              (VEvent) CalendarDataProperty.getCalendarfromResponse(response).getComponent(VEVENT);
          String etag = CalendarDataProperty.getEtagfromResponse(response);
          String uuid = event.getUid().toString();
          uuidsFromServer.add(uuid);
          if (null == eventsMap.get(uuid) || !etag.equals(eventsMap.get(uuid).getEtag())) {
            // update the events map
            eventsMap.put(uuid, new EventEntry(uuid, etag, event));
          }
        }
      }
      List<String> uuidToDelete =
          eventsMap.keySet().stream()
              .filter(uuid -> !uuidsFromServer.contains(uuid))
              .collect(Collectors.toList());
      for (String uuid : uuidToDelete) {
        eventsMap.remove(uuid);
      }
    } catch (Exception e) {
      throw new CalDAV4JException("Problem executing method", e);
    }
  }

  /**
   * refresh the cached event with specific uuid
   *
   * @param uuid the event uuid
   */
  public boolean refreshCachedEvent(String uuid) throws CalDAV4JException {
    String etag = this.getETagFromServer(uuid);
    if (null == etag) { // TODO Test when the event deleted from server
      eventsMap.remove(uuid);
      return true;
    }
    if (null != eventsMap.get(uuid) && eventsMap.get(uuid).getEtag().equals(etag)) {
      return false;
    }
    VEvent event = getEventFromServer(uuid);
    eventsMap.put(uuid, new EventEntry(uuid, etag, event));
    return true;
  }

  /**
   * @param uuidList
   * @return
   * @throws Exception
   */
  public List<VEvent> multiGetEventsFromServer(List<String> uuidList) throws Exception {
    List<String> urls =
        uuidList.stream()
            .map(uuid -> ICloudCalendarUtil.pathToCalendarPath(this.principal, this.calName, uuid))
            .collect(toList());

    return multigetCalendarUris(this.httpClient, urls).stream()
        .map(c -> (VEvent) (c.getComponent(VEVENT)))
        .collect(toList());
  }

  /**
   * for calendar service to get all events for three days (72 hours). note: this Date must be set
   * as UTC timezone.
   *
   * @return
   * @throws CalDAV4JException
   */
  public List<VEvent> getEventsForThreeDays() throws CalDAV4JException {
    return this.getEventsForNDays(3);
  }

  /**
   * for calendar service to get all events for n days. note: this Date must be set * as UTC
   * timezone.
   *
   * @param n
   * @return
   * @throws CalDAV4JException
   */
  public List<VEvent> getEventsForNDays(int n) throws CalDAV4JException {
    DateTime beginDate = new DateTime(true);
    DateTime endDate = new DateTime(beginDate.getTime() + n * 24 * 60 * 60 * 1000);
    endDate.setUtc(true);
    return this.getTimeRangeEvents(beginDate, endDate);
  }

  /**
   * the date must be utc timezone(date end with 'Z'),otherwise there will cause 412 precondition
   * failed.
   *
   * @param beginDate
   * @param endDate
   * @return
   * @throws CalDAV4JException
   */
  public List<VEvent> getTimeRangeEvents(Date beginDate, Date endDate) throws CalDAV4JException {
    DavPropertyNameSet properties = new DavPropertyNameSet();
    properties.add(DavPropertyName.GETETAG);

    CompFilter calendarFilter = new CompFilter(VCALENDAR);
    CompFilter eventFilter = new CompFilter(Component.VEVENT);
    eventFilter.setTimeRange(new TimeRange(beginDate, endDate));
    calendarFilter.addCompFilter(eventFilter);

    CalendarData calendarData = new CalendarData(CalendarData.LIMIT, beginDate,endDate, null);

    CalendarQuery query = new CalendarQuery(properties, calendarFilter, calendarData, false, false);
    logger.info(XMLUtils.prettyPrint(query));

    MultiStatusResponse[] multiStatusResponses;
    HttpCalDAVReportMethod reportMethod;
    try {
      reportMethod =
          methodFactory.createCalDAVReportMethod(
              this.calFolderPath, query, CalDAVConstants.DEPTH_1);
      HttpResponse response = httpClient.execute(reportMethod);
      // logger.info(EntityUtils.toString(response.getEntity()));
      multiStatusResponses = reportMethod.getResponseBodyAsMultiStatus(response).getResponses();
    } catch (Exception e) {
      throw new CalDAV4JException(e.getMessage());
    }

    List<VEvent> events = new ArrayList<>();
    for (MultiStatusResponse statusResponse : multiStatusResponses) {

      CalDAVResource calDAVResource = new CalDAVResource(statusResponse);
      events.add((VEvent) (calDAVResource.getCalendar().getComponent(VEVENT)));
    }
    return events;
  }

  /**
   * get event from icloud server with specified uuid
   *
   * @param uuid
   * @return
   * @throws CalDAV4JException
   */
  public VEvent getEventFromServer(String uuid) throws CalDAV4JException {
    String relativePath = uuid + ".ics";
    Calendar calendar = getCalendar(getHttpClient(), relativePath);
    return ICalendarUtils.getFirstEvent(calendar);
  }

  /**
   * [TODO] need test Add a event to server
   *
   * @param event
   * @throws CalDAV4JException
   */
  public String addEventToServer(VEvent event) throws CalDAV4JException {
    if (ICalendarUtils.getUIDValue(event) == null) {
      // if the event haven't uuid
      String uuid = UUID.randomUUID().toString();
      try {
        event.getProperty(Property.UID).setValue(uuid);
      } catch (Exception e) {
        throw new CalDAV4JException("Add event and set uuid throws a exception.");
      }
    } else if (eventsMap.containsKey(ICalendarUtils.getUIDValue(event))) {
      return ICalendarUtils.getUIDValue(event);
    }
    add(this.httpClient, event, null);
    String etag = this.getETagFromServer(event.getUid().toString());
    eventsMap.put(event.getUid().toString(), new EventEntry(etag, event)); // also add to local map
    return ICalendarUtils.getUIDValue(event);
  }

  /**
   * delete event from server and local cached map with event uuid
   *
   * @param uuid
   * @throws CalDAV4JException
   */
  public void deleteEventFromServer(String uuid) throws CalDAV4JException {
    String pathToDelete = this.calFolderPath + uuid + ".ics";
    delete(this.httpClient, pathToDelete);
    eventsMap.remove(uuid);
  }

  // getter and setter

  public String getPrincipal() {
    return principal;
  }

  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  public String getCalFolderPath() {
    return calFolderPath;
  }

  public void setCalFolderPath(String calFolderPath) {
    this.calFolderPath = calFolderPath;
  }
}
