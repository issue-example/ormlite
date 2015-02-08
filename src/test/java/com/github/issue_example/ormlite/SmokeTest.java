package com.github.issue_example.ormlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SmokeTest extends SpringH2Test {

    private Dao<Entity, Long> entities;

    @Before
    public void setUp() throws Exception {
        TableUtils.createTableIfNotExists(connectionSource, Entity.class);
        entities = DaoManager.createDao(connectionSource, Entity.class);
    }

    @After
    public void tearDown() throws Exception {
        TableUtils.clearTable(connectionSource, Entity.class);

    }

    @Test
    public void shouldStoreObjectAndLoadFromDb() throws Exception {
        // given
        Entity data = new Entity();
        data.value = "some value";
        entities.create(data);

        // when
        Entity found = entities.queryForId(data.id);

        // when
        assertThat(found.id).isNotNull();
        assertThat(found.value).isEqualTo("some value");

    }

    @DatabaseTable(tableName = "test_entity")
    static class Entity {
        @DatabaseField(generatedId = true)
        Long id;

        @DatabaseField
        String value;
    }
}
