package de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.conversion;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;
import org.springframework.data.graph.neo4j.finder.FinderFactory;
import org.springframework.data.graph.neo4j.finder.NodeFinder;
import org.springframework.data.graph.neo4j.template.Neo4jOperations;
import org.springframework.data.graph.neo4j.template.Neo4jTemplate;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.index.domain.*;

import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBiDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBiDDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBiFDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBiFDDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBsDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DBsEDTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DTO;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.dao.DTOE;
import de.mpg.mis.neuesbibliothekssystem.dbmaster.remote.messaging.DBMasterEndpointImpl;

public class DTOtoDBObject implements GenericConverter {

    private Set<ConvertiblePair> convertibles;
    private ConversionService cservice;

    private final Log log = LogFactory.getLog(this.getClass());

    private DBMasterEndpointImpl dbmaster;

    /**
     * @param dbmaster
     *            the dbmaster to set
     */
    public void setDbmaster(DBMasterEndpointImpl dbmaster) {
	this.dbmaster = dbmaster;
    }

    private FinderFactory finderFactory;

    /**
     * @param finderFactory
     *            the finderFactory to set
     */
    public void setFinderFactory(FinderFactory finderFactory) {
	this.finderFactory = finderFactory;
    }

    /**
     * @param gdbs
     *            the gdbs to set
     */
    public void setGdbs(GraphDatabaseService gdbs) {
	this.gdbs = gdbs;
    }

    private GraphDatabaseService gdbs;

    /*
     * Finder für die Entitäten
     */
    NodeFinder<DBi> iFinder;
    NodeFinder<DBiD> idFinder;
    NodeFinder<DBiF> ifFinder;
    NodeFinder<DBiFD> ifdFinder;
    NodeFinder<DBsE> seFinder;
    NodeFinder<DBs> sFinder;

    public DTOtoDBObject(ConversionService cservice) {
	this.convertibles = new HashSet<ConvertiblePair>();
	this.convertibles.add(new ConvertiblePair(DBiDTO.class, DBi.class));
	this.convertibles.add(new ConvertiblePair(DBiDDTO.class, DBiD.class));
	this.convertibles.add(new ConvertiblePair(DBiFDTO.class, DBiF.class));
	this.convertibles.add(new ConvertiblePair(DBiFDDTO.class, DBiFD.class));
	this.convertibles.add(new ConvertiblePair(DBsDTO.class, DBs.class));
	this.convertibles.add(new ConvertiblePair(DBsEDTO.class, DBsE.class));
	// this.convertibles.add(new ConvertiblePair(DeleteLog.class,
	// DTO.class));
	// this.convertibles.add(new ConvertiblePair(DBi.class, DTO.class));
	// this.convertibles.add(new ConvertiblePair(DBiD.class, DTO.class));
	// this.convertibles.add(new ConvertiblePair(DBiF.class, DTO.class));
	// this.convertibles.add(new ConvertiblePair(DBiFD.class, DTO.class));
	// this.convertibles.add(new ConvertiblePair(DBs.class, DTO.class));
	// this.convertibles.add(new ConvertiblePair(DBsE.class, DTO.class));

	// für die Bulk-Überführung in DTOs
	// this.convertibles
	// .add(new ConvertiblePair(Object[].class, DBiDTO.class));
	// this.convertibles
	// .add(new ConvertiblePair(Object[].class, DBiDDTO.class));
	// this.convertibles
	// .add(new ConvertiblePair(Object[].class, DBiFDTO.class));
	// this.convertibles.add(new ConvertiblePair(Object[].class,
	// DBiFDDTO.class));
	// this.convertibles
	// .add(new ConvertiblePair(Object[].class, DBsEDTO.class));
	// this.convertibles
	// .add(new ConvertiblePair(Object[].class, DBsDTO.class));
	this.cservice = cservice;
    }

