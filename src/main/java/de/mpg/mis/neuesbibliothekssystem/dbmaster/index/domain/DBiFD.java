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
public class DBiFD {

    @GraphId
    private Long graphId;

    public Long getGraphId() {
	return this.graphId;
    }

    @Indexed(indexName = "DBiFD")
    private Long id;

    public Long getId() {
	return this.id;
    }

    private DBiF dbif;

    private String contentStringDTD;

    private Integer strict;

    private Integer strictmax;

    private Integer igN;

    private Integer igS;

    private Date dateCreated;

    private Date lastUpdated;

    public DBiFD() {
	super();
    }

    public DBiFD(Long id, Integer s, Integer sm, Integer ign, Integer igs,
	    String dtd, Date dateCreated, Date lastUpdated) {
	this.id = id;
	// dbif(dbif);
	this.strict = s;
	this.strictmax = sm;
	this.igN = ign;
	this.igS = igs;
	this.contentStringDTD = dtd;
	this.dateCreated = dateCreated;
	this.lastUpdated = lastUpdated;
    }

    /**
     * @return the dbif
     */
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
     * @return the contentStringDTD
     */
    public String getContentStringDTD() {
	return contentStringDTD;
    }

    /**
     * @return the strict
     */
    public Integer getStrict() {
	return strict;
    }

    /**
     * @return the strictmax
     */
    public Integer getStrictmax() {
	return strictmax;
    }

    /**
     * @return the igN
     */
    public Integer getIgN() {
	return igN;
    }

    /**
     * @return the igS
     */
    public Integer getIgS() {
	return igS;
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
    public void dbif(DBiF dbif) {
	relateTo(dbif, "DBIF");
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
}
