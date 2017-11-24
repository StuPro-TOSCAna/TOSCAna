# TOSCAna - REST API sample responses and requests

## ``/status`` query

```JSON
{
	"status": "idle",
	"available_storage": 1000,
	"total_storage": 1100,
	"_links": {
		"self": {
			"href": "http://localhost:8080/status"
		}
	}
}
```

## ``/platforms`` query

```JSON
{
	"_embedded": {
		"platform": [
			{
				"id": "p-a",
				"name": "platform-1",
				"_links": {
					"self": {
						"href": "http://localhost:8080/platforms/p-a"
					}
				}
			},
			{
				"id": "p-b",
				"name": "platform-2",
				"_links": {
					"self": {
						"href": "http://localhost:8080/platforms/p-b"
					}
				}
			},
			{
				"id": "p-c",
				"name": "platform-3",
				"_links": {
					"self": {
						"href": "http://localhost:8080/platforms/p-c"
					}
				}
			}
    ]
	},
	"_links": {
		"self": {
			"href": "http://localhost:8080/platforms/"
		}
	}
}
```

## ``/platforms/{platform}`` query

```JSON
{
	"id": "p-a",
	"name": "platform-1",
	"_links": {
		"self": {
			"href": "http://localhost:8080/platforms/p-a"
		}
	}
}
```

## ``/csars`` query

```JSON
{
	"_embedded": {
		"csar": [
			{
				"name": "kubernetes-cluster",
				"_links": {
					"self": {
						"href": "http://localhost:8080/csars/kubernetes-cluster"
					},
					"transformations": {
						"href": "http://localhost:8080/csars/kubernetes-cluster/transformations/"
					}
				}
			},
			{
				"name": "apache-test",
				"_links": {
					"self": {
						"href": "http://localhost:8080/csars/apache-test"
					},
					"transformations": {
						"href": "http://localhost:8080/csars/apache-test/transformations/"
					}
				}
			},
			{
				"name": "mongo-db",
				"_links": {
					"self": {
						"href": "http://localhost:8080/csars/mongo-db"
					},
					"transformations": {
						"href": "http://localhost:8080/csars/mongo-db/transformations/"
					}
				}
			}
		]
	},
	"_links": {
		"self": {
			"href": "http://localhost:8080/csars/"
		}
	}
}
```

## ``/csars/{name}`` query

```JSON
{
	"name": "mongo-db",
	"_links": {
		"self": {
			"href": "http://localhost:8080/csars/mongo-db"
		},
		"transformations": {
			"href": "http://localhost:8080/csars/mongo-db/transformations/"
		}
	}
}
```

## ``/csars/{csarName}/transformations`` query

```JSON
{
	"_embedded": {
		"transformation": [
			{
				"progress": 0,
				"status": "INPUT_REQUIRED",
				"platform": "p-a",
				"_links": {
					"self": {
						"href": "http://localhost:8080/csars/mongo-db/transformations/p-a"
					},
					"logs": {
						"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/logs?start=0"
					},
					"platform": {
						"href": "http://localhost:8080/platforms/p-a"
					},
					"artifact": {
						"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/artifact"
					},
					"properties": {
						"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/properties"
					},
					"delete": {
						"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/delete"
					}
				}
			}
		]
	},
	"_links": {
		"self": {
			"href": "http://localhost:8080/csars/mongo-db/transformations/"
		}
	}
}
```

## ``/csars/{csarName}/transformations/{platform}`` query

```JSON
{
  "progress" : 0,
  "status" : "INPUT_REQUIRED",
  "platform" : "p-a",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/csars/mongo-db/transformations/p-a"
    },
    "logs" : {
      "href" : "http://localhost:8080/csars/mongo-db/transformations/p-a/logs?start=0"
    },
    "platform" : {
      "href" : "http://localhost:8080/platforms/p-a"
    },
    "artifact" : {
      "href" : "http://localhost:8080/csars/mongo-db/transformations/p-a/artifact"
    },
    "properties" : {
      "href" : "http://localhost:8080/csars/mongo-db/transformations/p-a/properties"
    },
    "delete" : {
      "href" : "http://localhost:8080/csars/mongo-db/transformations/p-a/delete"
    }
  }
}
```

## ``/csars/{csarName}/transformations/{platform}/logs`` query

```JSON
{
	"start": 0,
	"end": 9,
	"logs": [
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-0-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-1-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-2-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-3-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-4-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-5-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-6-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-7-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-8-1508006749680",
			"level": "DEBUG"
		},
		{
			"timestamp": 1508006749680,
			"message": "Hallo Welt-9-1508006749680",
			"level": "DEBUG"
		}
	],
	"_links": {
		"self": {
			"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/logs?start=0"
		},
		"next": {
			"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/logs?start=9"
		}
	}
}
```

## ``/csars/{csarName}/transformations/{platform}/artifact`` query

```JSON
{
	"access_url": "http://not-yet-implemented.com/",
	"_links": {
		"self": {
			"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/artifact"
		}
	}
}
```

## ``/csars/{csarName}/transformations/{platform}/properties`` query

### Getting the properties (HTTP GET)

```JSON
{
	"properties": [
		{
			"key": "text_property",
			"type": "text"
		},
		{
			"key": "float_property",
			"type": "float"
		},
		{
			"key": "name_property",
			"type": "name"
		},
		{
			"key": "unsigned_integer_property",
			"type": "unsigned_integer"
		},
		{
			"key": "secret_property",
			"type": "secret"
		},
		{
			"key": "integer_property",
			"type": "integer"
		}
	],
	"_links": {
		"self": {
			"href": "http://localhost:8080/csars/mongo-db/transformations/p-a/properties"
		}
	}
}
```

### Setting the properties (HTTP PUT / POST)

#### Sample Request Body

```JSON
{
	"properties": {
		"text_property":"Hallo Welt",
		"name_property": "hallo",
		"secret_property": "I bims 1 geheimnis",
		"unsigned_integer_property": "1337"
	}
}
```

#### Sample Response

```JSON
{
	"valid_inputs": {
		"name_property": true,
		"text_property": true,
		"secret_property": true,
		"unsigned_integer_property": true
	}
}
```
