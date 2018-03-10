# jsonp-maven-plugin
A maven plugin for applying patches to json resources

## Usage

### Override

An override will overlay one JSON file over another.  It will append new fields and overwrite existing ones. 

For example given a source json file named source.json

```
{"test":"Test1"}
```

And a override json file named override.json

```
{"test2":"Test2"}
```

The output will be:

```
{"test":"Test1","test2":"Test2"}
```

The plugin configuration required will look like this

```
  <build>
    <plugins>
      ...
      <plugin>
        <groupId>com.bobpaulin.maven.jsonp</groupId>
        <artifactId>jsonp-maven-plugin</artifactId>
        <version>0.0.2</version>
        <executions>
          <execution>
            <id>merge</id>
            <phase>validate</phase>
            <goals>
              <goal>merge</goal>
            </goals>
            <configuration>
                <sourceFile>src/main/resources/source.json</sourceFile>
                <overrideSourceFile>src/main/resources/override.json</overrideSourceFile>
                <outputFile>${project.build.directory}/test/out.json</outputFile>
            </configuration>
          </execution>
        </executions>
      </plugin>
      ...
    </plugins>
  </build>
```

### Patch

A patch uses a JSON Pointer to replace a specific section of a source json document.

For example given a source json file named source.json

```
{"test":"Test1"}
```

And a patch json file named patch.json

```
[
    { "op": "add", "path": "/test2", "value": "Test2" }
]
```

The output will be:

```
{"test":"Test1","test2":"Test2"}
```

The plugin configuration required will look like this:

```
  <build>
    <plugins>
    ...
      <plugin>
        <groupId>com.bobpaulin.maven.jsonp</groupId>
        <artifactId>jsonp-maven-plugin</artifactId>
        <version>0.0.2</version>
        <executions>
          <execution>
            <id>merge</id>
            <phase>validate</phase>
            <goals>
              <goal>merge</goal>
            </goals>
            <configuration>
                <sourceFile>src/main/resources/source.json</sourceFile>
                <patchFile>src/main/resources/patch.json</patchFile>
                <outputFile>${project.build.directory}/test/out.json</outputFile>
            </configuration>
          </execution>
        </executions>
      </plugin>
      ...
    </plugins>
  </build>
```

