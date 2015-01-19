package com.gene.app.util;

import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

public class ProjectSequenceGeneratorUtil {
	public static final String PROJECT_SEQUENCE = "ProjectSequence";
    public static final String PROJECT_SEQUENCE_PARENT = "ProjectSequenceParent";
    private static final String SEQUENCE = "sequence";
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    private final static Logger LOG = Logger.getLogger(ProjectSequenceGeneratorUtil.class.getSimpleName());
    
    public synchronized long nextValue() {
        Entity sequenceEntity = getSequence();
        long val = extractSequenceValue(sequenceEntity);
        incrementSequence(sequenceEntity, ++val);
        return val;
    }
    
    public long actualValue() {
        Entity sequenceEntity = getSequence();
        long val = extractSequenceValue(sequenceEntity);
        return val;
    }
    
    public void setActualSequence(final Long sequence) {
        Transaction txn = datastore.beginTransaction();
        try {
            Entity entity = getSequenceEntity();
            entity.setProperty(SEQUENCE, sequence);
            datastore.put(txn, entity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
                LOG.severe("ProjectSequenceGeneratorImpl.setActualSequence , transaction rollback.");
            }
        }

    }

    private Entity getSequence() {
        Entity sequenceEntity = getSequenceEntity();
        if (sequenceEntity == null) {
            createProjectSequence();
            sequenceEntity = getSequenceEntity();
        }
        return sequenceEntity;
    }

    private Entity getSequenceEntity() {
        Key ancestor = KeyFactory.createKey(PROJECT_SEQUENCE_PARENT, 1);
        Query query = new Query(PROJECT_SEQUENCE, ancestor);   //ancestor query doesn't need trans, cause it apply
        //any outstanding modifications before executing
        Entity sequenceEntity = datastore.prepare(query).asSingleEntity();
        return sequenceEntity;
    }

    private void createProjectSequence() {
        Transaction txn = datastore.beginTransaction();
        try {
            Key ancestor = KeyFactory.createKey(PROJECT_SEQUENCE_PARENT, 1);
            Entity projectSequence = new Entity(PROJECT_SEQUENCE, ancestor);
            projectSequence.setProperty(SEQUENCE, 1000000000L);
            datastore.put(txn, projectSequence);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
                LOG.severe("ProjectSequenceGeneratorImpl.createProjectSequence , transaction rollback.");
            }
        }
    }

    private long extractSequenceValue(Entity entity) {
        Object sequenceValue = entity.getProperty(SEQUENCE);
        if (sequenceValue != null) {
            String sequenceStringValue = String.valueOf(sequenceValue);
            return Long.valueOf(sequenceStringValue);
        }
        throw new IllegalStateException("ProjectSequenceGeneratorImpl.extractSequenceValue Entity: " + entity);
    }

    private void incrementSequence(Entity entity, long val) {
        Transaction txn = datastore.beginTransaction();
        try {
            entity.setProperty(SEQUENCE, val);
            datastore.put(txn, entity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
                LOG.severe("ProjectSequenceGeneratorImpl.incrementSequence , transaction rollback, " +
                        "objects state: \n" +
                        "entity: " + entity + "\n" +
                        "val: " + val
                );
            }
        }
    }
}
