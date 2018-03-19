# TOSCA Elements

When interacting with a TOSCA element, for example with an instance of the `MysqlDatabase` class, it seems its implementation is a simple POJO class:

```java
String dbName = node.getDatabaseName();
node.setPort(12345);
```

However, this appearance is deceiving.

Every node, capability and relationship class derives from the common base class `BaseToscaElement`.

```java
public <T> T get(ToscaKey<T> key) {
    T value = mappingEntity.getValue(key);
    return value;
}

public <T> void set(ToscaKey<T> key, T value) {
   mappingEntity.setValue(key, value);
}
```
