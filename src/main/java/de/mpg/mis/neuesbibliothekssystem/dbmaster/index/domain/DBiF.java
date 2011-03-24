package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain;

import java.util.Date;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.kernel.Traversal;
import org.springframework.data.annotation.Indexed;
import org.springframework.data.graph.annotation.GraphId;
import org.springframework.data.graph.annotation.NodeEntity;
import org.springframework.transaction.annotation.Transactional;

@NodeEntity
public class DBiF {

    @GraphId
    private Long graphId;

    public Long getGraphId() {
	return this.graphId;
    }

    @Indexed(indexName = "DBiF")
    private Long id;

    public Long getId() {
	return this.id;
    }

    private DBi d;

    private DBi g;

    private Long f;

    /**
     * @return the d
     */
    public DBi getD() {
	Iterable<DBi> i = findAllByTraversal(DBi.class, Traversal.description()
		.relationships(DynamicRelationshipType.withName("DBID"))
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
     * @return the g
     */
    public DBi getG() {
	Iterable<DBi> i = findAllByTraversal(DBi.class, Traversal.description()
		.relationships(DynamicRelationshipType.withName("DBIG"))
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
     * @return the f
     */
    public Long getF() {
	return f;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
	return descr;
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

    private String descr;

    private Date dateCreated;

    private Date lastUpdated;

    // public DBiF(Long d, Long g, Long f, String description) {
    // super();
    // this.d = DBi.findDBi(d);
    // this.g = DBi.findDBi(g);
    // this.f = f;
    // this.descr = description;
    // }

    public DBiF(Long id, Long f, String description, Date dateCreated,
	    Date lastUpdated) {
	super();
	this.id = id;
	// d(d);
	// g(g);
	this.f = f;
	this.descr = description;
	this.dateCreated = dateCreated;
	this.lastUpdated = lastUpdated;
    }

    public DBiF() {
	super();
    }

    @Transactional
    public void d(DBi dbi) {
	relateTo(dbi, "DBID");
    }

    @Transactional
    public void g(DBi dbi) {
	relateTo(dbi, "DBIG");
    }

    // @PreUpdate
    // private void updateValues() {
    // this.lastUpdated = new Date();
    // }
    //
    // @PrePersist
    // private void persistValues() {
    // this.dateCreated = new Date();
    // this.lastUpdated = new Date();
    // }
    //
    // public static Query findDBiFsByLastUpdatedGreaterThanEquals(Date
    // lastUpdated) {
    // if (lastUpdated == null)
    // throw new IllegalArgumentException(
    // "The lastUpdated argument is required");
    // EntityManager em = DBiF.entityManager();
    // Query q = em
    // .createQuery("SELECT DBiF FROM DBiF AS dbif WHERE dbif.lastUpdated >= :lastUpdated");
    // q.setParameter("lastUpdated", lastUpdated);
    // return q;
    // }
}
