## EffectiveModel implemenation

> HOWTO USE THE MODEL


#### Construction via builder
Most of the classes in the EffectiveModel use the builder pattern. This is how it works:

1. Construct instance of class `Foo`: 
```java
Foo foo = Foo.builder().build();
```
2. Construct instance of class `Foo` and set its optional field `bar` to `"bar"`:
```java
Foo foo = Foo.builder().bar("bar").build();
```
3. Construct instance of class `Foo`, which only requires field `bar` as mandatory parameter:
```java
Foo foo = Foo.builder("bar").build();
```
`Foo` can never be constructed without all mandatory parameters, making it impossible to construct invalid instances of `Foo`.
4. Passing `null` as a value for a required field results in a `NullPointerException`. If not, please report the bug.
4. // TODO supplying Builder with illegal parameters throws which exceptions? + supplying null is always wrong

#### Getting values
1. Getters will never return null.
2. If value in instance is null, corresponding getter will return an Optional.

If you encounter different behaviour please file a bug report.

## Possible shortcomings
#### Immutability
The implementation enforces immutability of every instance to its best. If, however, you have problems dealing with these immutable instances (e.g., you have to often change single fields of instances after their instantiation), please open an issue. We might need to get rid of some `final`'s and introduce setters.
#### Builder as parameters
In some classes the builder needs another builder as argument. This has to do with the harsh immutability enforcement. Should maybe get changed.


## Bugs
There are many.
