package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.sql.ResultSet;
import java.util.Iterator;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.services.ConnectionT;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.services.DBConnectorChooserLib;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.services.IndexService;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.services.ConnectionT.PreparedStatementT;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.messaging.DBMasterEndpointImpl;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/testContext.xml")
public class IndexTest {
    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private DBRepository dbRepository;

    @Autowired
    private DBMasterEndpointImpl dbmaster;

    @Autowired
    private IndexService indexService;

    // @Test
    public void initMap() {
	Assert.assertTrue(dbRepository.initMap());
    }

    // @Test
    public void testXPath() throws XPathExpressionException {
	String xpathExpression = "/dbs/bib/text()";
	String xmlString = "<dbs><item>31501</item><ibarcode>00405799</ibarcode><bib>23723</bib><status wert='i'>test</status><ruecken><breiteMm>34.0</breiteMm><hoeheMm>249.0</hoeheMm></ruecken></dbs>";

	XPathFactory xpf = XPathFactory.newInstance();
	XPath xpath = xpf.newXPath();

	String saxValue = (String) xpath
		.evaluate(xpathExpression, new InputSource(
			new ByteArrayInputStream(xmlString.getBytes())),
			XPathConstants.STRING);

	Assert.assertEquals("23723", saxValue);

    }

    // @Test
    public void testXPathWithWrongXML() throws XPathExpressionException {
	String xpathExpression = "/dbs/bob/text()";
	String xmlString = "<dbs><item>31501</item><ibarcode>00405799</ibarcode><bib>23723</bib><status wert='i'>test</status><ruecken><breiteMm>34.0</breiteMm><hoeheMm>249.0</hoeheMm></ruecken></dbs>";

	XPathFactory xpf = XPathFactory.newInstance();
	XPath xpath = xpf.newXPath();

	String saxValue = (String) xpath
		.evaluate(xpathExpression, new InputSource(
			new ByteArrayInputStream(xmlString.getBytes())),
			XPathConstants.STRING);

	Assert.assertNotNull(saxValue);
	Assert.assertEquals("", saxValue);
    }

    // @Test
    public void testNumberOfSets() throws Exception {
	Long count = dbRepository.getNodeCountByEntityType(DBs.class);
	Assert.assertTrue(count > 1);
	Assert.assertEquals(
		dbmaster.parseJPQLStatement("select COUNT(s) FROM DBs s")
			.get("results").get(0), count);
    }

    // @Test
    public void testBibnumIndexWithWrongPath() {
	indexService.indexSetsByXPath("bib", "/bob/text()");
    }

    // @Test
    public void testBibnumIndex() {
	indexService.indexSetsByXPath("bib", "/bib/text()");
    }

    // @Test
    public void testBibIndexTreffer() {
	Iterable<DBs> dbses = dbRepository.getDBsesByIndex("bib", "17937");

	Assert.assertTrue(dbses.iterator().hasNext());

	if (log.isDebugEnabled())
	    for (DBs s : dbses)
		log.debug("Set gefunden für bib=17937: Set-ID=" + s.getId()
			+ " XML=" + s.getXmlString());

	// Assert.assertEquals(new Long(225350),
	// dbRepository.getDBsByIndex("bib", "17937").getId());
    }

    // @Test
    public void testIBarIndex() {
	indexService.indexSetsByXPath("ibarcode", "/ibarcode/text()");
    }

    // @Test
    public void testBibVsIbar() {
	Iterable<DBs> dbses = dbRepository.getDBsesByIndex("bib", "17937");
	Assert.assertTrue(dbses.iterator().hasNext());

	if (log.isDebugEnabled())
	    for (DBs s : dbses)
		log.debug("Set gefunden für bib=17937: Set-ID=" + s.getId()
			+ " XML=" + s.getXmlString());

	Iterable<DBs> dbses_ibar = dbRepository.getDBsesByIndex("ibarcode",
		"00292337");
	Iterator<DBs> ibarIt = dbses_ibar.iterator();
	Assert.assertTrue(ibarIt.hasNext());
	// Assert.assertFalse(ibarIt.hasNext());
	// if (log.isDebugEnabled())
	// for (DBs s : dbses_ibar)
	// log.debug("Set gefunden für ibarcode=00292337: Set-ID="
	// + s.getId() + " XML=" + s.getXmlString());

	DBs ibarSet = ibarIt.next();
	Assert.assertNotNull(ibarSet);
	if (log.isDebugEnabled())
	    log.debug("Set gefunden für ibarcode=00292337: Set-ID="
		    + ibarSet.getId() + " XML=" + ibarSet.getXmlString());

	// boolean success = false;
	// for (DBs s : dbses DBs ibar : dbses_ibar) {
	// if (s.getId().equals(ibarSet.getId())) {
	// success = true;
	// break;
	// }
	// }

	// Assert.assertTrue(success);
    }

    @Test
    public void addIsbnToHorizonItem() throws Exception {
	indexService.indexSetWithDBQuery("bib#", "/bib/text()", "isbn");
    }
}
