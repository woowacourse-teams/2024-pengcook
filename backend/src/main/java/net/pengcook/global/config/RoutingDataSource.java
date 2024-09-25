package net.pengcook.global.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

    public static final String READER_SERVER = "reader";
    public static final String WRITER_SERVER = "writer";

    @Override
    protected Object determineCurrentLookupKey() {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return READER_SERVER;
        }

        return WRITER_SERVER;
    }
}
