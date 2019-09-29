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
    //
    // calDavAppleManager.setCalendarCollectionRoot("http://p46-caldav.icloud.com:80/published/2");
    //    Calendar cal =
    //        calDavAppleManager.getCalendar(
    //            calDavAppleManager.getHttpClient(),
    //            "/MTY4ODQ0ODI2ODIxNjg4NAMjulTFlQOcdNX48keW2Xoo8ipE263TVr0DdPLG2toH");
    Calendar cal =
        calDavAppleManager.getCalendarWithAuth();
    return cal.toString();
  }
}
