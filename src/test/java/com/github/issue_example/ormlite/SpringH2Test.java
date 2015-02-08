package com.github.issue_example.ormlite;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringH2Test.Config.class)
public abstract class SpringH2Test {

    public interface Profiles {
        String EMBEDDED_H2 = "embedded-h2";
        String REAL_H2 = "real-h2";
    }

    @Autowired
    protected ConnectionSource connectionSource;

    @Configuration
    static class Config {
        @Bean(destroyMethod = "shutdown")
        EmbeddedDatabase dataSource() {
            return new EmbeddedDatabaseBuilder().
                    setType(EmbeddedDatabaseType.H2).
                    build();
        }

        @Bean
        ConnectionSource embeddedConnectionSource(EmbeddedDatabase embeddedDatabase) throws SQLException {
            return new DataSourceConnectionSource(embeddedDatabase, embeddedDatabase.getConnection().getMetaData().getURL());
        }

    }
}