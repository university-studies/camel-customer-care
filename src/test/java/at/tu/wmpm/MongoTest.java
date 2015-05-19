package at.tu.wmpm;

import at.tu.wmpm.dao.IBusinessCaseDAO;
import at.tu.wmpm.model.MailBusinessCase;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by pavol on 17.5.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:camel-context.xml"})
public class MongoTest {

    @Autowired
    private Mongo mongo;
    @Autowired
    private IBusinessCaseDAO businessCaseDAO;

    @Test
    public void mongoSetupTest() throws UnknownHostException {
        assertThat(mongo, is(notNullValue()));
    }

    @Test
    public void staticTest() throws UnknownHostException {
        DB db = mongo.getDB("DSA");
        DBCollection collection = db.getCollection("ADASD");
        collection.drop();
    }


    @Test
    public void testSave() {
        MailBusinessCase mailBusinessCase = new MailBusinessCase();
        mailBusinessCase.setBody("das");
        mailBusinessCase.setSender("dsa");

        businessCaseDAO.save(mailBusinessCase);

        MailBusinessCase mailBusinessCase1 = new MailBusinessCase();
        mailBusinessCase1.setBody("rrr");
        mailBusinessCase1.setSender("rrr");

        businessCaseDAO.save(mailBusinessCase1);
    }
}