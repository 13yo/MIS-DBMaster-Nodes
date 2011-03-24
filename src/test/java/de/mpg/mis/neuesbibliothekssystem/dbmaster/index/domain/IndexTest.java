package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/testContext.xml")
public class IndexTest {

    @Autowired
    private DBRepository dbRepository;

    @Test
    public void initMap() {
	Assert.assertTrue(dbRepository.initMap());
    }

}
