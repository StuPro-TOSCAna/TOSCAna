# Iterate EffectiveModel with visitor pattern

**User Story:**

The EffectiveModel graph contains nodes of arbitrary type. How can a consumer iterate the nodes without loosing type information?

## Considered Alternatives

* Use instanceof
* Use double dispatch of visitor pattern

## Decision Outcome

* Chosen Alternative: Use double dispatch of visitor pattern
 
Comes out best - see above.

## Pros and Cons of the Alternatives <!-- optional -->

### Use instance

Use a combination of `instanceof` and switch/if statements whenever you want to iterate the nodes.
```java
if (node instanceof WebServer){
    doSomething(node)
}
```

* `+` straightforward 
* `-` really ugly, boilerplate

### Use double dispatch of visitor pattern

* `+` clean
* `+` extendable with use of java 8 default methods in interfaces
* `+` code reuse is easier 
* `-` makes architecture a bit more complex
* `-` boilerplate in tosca elements: every class needs an accept() method
