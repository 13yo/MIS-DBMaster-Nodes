/**
 * 
 */
package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.graph.neo4j.finder.FinderFactory;
import org.springframework.data.graph.neo4j.finder.NodeFinder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.neo4j.kernel.Traversal;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBsDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DTOE;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.messaging.DBMasterEndpointImpl;

/**
 * @author tobias kaatz
 * 
 */
@Repository
public class DBRepository {

    private final Log log = LogFactory.getLog(this.getClass());

    private DBMasterEndpointImpl dbmaster;

    /**
     * @param dbmaster
     *            the dbmaster to set
     */
    @Autowired
    public void setDbmaster(DBMasterEndpointImpl dbmaster) {
	this.dbmaster = dbmaster;
    }

    Map<String, List> dtos;

    private ConversionService conversionService;

    @Autowired
    public void setConversionService(ConversionService conversionService) {
	this.conversionService = conversionService;
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

    /**
     * @param gdbs
     *            the gdbs to set
     */
    @Autowired
    public void setGraphDatabaseService(GraphDatabaseService gdbs) {
	this.gdbs = gdbs;
    }

    private GraphDatabaseService gdbs;

    /**
     * @return the dtos
     * @throws Exception
     */
    public Map<String, List> getDtos() throws Exception {
	if (this.dtos == null)
	    this.dtos = new HashMap<String, List>();
	if (this.dtos.size() == 0)
	    this.dtos = this.dbmaster.doSince(new Date(0), DTOE.DBs);
	return this.dtos;
    }

    // @Transactional
    public boolean initMap() {
	try {
	    for (DBsDTO s : (List<DBsDTO>) this.getDtos().get("dtos"))
		conversionService.convert(s, DBs.class);
	    return true;
	} catch (Exception e) {
	    log.error("Fehler beim Konvertieren der DTOs in die DBObjekte", e);
	    return false;
	}
    }

    public Iterable<DBs> getDBsesByDBiF(Long dbifId) {
	if (dbifId == null)
	    throw new IllegalArgumentException("Bitte ID für das DBiF angeben!");

	NodeFinder<DBiF> ifFinder = finderFactory
		.createNodeEntityFinder(DBiF.class);
	NodeFinder<DBs> sFinder = finderFactory
		.createNodeEntityFinder(DBs.class);

	DBiF dbif = this.gdbs.index().existsForNodes("DBiF") ? ifFinder
		.findByPropertyValue("DBiF", "id", dbifId) : null;

	if (dbif == null)
	    throw new IllegalArgumentException(
		    "Index für DBiF nicht existent oder DBiF nicht gefunden!");

	return dbif.findAllByTraversal(
		DBs.class,
		Traversal
			.description()
			.breadthFirst()
			.relationships(
				DynamicRelationshipType.withName("DBiF"),
				Direction.INCOMING).evaluator(new Evaluator() {

			    @Override
			    public Evaluation evaluate(Path path) {
				if (path.length() == 0)
				    return Evaluation.EXCLUDE_AND_CONTINUE;
				if (path.length() == 1)
				    return Evaluation.INCLUDE_AND_CONTINUE;
				return Evaluation.EXCLUDE_AND_PRUNE;
			    }
			}));

    }
}