    public void init() {
	/*
	 * Finder für die Entitäten
	 */
	this.iFinder = finderFactory.createNodeEntityFinder(DBi.class);
	this.idFinder = finderFactory.createNodeEntityFinder(DBiD.class);
	this.ifFinder = finderFactory.createNodeEntityFinder(DBiF.class);
	this.ifdFinder = finderFactory.createNodeEntityFinder(DBiFD.class);
	this.seFinder = finderFactory.createNodeEntityFinder(DBsE.class);
	this.sFinder = finderFactory.createNodeEntityFinder(DBs.class);
    }

    @Override
    public Object convert(Object arg0, TypeDescriptor arg1, TypeDescriptor arg2) {
	if (arg0 == null) {
	    return null;
	}

	try {
	    // Batch-Überführung über Array
	    if (arg1.getName().equals("java.lang.Object[]")) {
		return this.convert((Object[]) arg0,
			Class.forName(arg2.getName()));
	    } else if (Class.forName(arg1.getName()) == DBiDTO.class) {
		return this.convert((DBiDTO) arg0);
	    } else if (Class.forName(arg1.getName()) == DBiDDTO.class) {
		return this.convert((DBiDDTO) arg0);
	    } else if (Class.forName(arg1.getName()) == DBiFDTO.class) {
		return this.convert((DBiFDTO) arg0);
	    } else if (Class.forName(arg1.getName()) == DBiFDDTO.class) {
		return this.convert((DBiFDDTO) arg0);
	    } else if (Class.forName(arg1.getName()) == DBsDTO.class) {
		return this.convert((DBsDTO) arg0);
	    } else if (Class.forName(arg1.getName()) == DBsEDTO.class) {
		return this.convert((DBsEDTO) arg0);
	    } else {
		return null;
	    }
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    @Transactional
    private DBi convert(DBiDTO arg0) {
	log.debug("Converting: " + arg0);
	if (arg0 == null)
	    return null;
	DBi i = gdbs.index().existsForNodes("DBi") ? this.iFinder
		.findByPropertyValue("DBi", "id", arg0.getId()) : null;
	if (i != null)
	    i.remove();
	return new DBi(arg0.getId(), arg0.getI(), arg0.getDescr(),
		arg0.getDateCreated(), arg0.getLastUpdated()).persist();
    }

    @Transactional
    private DBiD convert(DBiDDTO arg0) {
	log.debug("Converting: " + arg0);
	if (arg0 == null)
	    return null;
	DBiD id = gdbs.index().existsForNodes("DBiD") ? this.idFinder
		.findByPropertyValue("DBiD", "id", arg0.getId()) : null;
	if (id != null)
	    id.remove();

	DBi i = gdbs.index().existsForNodes("DBi") ? this.iFinder
		.findByPropertyValue("DBi", "id",
			arg0.getIId() != null ? arg0.getIId() : arg0.getI()
				.getId()) : null;
	if (i == null) {
	    try {
		i = convert(arg0.getIId() != null ? dbmaster.getDTOByID(
			arg0.getIId(), DBiDTO.class) : arg0.getI());
		// i.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von "
				+ arg0.toString(), e);
		return null;
	    }
	}

	id = new DBiD(arg0.getId(), arg0.getContentStringDTD(),
		arg0.getDateCreated(), arg0.getLastUpdated());
	id.persist();

	id.i(i);
	return id;
    }

    //
    @Transactional
    private DBiF convert(DBiFDTO arg0) {
	log.debug("Converting: " + arg0);
	if (arg0 == null)
	    return null;

	DBiF dbif = gdbs.index().existsForNodes("DBiF") ? this.ifFinder
		.findByPropertyValue("DBiF", "id", arg0.getId()) : null;
	if (dbif != null)
	    dbif.remove();

	DBi d = gdbs.index().existsForNodes("DBi") ? this.iFinder
		.findByPropertyValue("DBi", "id",
			arg0.getDId() != null ? arg0.getDId() : arg0.getD()
				.getId()) : null;
	if (d == null) {
	    try {
		d = convert(arg0.getDId() != null ? dbmaster.getDTOByID(
			arg0.getDId(), DBiDTO.class) : arg0.getD());
		// d.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von D "
				+ arg0.getDId(), e);
		return null;
	    }
	}
	DBi g = gdbs.index().existsForNodes("DBi") ? this.iFinder
		.findByPropertyValue("DBi", "id",
			arg0.getGId() != null ? arg0.getGId() : arg0.getG()
				.getId()) : null;
	if (g == null) {
	    try {
		g = convert(arg0.getGId() != null ? dbmaster.getDTOByID(
			arg0.getGId(), DBiDTO.class) : arg0.getG());
		// g.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von G "
				+ arg0.getGId(), e);
		return null;
	    }
	}

