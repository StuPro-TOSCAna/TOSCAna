## Use a Library for the CLI
To control the TOSCAna software we need a command-line-interface (CLI) which will be integrated in the program code. Therefore we could use a library.

## Considered Alternatives

* Library created by ourself
* [Commons CLI](https://commons.apache.org/proper/commons-cli/index.html)
* [args4j](https://github.com/kohsuke/args4j)
* [JCommander](https://github.com/cbeust/jcommander)
* [jopt simple](http://pholser.github.io/jopt-simple/examples.html)
* [JewelCli](http://jewelcli.lexicalscope.com/)
* [Picocli](https://github.com/remkop/picocli)


## Conclusion

* Chosen Alternative: *Picocli*
* supports everything we need to create a CLI like CloudFoundry/Git

## Pros and Cons of the Alternatives

### Commons Cli

* `+` Open Source
* `+` Customer suggested it
* `+` good Documentation
* `-` no Subcommands supported

### JCommander

* `+` Open Source
* `+` Customer suggested it
* `+` good Documentation
* `+` Subcommands supported
* `-` no customized Usage messages

### Picocli

* `+` Open Source
* `+` Customer suggested it
* `+` supports ANSI Colors and Styles
* `+` good Documentation
* `+` support of Subcommands, nested Subcommands and positional Parameters
* `+` customized Usage messages
* `+` POSIX clustered short Options
* `+` Autocomplete feature
* `-` pretty new


## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html