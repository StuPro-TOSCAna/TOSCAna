# Implement own tosca parser

**User Story:** 

>Abstract: The [eclipse winery project](https://github.com/eclipse/winery) provides a winery yaml parser. Should we reuse this parser or implement our own?

At first, we integrated the winery parser into TOSCAna.
However, we've quickly realized that using the resulting data structure, the `TServiceTemplate`, felt inconvenient. The obvious reason for this: Its data structure was designed around the tosca syntax (winery is a modelling tool for TOSCA), not around its semantics. For example, data access is not type-safe, and much casting has to be made when handling a `TServiceTemplate`. In addition, TOSCA symbolic names are not resolved.

So we rolled our own data structure (mainly quite simple POJOs) named `EffectiveModel` (whose design focused on semantics of the template). Additionally, we built a converter which converts a `TServiceTemplate` into an `EffectiveModel`.

However, when trying to implement automatic resolving of TOSCA intrinsic functions (like `get_attribte`), we soon realized that we need a single graph as backbone of our `EffectiveModel`. Otherwise, keeping the model in sync with arbitrary linked data would have been at least a hack, at worst a mess. The POJOs would now access this graph and not store their values on their own. The vision of the `ServiceGraph` was born.

At some point (christmas '17) we felt that the current workflow was kind of complicated: Why do we first force the YAML structure (which basically is a graph) into the shape of a `TServiceTemplate` and afterwards construct a graph from there?  
Wouldn't it be much simpler to directly use `SnakeYAML` (the tool the winery uses internally) to parse the YAML template and directly populate the `ServiceGraph`?
Note that at this point, the `ServiceGraph` and the logic which would convert the `TServiceTemplate` into the `ServiceGraph` was not yet implemented.

## Considered Alternatives

* Use winery yaml parser
* Implement own parser

## Decision Outcome

* Chosen Alternative: Implement own parser

Comes out best - see above. It was a very tough decision.

>Retrospective: After ditching the conversion code, adding the custom parser and implementing all aspects related to the `EffectiveModel` and `ServiceGraph`, we ended up having a smaller code base than before. Turned out that parsing the template was done in ~30 loc, handling short notation took ~100 loc.

Note that we still use the winery parser for syntax validation before using our own parser.

## Pros and Cons of the Alternatives <!-- optional -->

### Use winery yaml parser
* `+` parsing logic is already implemented (though converter logic still needs to get written!)
* `+` already supports handling of both short and extended notations (variants in TOSCA syntax)
* `-` visitor and builder pattern make winery parser inconvenient to work with
* `-` the winery parser is not well tested (or was, back in January '18) and occuring bugs would require a deep dive into a undocumented, foreign codebase
* `-` the `TServiceTemplate` could change in future revisions of the winery; it's not a lean interface

### Implement own parser
* `-` quite some effort
* `-` hard to estimate how hard it really is - maybe it's harder than we think, did we miss some aspects?
* `+` we can get rid of the converter logic - less code is always better
