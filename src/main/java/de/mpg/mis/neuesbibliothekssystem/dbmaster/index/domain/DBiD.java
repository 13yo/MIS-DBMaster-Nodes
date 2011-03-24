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
public class DBiD {

    @GraphId
    private Long graphId;

    public Long getGraphId() {
	return this.graphId;
    }

    @Indexed(indexName = "DBiD")
    private Long id;

    public Long getId() {
	return this.id;
    }

    private DBi i;

    private String contentStringDTD;

    private Date dateCreated;

    private Date lastUpdated;

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

    public DBiD(Long id, String dtd, Date dateCreated, Date lastUpdated) {
	super();
	this.id = id;
	// i(i);
	this.contentStringDTD = dtd;
	this.dateCreated = dateCreated;
	this.lastUpdated = lastUpdated;
    }

    /**
     * @return the i
     */
    public DBi getI() {
	Iterable<DBi> i = findAllByTraversal(DBi.class, Traversal.description()
		.relationships(DynamicRelationshipType.withName("DBI"))
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

    public DBiD() {
	super();
    }

    @Transactional
    public void i(DBi dbi) {
	relateTo(dbi, "DBI");
    }
}
