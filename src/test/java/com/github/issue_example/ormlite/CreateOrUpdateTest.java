package com.github.issue_example.ormlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.issue_example.ormlite.CreateOrUpdateTest.EntityId.entityId;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateOrUpdateTest extends SpringH2Test {

    private Dao<EntityWithStringId, String> entitiesWithStringId;
    private Dao<EntityWithCustomTypeId, EntityId> entitiesWithCustomTypeId;

    @Before
    public void setUp() throws Exception {
        DataPersisterManager.registerDataPersisters(EntityIdType.getInstance());
        TableUtils.createTableIfNotExists(connectionSource, EntityWithStringId.class);
        TableUtils.createTableIfNotExists(connectionSource, EntityWithCustomTypeId.class);
        entitiesWithStringId = DaoManager.createDao(connectionSource, EntityWithStringId.class);
        entitiesWithCustomTypeId = DaoManager.createDao(connectionSource, EntityWithCustomTypeId.class);
    }

    @After
    public void tearDown() throws Exception {
        TableUtils.clearTable(connectionSource, EntityWithStringId.class);
        TableUtils.clearTable(connectionSource, EntityWithCustomTypeId.class);

    }

    @Test
    public void shouldStoreEntityWithStringIdAndLoadFromDbOnCreate() throws Exception {
        // given
        EntityWithStringId data = new EntityWithStringId();
        data.id = "generated_id_from_factory";
        data.value = "some value";
        entitiesWithStringId.create(data);

        // when
        EntityWithStringId found = entitiesWithStringId.queryForId(data.id);

        // when
        assertThat(found.id).isEqualTo("generated_id_from_factory");
        assertThat(found.value).isEqualTo("some value");

    }

    @Test
    public void shouldStoreEntityWithStringIdAndLoadFromDbOnCreateOrUpdate() throws Exception {
        // given
        EntityWithStringId data = new EntityWithStringId();
        data.id = "generated_id_from_factory";
        data.value = "some value";
        entitiesWithStringId.createOrUpdate(data);

        // when
        EntityWithStringId found = entitiesWithStringId.queryForId(data.id);

        // when
        assertThat(found.id).isEqualTo("generated_id_from_factory");
        assertThat(found.value).isEqualTo("some value");

    }

    @Test
    public void shouldStoreEntityWithCustomTypeIdAndLoadFromDbOnCreate() throws Exception {
        // given
        EntityWithCustomTypeId data = new EntityWithCustomTypeId();
        data.id = entityId("generated_id_from_factory");
        data.value = "some value";
        entitiesWithCustomTypeId.create(data);

        // when
        EntityWithCustomTypeId found = entitiesWithCustomTypeId.queryForId(data.id);

        // when
        assertThat(found.id).isEqualTo(entityId("generated_id_from_factory"));
        assertThat(found.value).isEqualTo("some value");

    }

    @Test
    public void shouldStoreEntityWithCustomTypeIdAndLoadFromDbOnCreateOrUpdate() throws Exception {
        // given
        EntityWithCustomTypeId data = new EntityWithCustomTypeId();
        data.id = entityId("generated_id_from_factory");
        data.value = "some value";
        entitiesWithCustomTypeId.createOrUpdate(data);

        // when
        EntityWithCustomTypeId found = entitiesWithCustomTypeId.queryForId(data.id);

        // when
        assertThat(found.id).isEqualTo(entityId("generated_id_from_factory"));
        assertThat(found.value).isEqualTo("some value");

    }

    @DatabaseTable(tableName = "test_entity_string_id")
    static class EntityWithStringId {
        @DatabaseField(generatedId = false, id = true)
        String id;

        @DatabaseField
        String value;
    }

    @DatabaseTable(tableName = "test_entity_custom_type_id")
    static class EntityWithCustomTypeId {
        @DatabaseField(generatedId = false, id = true)
        EntityId id;

        @DatabaseField
        String value;
    }

    static class EntityId {
        private final String value;

        private EntityId(String value) {
            this.value = value;
        }

        static EntityId entityId(String value) {
            return new EntityId(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityId entityId = (EntityId) o;

            if (!value.equals(entityId.value)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static class EntityIdType extends StringType {


        private static final EntityIdType instance = new EntityIdType();

        public static EntityIdType getInstance() {
            return instance;
        }

        private EntityIdType() {
            super(SqlType.STRING, new Class<?>[]{EntityId.class});
        }

        @Override
        public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
            EntityId dateTime = (EntityId) javaObject;
            return dateTime.value;
        }

        @Override
        public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
            return entityId(String.valueOf(sqlArg));
        }
    }


}
