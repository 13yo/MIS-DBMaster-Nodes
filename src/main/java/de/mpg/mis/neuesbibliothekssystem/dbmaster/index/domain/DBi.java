package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain;

import java.util.Date;

import org.springframework.data.annotation.Indexed;
import org.springframework.data.graph.annotation.GraphId;
import org.springframework.data.graph.annotation.NodeEntity;

@NodeEntity
public class DBi {

    @GraphId
    private Long graphId;

    public Long getGraphId() {
	return this.graphId;
    }

    @Indexed(indexName = "DBi")
    private Long id;

    public Long getId() {
	return this.id;
    }

    private Long i;

    private String descr;

    private Date dateCreated;

    private Date lastUpdated;

    public DBi() {
	super();
    }

    public DBi(Long id, Long i, String descr, Date dateCreated, Date lastUpdated) {
	super();
	this.id = id;
	this.i = i;
	this.descr = descr;
	this.dateCreated = dateCreated;
	this.lastUpdated = lastUpdated;
    }

    /**
     * @return the i
     */
    public Long getI() {
	return i;
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
