/* $Id: eclipse-argo-codetemplates.xml 17993 2010-02-11 21:46:56Z linus $
 *******************************************************************************
 * Copyright (c) 2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Amir
 *******************************************************************************
 */

package org.argouml.modelchecker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.argouml.modelchecker.transform.Model2SourceUnits;
import org.argouml.uml.generator.CodeGenerator;
import org.argouml.uml.generator.SourceUnit;


public class ModelCheckerGenerator implements CodeGenerator 
{
    private final Logger log;
    
    public ModelCheckerGenerator() 
    {
        this.log = Logger.getLogger(this.getClass());
    }

    public Collection generate(Collection objs, boolean deps) {
        Model2SourceUnits t = new Model2SourceUnits("", deps);

        try
        {
            return t.toSourceUnits(objs);
        }
        catch (Exception e)
        {
            this.log.error("while transforming: " + e);
            return null;
        }

    }

    public Collection generateFiles(Collection objs, String path, boolean deps) 
    {
        Collection<String> res = new ArrayList<String>();
        Model2SourceUnits t = new Model2SourceUnits(path, deps);
        Collection<SourceUnit> sus;

        // transform
        try
        {
            sus = t.toSourceUnits(objs);
        }
        catch (Exception e)
        {
            this.log.error("while transforming: " + e);
            return null;
        }

        for (SourceUnit su : sus) {
            try 
            {
                // create directory
                File dir = new File(su.getBasePath());
                dir.mkdirs();

                // create file
                File file = new File(su.getFullName());

                // write file
                OutputStream os = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(os);
                osw.write(su.getContent());
                osw.close();
                res.add(file.getName());
            } 
            catch (IOException e) 
            {
                this.log.error("can't create file '" 
                        + su.getFullName() + "': " + e);
            }
        }        

        return res;
    }

    public Collection generateFileList(Collection objs, boolean deps) 
    {
        Collection<String> res = new ArrayList<String>();
        Model2SourceUnits t = new Model2SourceUnits("", deps);
        Collection<SourceUnit> sus;

        // transform
        try
        {
            sus = t.toSourceUnits(objs);
        }
        catch (Exception e)
        {
            this.log.error("while transforming: " + e);
            return null;
        }

        for (SourceUnit su : sus) {
            res.add(su.getFullName());
        }        

        return res;
    }

}
