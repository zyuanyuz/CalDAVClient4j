package yzy.zyuanyuz.caldavclient4j.client.util;

/**
 * @author George Yu
 * @since 2019/10/29 17:32
 */
public abstract class ICloudCalDAVUtil {

    public static String getUidFromHref(String href){
        return href.substring(href.lastIndexOf("/")+1,href.indexOf(".ics"));
    }
}
