package yzy.zyuanyuz.caldavclient4j.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yzy.zyuanyuz.caldavclient4j.client.icloud.ICloudCalDAVManager;

/**
 * @author George Yu
 * @since 2019/10/29 14:40
 */
@Configuration
public class CalDAVConfig {

    @Bean
    public ICloudCalDAVManager iCloudCalDAVManager() throws Exception{
        return new ICloudCalDAVManager("zoom2019097@icloud.com","itkg-ogby-zxti-hpav","work");
    }
}
