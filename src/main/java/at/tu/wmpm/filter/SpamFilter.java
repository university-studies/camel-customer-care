package at.tu.wmpm.filter;

import at.tu.wmpm.model.BusinessCase;
import at.tu.wmpm.model.MailBusinessCase;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pavol on 18.5.2015.
 */
public class SpamFilter {

    private static final Logger log = LoggerFactory.getLogger(SpamFilter.class);


    public static boolean isNoSpam(@Body BusinessCase body, @Header("Return-Path") String from) {

        if (body.getComments().toString().contains("spam")) {
            log.info("spam from:{}, body:\n{}", from, body);
            return false;
        }

        return true;
    }
}
