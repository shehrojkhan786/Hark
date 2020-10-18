package com.hark.websocket.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;

@Profile({"dev","test"})
@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {
 
    
	@Value("${hark.db.cassandra.host}")
	private String cassandraHost;
	
	@Value("${hark.db.cassandra.keyspace}")
	private String cassandraKeyspace;
	
	@Value("${hark.db.cassandra.port}")
	private Integer cassandraPort;
	
	@Override
    protected String getKeyspaceName() {
        return this.cassandraKeyspace;
    }
    
    @Bean
    @Primary
    public CqlSessionFactoryBean cluster() {
    	CqlSessionFactoryBean cluster = new CqlSessionFactoryBean();  	
        cluster.setContactPoints(this.cassandraHost);
        cluster.setPort(this.cassandraPort);
        cluster.setKeyspaceCreations(
        		Arrays.asList(
        				CreateKeyspaceSpecification.createKeyspace(getKeyspaceName())
        				.ifNotExists()
        				.withSimpleReplication(1))
        		);
        cluster.setStartupScripts(Arrays.asList(
        		"USE ebook_chat",
        		"CREATE TABLE IF NOT EXISTS messages (" +
					"username text," +
					"chatRoomId text," +
					"date timestamp," +
					"fromUser text," +
					"toUser text," +
					"text text," +
					"PRIMARY KEY ((username, chatRoomId), date)" +
				") WITH CLUSTERING ORDER BY (date ASC)"
        		)
        );
        return cluster;
    }

    @Bean
    @Override
    public CassandraMappingContext cassandraMapping() 
      throws ClassNotFoundException {
        return new CassandraMappingContext();
    }
}
