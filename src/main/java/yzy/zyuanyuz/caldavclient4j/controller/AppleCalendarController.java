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
    calDavAppleManager.addEvent();
    return "here";
  }

}
