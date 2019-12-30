package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.model.request.*;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.http.client.HttpClient;
import yzy.zyuanyuz.caldavclient4j.client.commons.ResourceEntry;
import yzy.zyuanyuz.caldavclient4j.client.extensions.model.request.SyncCollection;
import yzy.zyuanyuz.caldavclient4j.client.util.ICloudCalendarUtil;

/**
 * @author zyuanyuz
 * @since 2019/12/28 22:09
 */
public class ICloudCalendar {

  private HttpClient httpClient;

  private CalDAV4JMethodFactory methodFactory;

  private String principalId;

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public CalDAV4JMethodFactory getMethodFactory() {
    return methodFactory;
  }

  public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
    this.methodFactory = methodFactory;
  }

  public String getPrincipalId() {
    return principalId;
  }

  public void setPrincipalId(String principalId) {
    this.principalId = principalId;
  }

  public IResource iResource() {
    return new IResource();
  }

  public class IResource {
    public List list() {
      return new List();
    }

    public class List {}
  }

  public IEvent iEvent() {
    return new IEvent();
  }

  public class IEvent {

    public List list(String resourceId) {
      return new List(resourceId);
    }

    public class List {

      private static final String REST_PATH = "{principalId}/calendars/{resourceId}/{eventId}.ics";


      private String resourceId;

      List(String resourceId) {
        this.resourceId = resourceId;
      }

      private SyncCollection syncCollectionReport;

      private CalendarMultiget calendarMultigetReport;

      private CalendarQuery calendarQueryReport;

      public String getSyncToken() {
        return null == syncCollectionReport ? null : syncCollectionReport.getSyncToken();
      }

      public List setSyncToken(String syncToken) {
        if (null == syncCollectionReport) {
          syncCollectionReport = new SyncCollection();
        }
        syncCollectionReport.setSyncToken(syncToken);
        return this;
      }

      private DateTime startDateTime;

      public DateTime getStartDateTime() {
        return startDateTime;
      }

      public List setStartDateTime(DateTime startDataTime) {
        if (null == calendarQueryReport) {
          calendarQueryReport = new CalendarQuery();
        }
        if (null == calendarQueryReport.getCompFilter()) {
          calendarQueryReport.setCompFilter(new CompFilter());
        }
        CompFilter compFilter = calendarQueryReport.getCompFilter();
        if (null == compFilter.getTimeRange()) {
          compFilter.setTimeRange(new TimeRange(startDataTime, null));
        } else {
          compFilter.getTimeRange().setStart(startDataTime);
        }
        return this;
      }

      private DateTime endDateTime;

      public DateTime getEndDateTime() {
        return null == calendarQueryReport || null == calendarQueryReport.getCompFilter()
            ? null
            : (DateTime) calendarQueryReport.getCompFilter().getTimeRange().getEnd();
      }

      public List setEndDateTime(DateTime endDateTime) {
        if (null == calendarQueryReport) {
          calendarQueryReport = new CalendarQuery();
        }
        if (null == calendarQueryReport.getCompFilter()) {
          calendarQueryReport.setCompFilter(new CompFilter());
        }
        CompFilter compFilter = calendarQueryReport.getCompFilter();
        if (null == compFilter.getTimeRange()) {
          compFilter.setTimeRange(new TimeRange(null, endDateTime));
        } else {
          compFilter.getTimeRange().setEnd(endDateTime);
        }
        return this;
      }

      public Boolean isSingleEvent() {
        return null != calendarQueryReport
            && null != calendarQueryReport.getCalendarDataProp()
            && calendarQueryReport
                .getCalendarDataProp()
                .getExpandOrLimitRecurrenceSet()
                .equals(CalendarData.LIMIT);
      }

      /**
       * if set single event is true,singleEventStartTime and singleEventEndTime is required.
       *
       * @param isSingleEvent
       * @return
       */
      public List setSingleEvent(Boolean isSingleEvent) {
        if (null == calendarQueryReport) {
          calendarQueryReport = new CalendarQuery();
        }
        if (null == calendarQueryReport.getCalendarDataProp()) {
          calendarQueryReport.setCalendarDataProp(new CalendarData());
        }
        if (isSingleEvent)
          calendarQueryReport
              .getCalendarDataProp()
              .setExpandOrLimitRecurrenceSet(CalendarData.LIMIT);
        return this;
      }

      /**
       *
       * @param startDateTime
       * @return
       */
      public List setSingleEventStartTime(DateTime startDateTime) {
        if (isSingleEvent()) {
          calendarQueryReport.getCalendarDataProp().setRecurrenceSetStart(startDateTime);
        }
        return this;
      }

      /** @return */
      public List setSingleEventEndDateTime(DateTime endDateTime) {
        if (isSingleEvent()) {
          calendarQueryReport.getCalendarDataProp().setRecurrenceSetEnd(endDateTime);
        }
        return this;
      }

      public java.util.List<VEvent> execute() {
        // TODO execute the method

        return null;
      }
    }
  }

  /** ICloudCalendar builder */
  public static final class Builder {
    private String appleId;
    private String appPwd;

    private HttpClient httpClient;
    private CalDAV4JMethodFactory methodFactory;
    private String principalId;
    private java.util.List<ResourceEntry> resourceEntryList = new java.util.ArrayList<>();

    public Builder() {}

    /**
     * if set the HttpClient ,appleId and appPwd won't work
     *
     * @param httpClient
     * @return
     */
    public Builder setHttpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    public Builder setAppleIdAndPwd(String appleId, String appPwd) {
      this.appleId = appleId;
      this.appPwd = appPwd;
      return this;
    }

    public Builder setCalDav4JMethodFactory(CalDAV4JMethodFactory methodFactory) {
      this.methodFactory = methodFactory;
      return this;
    }

    public Builder setPrincipalId(String principalId) {
      this.principalId = principalId;
      return this;
    }

    public Builder addResourceEntry(ResourceEntry resourceEntry) {
      this.resourceEntryList.add(resourceEntry);
      return this;
    }

    public ICloudCalendar build() throws CalDAV4JException {
      ICloudCalendar iCloudCalendar = new ICloudCalendar();
      if (null == httpClient) {
        if (null == appleId || null == appPwd) {
          return null;
        }
        httpClient = ICloudCalendarUtil.createHttpClient(appleId, appPwd);
      }
      if (null == methodFactory) {
        methodFactory = new CalDAV4JMethodFactory();
      }
      if (null == principalId) {
        principalId = ICloudCalendarUtil.getPrincipalId(httpClient, methodFactory);
      }
      if (null == resourceEntryList || resourceEntryList.isEmpty()) {
        resourceEntryList =
            ICloudCalendarUtil.getAllResourceFromServer(httpClient, methodFactory, principalId);
      }
      iCloudCalendar.setHttpClient(httpClient);
      iCloudCalendar.setMethodFactory(methodFactory);
      iCloudCalendar.setPrincipalId(principalId);
      return iCloudCalendar;
    }
  }
}
