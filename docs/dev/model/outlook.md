# Limitations

#### TODO

# Outlook

In theory, the current approach of providing a java interface for TOSCA types is flexible enough to take the idea one step further: Automatic code generation. Starting with a TOSCA type definition, an engine could generate the corresponding TOSCA element class (source file) on its own.

Steps the engine would have to consider:

- choose the appropriate base class to inherit from
- convert entries to appropriate ToscaKeys
- Generate getter and setter

Due to the structure of the EffectiveModel, no further conversion code has to be written when adding a new type class.
