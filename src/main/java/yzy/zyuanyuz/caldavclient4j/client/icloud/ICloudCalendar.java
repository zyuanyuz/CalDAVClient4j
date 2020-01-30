package yzy.zyuanyuz.caldavclient4j.client.icloud;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.methods.HttpCalDAVReportMethod;
import com.github.caldav4j.methods.HttpDeleteMethod;
import com.github.caldav4j.model.request.*;
import com.github.caldav4j.util.CalDAVStatus;
import com.github.caldav4j.util.XMLUtils;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.stream.Collectors;

import static net.fortuna.ical4j.model.Calendar.VCALENDAR;
import static net.fortuna.ical4j.model.Component.VEVENT;
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

    public Get get() {
      return new Get();
    }

    public Get get(String resourceId, String eventId) {
      return new Get(resourceId, eventId);
    }

    public class Get {
      private static final String REST_PATH = "{principalId}/calendars/{resourceId}/{eventId}.ics";

      public Get() {}

      public Get(String resourceId, String... eventId) {
        this.resourceId = resourceId;
        this.eventIdList.addAll(java.util.Arrays.asList(eventId));
      }

      private String resourceId;

      public Get setResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
      }

      private java.util.List<String> eventIdList = new java.util.ArrayList<>();

      public Get setEventIdList(java.util.List<String> eventIdList) {
        this.eventIdList = eventIdList;
        return this;
      }

      public IEvent execute() {
        if (StringUtils.isAnyEmpty(resourceId) || CollectionUtils.isEmpty(eventIdList)) {
          return IEvent.this;
        }
        String resourceUri = ICLOUD_CALDAV_HOST_PORT_STR + getBaseServiceURI() + resourceId + "/";
        CalendarMultiget multiGet = new CalendarMultiget();
        multiGet.setHrefs(
            this.eventIdList.stream()
                .map(eventId -> getBaseServiceURI() + this.resourceId + "/" + eventId + ".ics")
                .collect(Collectors.toList()));
        HttpCalDAVReportMethod reportMethod;
        try {
          reportMethod =
              methodFactory.createCalDAVReportMethod(
                  resourceUri, multiGet, CalDAVConstants.DEPTH_1);
          HttpResponse response = httpClient.execute(reportMethod);
          MultiStatus multiStatus = reportMethod.getResponseBodyAsMultiStatus(response);
          IEvent.this.eventItems = ICloudCalendarUtil.getVEventFromMultiStatus(multiStatus);
        } catch (Exception e) {
          IEvent.this.eventItems = new ArrayList<>();
        }
        return IEvent.this;
      }
    }

    public List list(String resourceId) {
      return new List(resourceId);
    }

    public class List {

      private static final String REST_PATH = "{principalId}/calendars/{resourceId}/{eventId}.ics";

      private String resourceId;

      /* default is CalendarQuery , if set sync token , will be changed to SyncCollection*/
      private CalDAVReportRequest reportRequest;

      List(String resourceId) {
        this.resourceId = resourceId;
        this.reportRequest = new CalendarQuery();
      }

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
       * this time should be set as UTC time end with 'Z'
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

      /**
       * this end time should be set as UTC time what end with 'Z'
       * @param endDateTime
       * @return
       */
      public List setEndDateTime(DateTime endDateTime) {
        this.endDateTime = endDateTime;
        this.endDateTime.setUtc(true);
        return this;
      }

      /** only the time-range filter exist,the expand event will work with the recurrence events */
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

      private DateTime getExpandEventEndTime() {
        return expandEventEndDateTime;
      }

      public List setExpandEventEndTime(DateTime endDateTime) {
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
          IEvent.this.uidsToDelete = new ArrayList<>();
          IEvent.this.eventItems = new ArrayList<>();
          return;
        }
        if (!CollectionUtils.isEmpty(uidToDel)) {
          IEvent.this.uidsToDelete = uidToDel;
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
        } else {
          IEvent.this.eventItems = new ArrayList<>();
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
        CompFilter eventFilter = new CompFilter(VEVENT);
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

    public Delete delete() {
      return new Delete();
    }

    public Delete delete(String resourceId, String eventId) {
      return new Delete(resourceId, eventId);
    }

    public class Delete {
      private static final String REST_PATH = "{principalId}/calendars/{resourceId}/{eventId}.ics";

      public Delete() {};

      public Delete(String resourceId, String... eventIds) {
        this.resourceId = resourceId;
        this.eventIdList = new ArrayList<>();
        this.eventIdList.addAll(java.util.Arrays.asList(eventIds));
      }

      private String resourceId;

      public String getResourceId() {
        return this.resourceId;
      }

      public Delete setResourceId(String resourceId) {
        this.resourceId = resourceId;
        return this;
      }

      private java.util.List<String> eventIdList;

      public java.util.List<String> getEventIdList() {
        return this.eventIdList;
      }

      public Delete setEventId(java.util.List<String> eventIdList) {
        this.eventIdList = eventIdList;
        return this;
      }

      private boolean debugMode = false;

      public boolean getDebugMode() {
        return this.debugMode;
      }

      public Delete setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
      }

      public IEvent execute() {
        if (StringUtils.isAnyEmpty(resourceId) || CollectionUtils.isEmpty(eventIdList)) {
          return IEvent.this;
        }
        uidsToDelete = new java.util.ArrayList<>();
        this.eventIdList.forEach(
            eventId -> {
              String eventPath =
                  ICLOUD_CALDAV_HOST_PORT_STR
                      + getBaseServiceURI()
                      + this.resourceId
                      + "/"
                      + eventId
                      + ".ics";
              // there don't have multi delete method,so delete every eventId with a http
              // request
              HttpDeleteMethod deleteMethod = new HttpDeleteMethod(eventPath);
              HttpResponse response = null;
              try {
                response = httpClient.execute(deleteMethod);
              } catch (Exception e) {
                if (debugMode) calendarLogger.error("Problem executing delete method", e);
              }
              if (response == null
                  || response.getStatusLine().getStatusCode() != CalDAVStatus.SC_NO_CONTENT) {
                if (debugMode) calendarLogger.error("Problem executing delete method");
              }
              if (debugMode) calendarLogger.info("Event with Id : {} deleted success", eventId);
              uidsToDelete.add(eventId);
            });
        return IEvent.this;
      }
    }

    // for event get & list (include mutilGet & calendarQuery) method
    private java.util.List<VEvent> eventItems;

    private java.util.List<String> uidsToDelete; // for event sync token method

    private String nextSyncToken;

    private boolean success; // show the request flow complete or not

    public java.util.List<VEvent> getEventItems() {
      return eventItems;
    }

    public void setEventItems(java.util.List<VEvent> eventItems) {
      this.eventItems = eventItems;
    }

    public java.util.List<String> getUidsToDelete() {
      return uidsToDelete;
    }

    public void setUidsToDelete(java.util.List<String> uidsToDelete) {
      this.uidsToDelete = uidsToDelete;
    }

    public String getNextSyncToken() {
      return nextSyncToken;
    }

    public void setNextSyncToken(String nextSyncToken) {
      this.nextSyncToken = nextSyncToken;
    }

    public boolean isSuccess(){
      return this.success;
    }

    public void setSuccess(boolean success){
      this.success = success;
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
  }
}
