# TOSCA Elements

Whenever the term `TOSCA Element` is mentioned, it refers to one of the classes which are used to model TOSCA types, e.g., `WebServer`, `ContainerCapability`, `Requirement` or `HostedOn`.

### TOSCA Elements are only wrappers 
When interacting with a TOSCA element, e.g. with an instance of the `MysqlDatabase` class, it seems its implemented like a plain POJO class:

```java
String dbName = node.getDatabaseName();
node.setDatabaseName("my-db");
```

However, this appearance is deceiving.

The actual implementation of most getters / setters of TOSCA elements look like this:
```java
public String getDatabaseName() {
    return get(DATABASE_NAME);
}

public Database setDatabaseName(String databaseName) {
    set(DATABASE_NAME, databaseName);
    return this;
}
```

> Note: You don't have to type these getters / setters by hand. Instead, use these [code templates](code-templates.md) (if you happen to run intellij). The templates will generate getters / setters based on the defined [`ToscaKey`](#toscakeys) fields.

The above used generic `get()` and `set()` methods are implemented in the `BaseToscaElement` class and look like this:
```java
public <T> T get(ToscaKey<T> key) {
    return mappingEntity.getValue(key);
}

public <T> void set(ToscaKey<T> key, T value) {
   mappingEntity.setValue(key, value);
}
```

So whats basically happening? Generally speaking, all data access is delegated to its underlying `MappingEntity`. Read about the [ServiceGraph](servicegraph.md) to understand how this works.
The truth is: TOSCA Element instances are simple and stupid wrappers.

### ToscaKeys
As you might have noticed, the generic `get()` and `set()` methods expect a `ToscaKey` instance. As a whole, a `ToscaKey` describes all **relevant characteristics** of a value belonging to the TOSCA element.   

As a general rule, every Tosca Element defines its own ToscaKeys, similar to a POJO defining its data fields.
This could look like this:

```java
public static ToscaKey<String> DATABASE_NAME = new ToscaKey<>(PROPERTIES, "name").required();

```

A ToscaKey has following characteristics:

- **Type information**. Special care has been taken to achieve type-safety even with a generic approach for getters / setters. A `ToscaKey` contains both generic type information and class type information (because generic type information is erased at runtime, this is needed as well).
- **Name**. The name which is used in *TOSCA yaml* to refer to this element
- *required* boolean flag which specifies if a value must be present
- **predecssor**. Another `ToscaKey` which acts as the predecessor of this `ToscaKey`. With defining a predecessor it's possible to map the Key to a nested structure. E.g., above defined key has the predecessor `PROPERTIES` (which happens to have the name `"properties"`) - so it points to a value in `properties.name`.
- **directives**. When needed, special directives can be saved in a `Map<String,Object>` construction. This is currently used for storing information about default units of TOSCA scalar types.

> *Note: `ToscaKeys` are not declared as `final`, as this would cause problems in combination with inheritance (which is used heavily within Tosca Element classes)*


#### RequirementKeys
A `RequirementKey` is a special `ToscaKey` which allows storage of multiple type information instead of only one. 
As the name implies, shall be used for defining TOSCA requirements. 
This allows for type-safe handling of its correlated node, capability and relationship.
