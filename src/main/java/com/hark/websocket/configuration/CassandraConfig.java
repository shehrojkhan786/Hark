package com.hark.websocket.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;

import lombok.Data;

@Profile({"dev","test"})
@Configuration
@Data
public class CassandraConfig extends AbstractCassandraConfiguration {
	
	private final String localDataCenter;
	private final String hosts;
	private final String entityBasePackage;
	private final String keyspace;
    
	
		
	CassandraConfig(
		      @Value("${spring.data.cassandra.local-datacenter}") String localDataCenter,
		      @Value("${spring.data.cassandra.entity-base-package}") String entityBasePackage,
		      @Value("${spring.data.cassandra.contact-points}") String hosts,
		      @Value("${spring.data.cassandra.keyspace-name}") String keyspace) {
			this.entityBasePackage = entityBasePackage;
		    this.localDataCenter = localDataCenter;
		    this.hosts = hosts;
		    this.keyspace = keyspace;
		}
	
	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.CREATE_IF_NOT_EXISTS;
	}
	
	@Override
	public List<CreateKeyspaceSpecification> getKeyspaceCreations() {
		CreateKeyspaceSpecification specification = CreateKeyspaceSpecification.createKeyspace(getKeyspaceName())
				.with(KeyspaceOption.DURABLE_WRITES, true)
				.ifNotExists();
		
		return Arrays.asList(specification);
	}
	
	@Override
	protected String getKeyspaceName() {
		return this.keyspace;
	}

	@Override
	protected String getLocalDataCenter() {
	    return this.localDataCenter;
	}
	
	@Override
	protected String getContactPoints() {
	    return this.hosts;
	}
	
	 @Override
	  protected List<String> getStartupScripts() {
	    final String script =
	        "CREATE KEYSPACE IF NOT EXISTS "
	            + getKeyspaceName()
	            + " WITH durable_writes = true"
	            + " AND replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};";
	    final String script1 = "USE ebook_chat";
	    final String script2 = "CREATE TABLE IF NOT EXISTS messages (" + "username text," +
	    		"chatRoomId text," + "date timestamp," + "fromUser text," + "toUser text," +
	    		"text text," + "PRIMARY KEY ((username, chatRoomId), date)" +
	    		") WITH CLUSTERING ORDER BY (date ASC)" ;
	    return Arrays.asList(script,script1,script2);
	  }

	  @Override
	  protected List<String> getShutdownScripts() {
	    return List.of("DROP KEYSPACE IF EXISTS " + getKeyspaceName() + ";");
	  }


    
	/*
	 * @Bean
	 * 
	 * @Primary public CqlSessionFactoryBean cluster() { CqlSessionFactoryBean
	 * cluster = new CqlSessionFactoryBean();
	 * cluster.setContactPoints(this.cassandraHost);
	 * cluster.setPort(this.cassandraPort);
	 * //cluster.setLocalDatacenter("datacenter1"); cluster.setKeyspaceCreations(
	 * Arrays.asList( CreateKeyspaceSpecification.createKeyspace(getKeyspaceName())
	 * .ifNotExists() .withSimpleReplication(1)) );
	 * cluster.setStartupScripts(Arrays.asList( "USE ebook_chat",
	 * "CREATE TABLE IF NOT EXISTS messages (" + "username text," +
	 * "chatRoomId text," + "date timestamp," + "fromUser text," + "toUser text," +
	 * "text text," + "PRIMARY KEY ((username, chatRoomId), date)" +
	 * ") WITH CLUSTERING ORDER BY (date ASC)" ) ); return cluster; }
	 */

    @Bean
    @Override
    public CassandraMappingContext cassandraMapping() 
      throws ClassNotFoundException {
        return new CassandraMappingContext();
    }
}
