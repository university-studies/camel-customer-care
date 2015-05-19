package at.tu.wmpm.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import facebook4j.Post;
import at.tu.wmpm.model.FacebookBusinessCase;
import at.tu.wmpm.model.MailBusinessCase;

/**
 * Created by pavol on 8.5.2015.
 *
 * Access token is probably valid only for short period of time
 *      get accessToken - https://developers.facebook.com/tools/explorer/145634995501895/
 *
 * Facebook app - Customer Care
 *      https://developers.facebook.com/apps/833818856683698/dashboard/
 *
 * Facebook page - Area 51 Customer Care
 *      https://www.facebook.com/area51customercare
 *
 **/
@Service
public class FacebookProcessor implements Processor {

    private final Logger log = LoggerFactory.getLogger(FacebookProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        log.debug("\n\nFACEBOOK");
        log.debug(ReflectionToStringBuilder.toString(exchange));
        log.debug("\n\n");
        
        Message in = exchange.getIn();
        
        Post post = (Post)in.getBody();
        
        /**
         * Set new Business case to exchange message
         */
        FacebookBusinessCase businessCase = new FacebookBusinessCase();
        businessCase.setSender(post.getFrom().getName());
        businessCase.setBody(post.getMessage());
 
        Message message = new DefaultMessage();
        message.setBody(businessCase);
        
        exchange.setOut(message);    	

    }
}
