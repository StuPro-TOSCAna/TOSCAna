# Intellij Code Templates

### Effective Model

In order to ease the implementation of the EffectiveModel, these code templates for getters / setters might come in handy. They may be used in all subclasses of `BaseToscaElement.java` instead of the default intellij getter / setter templates.

##### Tosca Getter
```
/**
@return {@link #$field.name}

*/
#set( $camelCaseName = "" )
#set( $part = "" )
#foreach($part in $field.name.split("_"))
    #set( $camelCaseName = "${camelCaseName}$part.substring(0,1).toUpperCase()$part.substring(1).toLowerCase()" )
#end
#set($methodCase = "$camelCaseName")
#set($paramName = "$camelCaseName.substring(0,1).toLowerCase()$camelCaseName.substring(1)")
#set($stripped_field = $field.type.replaceAll(".*ToscaKey", ""))
#set($real_type = $stripped_field.substring(1).replaceAll(">$", ""))
$real_type ##
#if ($field.boolean && $field.primitive)
is##
#else
get##
#end
${methodCase}() {
return get($field.name);
}
```

##### Tosca Optional Getter
```
/**
@return {@link #$field.name}

*/
#set( $camelCaseName = "" )
#set( $part = "" )
#foreach($part in $field.name.split("_"))
    #set( $camelCaseName = "${camelCaseName}$part.substring(0,1).toUpperCase()$part.substring(1).toLowerCase()" )
#end
#set($methodCase = "$camelCaseName")
#set($paramName = "$camelCaseName.substring(0,1).toLowerCase()$camelCaseName.substring(1)")
#set($return_type = $field.type.replaceAll(".*<", "").replaceAll(">", ""))
Optional<$return_type> ##
#if ($field.boolean && $field.primitive)
is##
#else
get##
#end
${methodCase}() {
return Optional.ofNullable(get($field.name));
}
```

##### Tosca Setter
```
/**
Sets {@link #$field.name}

*/
#set( $camelCaseName = "" )
#set( $part = "" )
#foreach($part in $field.name.split("_"))
    #set( $camelCaseName = "${camelCaseName}$part.substring(0,1).toUpperCase()$part.substring(1).toLowerCase()" )
#end
#set($methodName = "set$camelCaseName")
#set($paramName = "$camelCaseName.substring(0,1).toLowerCase()$camelCaseName.substring(1)")
#set($stripped_field = $field.type.replaceAll(".*ToscaKey", ""))
#set($real_type = $stripped_field.substring(1).replaceAll(">$", ""))
$class.getName() $methodName($real_type $paramName) {
  set($field.name, $paramName);
  return this;
}
```