	dbif = new DBiF(arg0.getId(), arg0.getF(), arg0.getDescr(),
		arg0.getDateCreated(), arg0.getLastUpdated());
	dbif.persist();

	dbif.d(d);
	dbif.g(g);

	return dbif;
    }

    @Transactional
    private DBiFD convert(DBiFDDTO arg0) {
	log.debug("Converting: " + arg0);
	if (arg0 == null)
	    return null;

	DBiFD ifd = gdbs.index().existsForNodes("DBiFD") ? this.ifdFinder
		.findByPropertyValue("DBiFD", "id", arg0.getId()) : null;
	if (ifd != null)
	    ifd.remove();

	DBiF dbif = gdbs.index().existsForNodes("DBi") ? this.ifFinder
		.findByPropertyValue("DBiF", "id",
			arg0.getDbifId() != null ? arg0.getDbifId() : arg0
				.getDbif().getId()) : null;
	if (dbif == null) {
	    try {
		dbif = convert(arg0.getDbifId() != null ? dbmaster.getDTOByID(
			arg0.getDbifId(), DBiFDTO.class) : arg0.getDbif());
		// dbif.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von "
				+ arg0.toString(), e);
		return null;
	    }
	}

	ifd = new DBiFD(arg0.getId(), arg0.getStrict(), arg0.getStrictmax(),
		arg0.getIgN(), arg0.getIgS(), arg0.getContentStringDTD(),
		arg0.getDateCreated(), arg0.getLastUpdated());
	ifd.persist();

	ifd.dbif(dbif);

	return ifd;
    }

    @Transactional
    private DBs convert(DBsDTO arg0) {
	log.debug("Converting: " + arg0);
	if (arg0 == null)
	    return null;

	DBs s = gdbs.index().existsForNodes("DBs") ? this.sFinder
		.findByPropertyValue("DBs", "id", arg0.getId()) : null;
	if (s != null)
	    s.remove();

	DBiF dbif = gdbs.index().existsForNodes("DBi") ? this.ifFinder
		.findByPropertyValue("DBiF", "id",
			arg0.getDbifId() != null ? arg0.getDbifId() : arg0
				.getDbif().getId()) : null;
	if (dbif == null) {
	    try {
		dbif = convert(arg0.getDbifId() != null ? dbmaster.getDTOByID(
			arg0.getDbifId(), DBiFDTO.class) : arg0.getDbif());
		// dbif.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von "
				+ arg0.toString(), e);
		return null;
	    }
	}

	DBsE dbsed = gdbs.index().existsForNodes("DBi") ? this.seFinder
		.findByPropertyValue("DBsE", "id",
			arg0.getDbsedId() != null ? arg0.getDbsedId() : arg0
				.getDbsed().getId()) : null;
	if (dbsed == null) {
	    try {
		dbsed = convert(arg0.getDbsedId() != null ? dbmaster
			.getDTOByID(arg0.getDbsedId(), DBsEDTO.class) : arg0
			.getDbsed());
		// dbsed.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von "
				+ arg0.toString(), e);
		return null;
	    }
	}
	DBsE dbseg = gdbs.index().existsForNodes("DBi") ? this.seFinder
		.findByPropertyValue("DBsE", "id",
			arg0.getDbsegId() != null ? arg0.getDbsegId() : arg0
				.getDbseg().getId()) : null;
	if (dbseg == null) {
	    try {
		dbseg = convert(arg0.getDbsegId() != null ? dbmaster
			.getDTOByID(arg0.getDbsegId(), DBsEDTO.class) : arg0
			.getDbseg());
		// dbseg.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von "
				+ arg0.toString(), e);
		return null;
	    }
	}

	s = new DBs(arg0.getId(), arg0.getXmlString(), arg0.getDateCreated(),
		arg0.getLastUpdated());
	s.persist();

	s.dbif(dbif);
	s.dbsed(dbsed);
	s.dbseg(dbseg);

	return s;
    }

    @Transactional
    private DBsE convert(DBsEDTO arg0) {
	log.debug("Converting: " + arg0);
	if (arg0 == null)
	    return null;

	DBsE se = gdbs.index().existsForNodes("DBsE") ? this.seFinder
		.findByPropertyValue("DBsE", "id", arg0.getId()) : null;
	if (se != null)
	    se.remove();

	DBi i = gdbs.index().existsForNodes("DBi") ? this.iFinder
		.findByPropertyValue("DBi", "id",
			arg0.getIId() != null ? arg0.getIId() : arg0.getI()
				.getId()) : null;
	if (i == null) {
	    try {
		i = convert(arg0.getIId() != null ? dbmaster.getDTOByID(
			arg0.getIId(), DBiDTO.class) : arg0.getI());
		// i.persist();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		log.error(
			"Fehler beim Nachladen der DTO-Infos von "
				+ arg0.toString(), e);
		return null;
	    }
	}

	se = new DBsE(arg0.getId(), arg0.getS(), arg0.getDateCreated(),
		arg0.getLastUpdated());
	se.persist();

	se.i(i);
	return se;
    }

    private <T> T convert(Object[] a, Class<T> dtoType) {
	if (dtoType.getSimpleName().equals("DBiDTO")) {
	    checkArrayLength(a, 5);
	    return (T) new DBiDTO((Long) a[0], null, (Long) a[1],
		    (String) a[2], (Date) a[3], (Date) a[4]);

	}

	if (dtoType.getSimpleName().equals("DBiDDTO")) {
	    checkArrayLength(a, 5);
	    return (T) new DBiDDTO((Long) a[0], null, (Long) a[1],
		    (String) a[2], (Date) a[3], (Date) a[4]);
	}

	if (dtoType.getSimpleName().equals("DBiFDTO")) {
	    checkArrayLength(a, 7);
	    return (T) new DBiFDTO((Long) a[0], null, (Long) a[1], (Long) a[2],
		    (Long) a[3], (String) a[4], (Date) a[5], (Date) a[6]);
	}

	if (dtoType.getSimpleName().equals("DBiFDDTO")) {
	    checkArrayLength(a, 9);
	    return (T) new DBiFDDTO((Long) a[0], null, (Long) a[1],
		    (String) a[2], (Integer) a[3], (Integer) a[4],
		    (Integer) a[5], (Integer) a[6], (Date) a[7], (Date) a[8]);
	}

	if (dtoType.getSimpleName().equals("DBsEDTO")) {
	    checkArrayLength(a, 5);
	    return (T) new DBsEDTO((Long) a[0], null, (Long) a[1], (Long) a[2],
		    (Date) a[3], (Date) a[4]);
	}

	if (dtoType.getSimpleName().equals("DBsDTO")) {
	    checkArrayLength(a, 7);

	    return (T) new DBsDTO((Long) a[0], null, (Long) a[1], (Long) a[2],
		    (Long) a[3], (String) a[4], (Date) a[5], (Date) a[6]);
	}

	return null;
    }

    /**
     * 
     * @param arg0
     *            Object-Array mit den zu konvertierenden Daten
     * @param length
     * @throws IlleaglArgumentException
     *             wenn Anzahl der Array-Elemente nicht mit der benötigten
     *             Argumentanzahl für die Konverierung übereinstimmt
     */
    private void checkArrayLength(Object[] arg0, int length) {
	if (arg0.length != length) {
	    throw new IllegalArgumentException(
		    "Die Anzahl der übergebenen Elemente von Object[] ("
			    + arg0.length
			    + ") stimmt nicht mit dem Zieltyp überein!");
	}
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
	return convertibles;
    }

}
