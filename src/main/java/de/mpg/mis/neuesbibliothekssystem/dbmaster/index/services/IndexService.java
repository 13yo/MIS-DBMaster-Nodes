package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.services;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.graph.neo4j.finder.FinderFactory;
import org.springframework.data.graph.neo4j.finder.NodeFinder;
import org.springframework.data.graph.neo4j.template.Neo4jOperations;
import org.springframework.data.graph.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.DBRepository;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.DBs;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.services.ConnectionT.PreparedStatementT;

@Service
public class IndexService {
    private final Log log = LogFactory.getLog(this.getClass());

    private DBRepository dbRepository;

    @Autowired
    public void setDbRepository(DBRepository repo) {
	this.dbRepository = repo;
    }

    private Neo4jOperations neo;

    @Autowired
    private void setNeoTemplate(Neo4jTemplate neo) {
	this.neo = neo;
    }

    private FinderFactory finderFactory;

    /**
     * @param finderFactory
     *            the finderFactory to set
     */
    @Autowired
    public void setFinderFactory(FinderFactory finderFactory) {
	this.finderFactory = finderFactory;
    }

    @Transactional
    public void indexSetsByXPath(String indexName, String xPathExpression) {
	XPathFactory xpf = XPathFactory.newInstance();
	XPath xpath = xpf.newXPath();

	StringBuilder pathSb = new StringBuilder();
	pathSb.append("/dbs");
	pathSb.append(xPathExpression);

	String saxValue = null;
	StringBuilder xmlSb = null;
	for (DBs s : dbRepository.getAllDBses()) {
	    xmlSb = new StringBuilder();

	    // Klammerung des eigentlichen Strings in <dbs></dbs> für die
	    // Wohlgeformtheit
	    xmlSb.append("<dbs>").append(s.getXmlString()).append("</dbs>");

	    try {
		saxValue = (String) xpath
			.evaluate(pathSb.toString(), new InputSource(
				new ByteArrayInputStream(xmlSb.toString()
					.getBytes())), XPathConstants.STRING);

		if (saxValue != null && !saxValue.equals("")) {
		    neo.index("DBs", s.getPersistentState(), indexName,
			    saxValue);
		    if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Index ").append(indexName).append("=")
				.append(saxValue)
				.append(" erstellt für Set mit ID ")
				.append(s.getId());
			log.debug(sb.toString());
		    }
		    saxValue = null;
		}
	    } catch (XPathExpressionException e) {
		// if (log.isErrorEnabled()) {
		// StringBuilder sb = new StringBuilder();
		// sb.append("Fehler aufgetreten bei Set-ID ")
		// .append(s.getId())
		// .append(". Argumente für Methode: indexName=")
		// .append(indexName).append(" xPathExpression=")
		// .append(xPathExpression);
		// log.error(sb.toString(), e);
		// }
	    }
	}
    }

    @Transactional
    public void indexSetWithDBQuery(String queryIndexName,
	    String xPathExpression, String indexName) throws Exception {
	DBConnectorChooserLib dbConnector = new DBConnectorChooserLib();
	ConnectionT con = new ConnectionT(dbConnector.getConnector("horizon")
		.getConnection());
	PreparedStatementT psItem = con.prepareStatement("select a."
		+ indexName + " from horizon..isbn_inverted a where a."
		+ queryIndexName + "=?");

	XPathFactory xpf = XPathFactory.newInstance();
	XPath xpath = xpf.newXPath();

	StringBuilder pathSb = new StringBuilder();
	pathSb.append("/dbs");
	pathSb.append(xPathExpression);

	String saxValue = null;
	StringBuilder xmlSb = null;
	for (DBs s : dbRepository.getAllDBses()) {
	    xmlSb = new StringBuilder();

	    // Klammerung des eigentlichen Strings in <dbs></dbs> für die
	    // Wohlgeformtheit
	    xmlSb.append("<dbs>").append(s.getXmlString()).append("</dbs>");

	    try {
		saxValue = (String) xpath
			.evaluate(pathSb.toString(), new InputSource(
				new ByteArrayInputStream(xmlSb.toString()
					.getBytes())), XPathConstants.STRING);

		if (saxValue != null && !saxValue.equals("")) {

		    psItem.setInt(1, Integer.parseInt(saxValue));
		    ResultSet rs = psItem.executeQuery();
		    while (rs.next()) {
			String res = rs.getString(1);
			neo.index("DBs", s.getPersistentState(), indexName, res);
			if (log.isDebugEnabled()) {
			    StringBuilder sb = new StringBuilder();
			    sb.append("Index ").append(indexName).append("=")
				    .append(res)
				    .append(" erstellt für Set mit ID ")
				    .append(s.getId());
			    log.debug(sb.toString());
			}
		    }
		}
	    } catch (XPathExpressionException e) {
		// if (log.isErrorEnabled()) {
		// StringBuilder sb = new StringBuilder();
		// sb.append("Fehler aufgetreten bei Set-ID ")
		// .append(s.getId())
		// .append(". Argumente für Methode: indexName=")
		// .append(indexName).append(" xPathExpression=")
		// .append(xPathExpression);
		// log.error(sb.toString(), e);
		// }
	    }
	}
    }
}
