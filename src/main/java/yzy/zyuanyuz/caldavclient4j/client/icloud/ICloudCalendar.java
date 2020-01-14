package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.model.request.*;
import com.github.caldav4j.util.XMLUtils;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import yzy.zyuanyuz.caldavclient4j.client.commons.ResourceEntry;
import yzy.zyuanyuz.caldavclient4j.client.extensions.model.request.SyncCollection;
import yzy.zyuanyuz.caldavclient4j.client.util.ICloudCalendarUtil;

import java.util.ArrayList;

import static net.fortuna.ical4j.model.Calendar.VCALENDAR;
import static yzy.zyuanyuz.caldavclient4j.client.commons.ICloudCalDAVConstants.ICLOUD_CALDAV_HOST_PORT_STR;

/**
 * @author zyuanyuz
 * @since 2019/12/28 22:09
 */
public class ICloudCalendar {
  private static final Logger calendarLogger = LoggerFactory.getLogger(ICloudCalendar.class);

  private HttpClient httpClient;

  private CalDAV4JMethodFactory methodFactory;

  private String principalId;

  private boolean debugMode;

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

  public boolean getDebugMode() {
    return debugMode;
  }

  public void setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }

  private String getBaseServiceURI() {
    return principalId + "/calendars/";
  }

  public IResource iResource() {
    return new IResource();
  }

  public class IResource {

    public List list() {
      return new List();
    }

    public class List {
      private static final String REST_PATH = "{principalId}/calendars";

      public IResource execute() {
        try {
          IResource.this.resourceEntries =
              ICloudCalendarUtil.getAllResourceFromServer(httpClient, methodFactory, principalId);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return IResource.this;
      }
    }

    private java.util.List<ResourceEntry> resourceEntries;

    public java.util.List<ResourceEntry> getResourceEntries() {
      return resourceEntries;
    }

    public void setResourceEntries(java.util.List<ResourceEntry> resourceEntries) {
      this.resourceEntries = resourceEntries;
    }
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
        this.reportRequest = new CalendarQuery();
      }

      private CalDAVReportRequest reportRequest;

      public String getSyncToken() {
        return reportRequest instanceof SyncCollection
            ? ((SyncCollection) reportRequest).getSyncToken()
            : null;
      }

      /**
       * this function will change the reportRequest to SyncCollection report
       *
       * @param syncToken
       * @return
       */
      public List setSyncToken(String syncToken) {
        if (!(reportRequest instanceof SyncCollection)) {
          reportRequest = new SyncCollection();
        }
        ((SyncCollection) reportRequest).setSyncToken(syncToken);
        return this;
      }

      private DateTime startDateTime;

      public DateTime getStartDateTime() {
        return startDateTime;
      }

      /**
       * this time should set as UTC time
       *
       * @param startDataTime
       * @return
       */
      public List setStartDateTime(DateTime startDataTime) {
        this.startDateTime = startDataTime;
        this.startDateTime.setUtc(true);
        return this;
      }

      private DateTime endDateTime;

      public DateTime getEndDateTime() {
        return endDateTime;
      }

      public List setEndDateTime(DateTime endDateTime) {
        this.endDateTime = endDateTime;
        this.endDateTime.setUtc(true);
        return this;
      }

      /** if set single event is false,singleEventStartTime and singleEventEndTime is required. */
      private boolean isExpandEvent = false;

      public boolean isExpandEvent() {
        return isExpandEvent;
      }

      public List setExpandEvent(boolean isExpandEvent) {
        this.isExpandEvent = isExpandEvent;
        return this;
      }

      private DateTime expandEventStartDateTime = null;

      private DateTime getExpandEventStartDateTime() {
        return expandEventStartDateTime;
      }

      public List setExpandEventStartTime(DateTime startDateTime) {
        this.expandEventStartDateTime = startDateTime;
        this.expandEventStartDateTime.setUtc(true);
        return this;
      }

      private DateTime expandEventEndDateTime = null;

      private DateTime getExpandEventEndDateTime() {
        return expandEventEndDateTime;
      }

      public List setExpandEventEndDateTime(DateTime endDateTime) {
        this.expandEventEndDateTime = endDateTime;
        this.expandEventEndDateTime.setUtc(true);
        return this;
      }

      private boolean debugMode = false;

      public boolean getDebugMode() {
        return debugMode;
      }

      public List setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
      }

      public IEvent execute() {
        if (reportRequest instanceof SyncCollection) {
          executeWithSyncToken();
        } else if (reportRequest instanceof CalendarQuery) {
          executeWithCalendarQuery();
        }
        return IEvent.this;
      }

      /**
       * this function will execute twice http request if SyncCollection report response return
       * multi event hrefs.
       */
      private void executeWithSyncToken() {
        String resourceUri = ICLOUD_CALDAV_HOST_PORT_STR + getBaseServiceURI() + resourceId + "/";

        java.util.List<String> hrefsToMGet;
        java.util.List<String> uidToDel;
        String nextSyncToken;

        HttpCalDAVReportMethod reportMethod;
        HttpResponse response;

        // add get etag
        // DavPropertyNameSet properties = new DavPropertyNameSet();
        // properties.add(DavPropertyName.GETETAG);
        // ((SyncCollection)reportRequest).getProperties().addChildren(properties);

        try {
          reportMethod =
              methodFactory.createCalDAVReportMethod(
                  resourceUri, reportRequest, CalDAVConstants.DEPTH_0);
          if (debugMode) {
            calendarLogger.info("syncToken report method:{}", XMLUtils.prettyPrint(reportRequest));
          }
          HttpEntity httpEntity = httpClient.execute(reportMethod).getEntity();
          Element element = reportMethod.getResponseBodyAsDocument(httpEntity).getDocumentElement();
          nextSyncToken =
              element.getElementsByTagName("sync-token").item(0).getFirstChild().getNodeValue();
          MultiStatus multiStatus = MultiStatus.createFromXml(element);
          Pair<java.util.List<String>, java.util.List<String>> triple =
              ICloudCalendarUtil.getSyncHrefsAndToDel(multiStatus);
          hrefsToMGet = triple.getLeft();
          uidToDel = triple.getRight();
        } catch (Exception e) {
          if (debugMode) {
            calendarLogger.error("syncToken report with error:", e);
          }
          IEvent.this.uidToDelete = new ArrayList<>();
          IEvent.this.eventItems = new ArrayList<>();
          return;
        }
        if (null != uidToDel && !uidToDel.isEmpty()) {
          IEvent.this.uidToDelete = uidToDel;
        }
        if (null != nextSyncToken) {
          IEvent.this.nextSyncToken = nextSyncToken;
        }

        if (null != hrefsToMGet && !hrefsToMGet.isEmpty()) {
          CalendarMultiget multiGet = new CalendarMultiget();
          multiGet.setHrefs(hrefsToMGet);
          if (isExpandEvent) {
            CalendarData calendarData =
                new CalendarData(
                    CalendarData.EXPAND, expandEventStartDateTime, expandEventEndDateTime, null);
            multiGet.setCalendarDataProp(calendarData);
          } else {
            multiGet.setCalendarDataProp(new CalendarData());
          }
          try {
            reportMethod =
                methodFactory.createCalDAVReportMethod(
                    resourceUri, multiGet, CalDAVConstants.DEPTH_1);
            if (debugMode) {
              calendarLogger.info("multiGet report method:{}", XMLUtils.prettyPrint(multiGet));
            }
            response = httpClient.execute(reportMethod);
            IEvent.this.eventItems =
                ICloudCalendarUtil.getVEventFromMultiStatus(
                    reportMethod.getResponseBodyAsMultiStatus(response));
          } catch (Exception e) {
            if (debugMode) {
              calendarLogger.error("multiGet calendar failed with error:", e);
            }
            IEvent.this.eventItems = new ArrayList<>();
          }
        }
      }

      /** */
      private void executeWithCalendarQuery() {
        String resourceUri =
            ICLOUD_CALDAV_HOST_PORT_STR + "/" + getBaseServiceURI() + resourceId + "/";

        DavPropertyNameSet properties = new DavPropertyNameSet();
        properties.add(DavPropertyName.GETETAG);
        ((CalendarQuery) reportRequest).setProperties(properties);

        CompFilter calendarFilter = new CompFilter(VCALENDAR);
        CompFilter eventFilter = new CompFilter(Component.VEVENT);
        calendarFilter.addCompFilter(eventFilter);
        if (null != startDateTime || null != endDateTime) {
          eventFilter.setTimeRange(new TimeRange(startDateTime, endDateTime));
        }
        ((CalendarQuery) reportRequest).setCompFilter(calendarFilter);

        if (isExpandEvent) {
          CalendarData calendarData =
              new CalendarData(
                  CalendarData.EXPAND, expandEventStartDateTime, expandEventEndDateTime, null);
          ((CalendarQuery) reportRequest).setCalendarDataProp(calendarData);
        } else {
          ((CalendarQuery) reportRequest).setCalendarDataProp(new CalendarData());
        }

        try {
          HttpCalDAVReportMethod reportMethod =
              methodFactory.createCalDAVReportMethod(
                  resourceUri, reportRequest, CalDAVConstants.DEPTH_1);
          if (debugMode) {
            calendarLogger.info(
                "calendarQuery report request:{}", XMLUtils.prettyPrint(reportRequest));
          }
          HttpResponse response = httpClient.execute(reportMethod);
          // System.out.println(EntityUtils.toString(response.getEntity()));
          IEvent.this.eventItems =
              ICloudCalendarUtil.getVEventFromMultiStatus(
                  reportMethod.getResponseBodyAsMultiStatus(response));
        } catch (Exception e) {
          if (debugMode) {
            calendarLogger.error("multiGet calendar failed with error:", e);
          }
          IEvent.this.eventItems = new ArrayList<>();
        }
      }
    }

    private java.util.List<VEvent> eventItems;

    private java.util.List<String> uidToDelete;

    private String nextSyncToken;

    public java.util.List<VEvent> getEventItems() {
      return eventItems;
    }

    public void setEventItems(java.util.List<VEvent> eventItems) {
      this.eventItems = eventItems;
    }

    public java.util.List<String> getUidToDelete() {
      return uidToDelete;
    }

    public void setUidToDelete(java.util.List<String> uidToDelete) {
      this.uidToDelete = uidToDelete;
    }

    public String getNextSyncToken() {
      return nextSyncToken;
    }

    public void setNextSyncToken(String nextSyncToken) {
      this.nextSyncToken = nextSyncToken;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  /** ICloudCalendar builder */
  public static final class Builder {
    private String appleId;
    private String appPwd;

    private HttpClient httpClient;
    private CalDAV4JMethodFactory methodFactory;
    private String principalId;
    private boolean debugMode;
    //    private java.util.List<ResourceEntry> resourceEntryList = new java.util.ArrayList<>();

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

    public Builder setDebugMode(boolean debugMode) {
      this.debugMode = debugMode;
      return this;
    }

    public ICloudCalendar build() throws CalDAV4JException {
      ICloudCalendar iCloudCalendar = new ICloudCalendar();
      if (null == httpClient) {
        if (StringUtils.isEmpty(appleId) || StringUtils.isEmpty(appPwd)) {
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
      //      if (null == resourceEntryList || resourceEntryList.isEmpty()) {
      //        resourceEntryList =
      //            ICloudCalendarUtil.getAllResourceFromServer(httpClient, methodFactory,
      // principalId);
      //      }
      iCloudCalendar.setHttpClient(httpClient);
      iCloudCalendar.setMethodFactory(methodFactory);
      iCloudCalendar.setPrincipalId(principalId);
      iCloudCalendar.setDebugMode(debugMode);
      return iCloudCalendar;
    }

    //    private boolean validBuilder(){
    //
    //    }
  }
}
