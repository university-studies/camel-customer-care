package at.tu.wmpm.route;

import at.tu.wmpm.filter.SpamFilter;
import at.tu.wmpm.model.BusinessCase;
import at.tu.wmpm.processor.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;

/**
 * Created by pavol on 8.6.2015.
 */
@Component
@DependsOn({"google-calendar"})
public class MailRoute extends RouteBuilder {

    private static final Logger log = LoggerFactory.getLogger(ExceptionRoute.class);

    @Value("${dropbox.auth.param}")
    private String DROPBOX_AUTH_PARAMETERS;

    @Autowired
    private MailProcessor mailProcessor;
    @Autowired
    private MailUpdateCommentsProcessor mailUpdateProcessor;
    @Autowired
    private WireTapLogMail wiretapMail;
    @Autowired
    private AutoReplyHeadersProcessor autoReplyHeadersProcessor;
    @Autowired
    private CalendarProcessor calendarProcessor;


    @Override
    @SuppressWarnings("deprecation")
    public void configure() throws Exception {

        JAXBContext jaxbContext = JAXBContext.newInstance(BusinessCase.class);
        JaxbDataFormat jaxbFormat = new JaxbDataFormat(jaxbContext);

        from("pop3s://{{mail.userName}}@{{mail.pop.address}}:{{mail.pop.port}}?password={{mail.password}}")
                .wireTap("direct:logMail", wiretapMail)
                .process(mailProcessor)
                .filter().method(SpamFilter.class, "isNoSpam")
                .choice()
                .when(header("hasParent").isEqualTo(true))
                .to("direct:mailUpdateComment")
                .otherwise()
                .multicast()
                .parallelProcessing()
                .to("mongodb:mongo?database={{mongodb.database}}&collection={{mongodb.collection}}&operation=insert",
                        "direct:autoReplyEmail",
                        "direct:addToCalendar",
                        "direct:storeXMLEmail")
                .end();


        from("direct:mailUpdateComment")
                .transform(body(BusinessCase.class).method("getParentId"))
                .to("mongodb:mongo?database={{mongodb.database}}&collection={{mongodb.collection}}&operation=findById")
                .process(mailUpdateProcessor)
                .to("mongodb:mongo?database={{mongodb.database}}&collection={{mongodb.collection}}&operation=save");

        from("direct:logMail")
                .to("file:logs/workingdir/wiretap-logs/logMail?fileName=mail_${date:now:yyyyMMdd_HH-mm-SS}.log&flatten=true");

        from("direct:autoReplyEmail")
                .process(autoReplyHeadersProcessor)
                .to("velocity:mail-templates/auto-reply.vm")
                .to("smtps://{{mail.smtp.address}}:{{mail.smtp.port}}?password={{mail.password}}&username={{mail.userName}}");

        from("direct:addToCalendar")
                .process(calendarProcessor)
                .to("google-calendar://events/insert?calendarId={{google.calendar.id}}");

        from("direct:storeXMLEmail")
                .marshal(jaxbFormat)
                .setHeader(Exchange.FILE_NAME, constant("ex1.xml"))
                .to("file:logs/XMLExports?autoCreate=true")
                .recipientList(
                        simple("dropbox://put?"
                                + DROPBOX_AUTH_PARAMETERS
                                + "&uploadMode=add&localPath=logs/XMLExports/ex1.xml&remotePath=/XMLExports/M_${date:now:yyyyMMdd_HH-mm-SS}.xml"));
    }
}