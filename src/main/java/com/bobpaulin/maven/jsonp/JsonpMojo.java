package com.bobpaulin.maven.jsonp;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import javax.json.JsonWriter;

/**
 * Goal which merges a json file.
 *
 */
@Mojo( name = "merge", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class JsonpMojo
    extends AbstractMojo
{
    @Parameter(property = "sourceFile", required = true)
    private File sourceFile;
    
    @Parameter(property = "patchFile", required = false)
    private File patchFile;
    
    @Parameter(property = "overrideSourceFile", required = false)
    private File overrideSourceFile;
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}/out.json", property = "outputFile", required = true )
    private File outputFile;

    public void execute()
        throws MojoExecutionException
    {
        JsonReader sourceReader = null;
        JsonReader patchReader = null;
        JsonReader overrideSource = null;
        JsonObject mergedJson = null;
        
        try {
            sourceReader = Json.createReader(new FileInputStream(this.sourceFile));
        } 
        catch (IOException e)
        {
            throw new MojoExecutionException("Can't find source file");
        }
        if(patchFile != null)
        {
            try {
                patchReader = Json.createReader(new FileInputStream(this.patchFile));
                JsonArray patchArray = patchReader.readArray();
                
                JsonPatch jsonPatch = Json.createPatch(patchArray);
                
                mergedJson = jsonPatch.apply(sourceReader.readObject());
            } catch (FileNotFoundException e) {
                throw new MojoExecutionException("Can't find patch file");
            }
            
        }
        else if(this.overrideSourceFile != null)
        {
            try {
                overrideSource = Json.createReader(new FileInputStream(this.overrideSourceFile));
                JsonObject patch = overrideSource.readObject();
                
                JsonMergePatch mergePatch = Json.createMergePatch(patch);
                
                mergedJson = mergePatch.apply(sourceReader.readObject()).asJsonObject();
            } catch (FileNotFoundException e) {
                throw new MojoExecutionException("Can't find override file");
            }
        }
        else
        {
            throw new MojoExecutionException("Need to provide a patch or override file");
        }
        
        
        
        try {
            if ( !this.outputFile.exists() )
            {
                
                this.outputFile.getParentFile().mkdirs();
            }
            JsonWriter writer = Json.createWriter(new FileWriter(this.outputFile));

            writer.write(mergedJson);
            writer.close();
        } catch(IOException e)
        {
            throw new MojoExecutionException("Can't write to output file", e);
        }
        

        
    }
}
