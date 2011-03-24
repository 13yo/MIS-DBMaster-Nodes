package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.kernel.Traversal;
import org.springframework.data.annotation.Indexed;
import org.springframework.data.graph.annotation.GraphId;
import org.springframework.data.graph.annotation.NodeEntity;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.validation.DBiFDBsE;

@DBiFDBsE
@NodeEntity
public class DBs {

    @GraphId
    private Long graphId;

    public Long getGraphId() {
	return this.graphId;
    }

    @Indexed(indexName = "DBs")
    private Long id;

    public Long getId() {
	return this.id;
    }

    private DBiF dbif;

    private DBsE dbsed;

    private DBsE dbseg;

    private String xmlString;

    private Date dateCreated;

    private Date lastUpdated;

    public DBs() {
	super();
    }

    public DBs(Long id, Date dateCreated, Date lastUpdated) {
	super();
	this.id = id;
	// dbif(dbif);
	// dbsed(dbsed);
	// dbseg(dbseg);
	this.dateCreated = dateCreated;
	this.lastUpdated = lastUpdated;
    }

    public DBs(Long id, String xml, Date dateCreated, Date lastUpdated) {
	super();
	this.id = id;
	// dbif(dbif);
	// dbsed(dbsed);
	// dbseg(dbseg);
	this.xmlString = xml;
	this.dateCreated = dateCreated;
	this.lastUpdated = lastUpdated;
    }

    /**
     * @return the dbif
     */
    @Transactional
    public DBiF getDbif() {
	Iterable<DBiF> i = findAllByTraversal(
		DBiF.class,
		Traversal
			.description()
			.relationships(DynamicRelationshipType.withName("DBIF"))
			.breadthFirst().evaluator(new Evaluator() {

			    @Override
			    public Evaluation evaluate(Path path) {
				if (path.length() == 1)
				    return Evaluation.INCLUDE_AND_PRUNE;
				else
				    return Evaluation.EXCLUDE_AND_PRUNE;
			    }
			}));

	return i.iterator().next();
    }

    /**
     * @return the dbsed
     */
    @Transactional
    public DBsE getDbsed() {
	Iterable<DBsE> i = findAllByTraversal(
		DBsE.class,
		Traversal
			.description()
			.relationships(
				DynamicRelationshipType.withName("DBSED"))
			.breadthFirst().evaluator(new Evaluator() {

			    @Override
			    public Evaluation evaluate(Path path) {
				if (path.length() == 1)
				    return Evaluation.INCLUDE_AND_PRUNE;
				else
				    return Evaluation.EXCLUDE_AND_PRUNE;
			    }
			}));

	return i.iterator().next();
    }

    /**
     * @return the dbseg
     */
    @Transactional
    public DBsE getDbseg() {
	Iterable<DBsE> i = findAllByTraversal(
		DBsE.class,
		Traversal
			.description()
			.relationships(
				DynamicRelationshipType.withName("DBSEG"))
			.breadthFirst().evaluator(new Evaluator() {

			    @Override
			    public Evaluation evaluate(Path path) {
				if (path.length() == 1)
				    return Evaluation.INCLUDE_AND_PRUNE;
				else
				    return Evaluation.EXCLUDE_AND_PRUNE;
			    }
			}));

	return i.iterator().next();
    }

    /**
     * @return the xmlString
     */
    public String getXmlString() {
	return xmlString;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
	return dateCreated;
    }

    /**
     * @return the lastUpdated
     */
    public Date getLastUpdated() {
	return lastUpdated;
    }

    @Transactional
    public void dbsed(DBsE dbse) {
	relateTo(dbse, "DBSED");
    }

    @Transactional
    public void dbseg(DBsE dbse) {
	relateTo(dbse, "DBSEG");
    }

    @Transactional
    public void dbif(DBiF dbif) {
	relateTo(dbif, "DBIF");
    }

    // @PreUpdate
    // private void updateValues() {
    // }
    //
    // @PrePersist
    // private void persistValues() {
    // this.dateCreated = new Date();
    // this.lastUpdated = new Date();
    // }
}
