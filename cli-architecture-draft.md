# CLI - Architecture Library
to control the TOSCAna software we need a command-line-interface (CLI) which will be integrated in the program-code. Therefore we could use different libraries.

## Considered Alternatives

* library created by ourself
* open source library from apache - ["Commons CLI"](https://commons.apache.org/proper/commons-cli/index.html)
* [args4j](https://github.com/kohsuke/args4j)
* [jopt simple](http://pholser.github.io/jopt-simple/examples.html)
* [JewelCli](http://jewelcli.lexicalscope.com/)


## Conclusion

* *Chosen Alternative: Commons CLI from Apache*

### Commons CLI

* `+` open source
* `+` costumer prefers Commons CLI
* `+` well provided documentation
* `-` could be more powerful than needed


## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
