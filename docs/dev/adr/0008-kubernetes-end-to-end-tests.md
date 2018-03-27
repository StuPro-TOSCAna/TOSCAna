# How to perform Kubernetes end to end tests

To verify that the transformator correctly transforms to valid kubernetes artifacts we have to perform end to end tests.

## Considered Alternatives

-   [fabric8io/fabric8/components/kubernetes-assertions](https://github.com/fabric8io/fabric8/tree/master/components/kubernetes-assertions)
-   Shell scripts

## Decision Outcome

-   Chosen Alternative: [fabric8io/fabric8/components/kubernetes-assertions](https://github.com/fabric8io/fabric8/tree/master/components/kubernetes-assertions)

## Pros and Cons of the Alternatives <!-- optional -->

### [fabric8io/fabric8/components/kubernetes-assertions](https://github.com/fabric8io/fabric8/tree/master/components/kubernetes-assertions)

-   `+` easy to integrate into current test suite
-   `+` easy to use
-   `+` provides functionality we need
-   `-` external dependency

### Shell scripts

-   `+` No external dependencies
-   `-` Time consuming test writing
-   `-` Effort to integrate in current test suite
-   `-` Mistakes in the shell tests
