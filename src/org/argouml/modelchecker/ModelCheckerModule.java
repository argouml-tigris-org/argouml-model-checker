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
import java.util.List;

import org.apache.log4j.Logger;
import org.argouml.kernel.Project;
import org.argouml.moduleloader.ModuleInterface;
//import org.argouml.modelchecker.transform.Code2Model;
import org.argouml.modelchecker.transform.Model2SourceUnits;
import org.argouml.taskmgmt.ProgressMonitor;
import org.argouml.uml.generator.CodeGenerator;
import org.argouml.uml.generator.GeneratorHelper;
import org.argouml.uml.generator.GeneratorManager;
import org.argouml.uml.generator.Language;
import org.argouml.uml.generator.SourceUnit;
import org.argouml.uml.reveng.FileImportUtils;
import org.argouml.uml.reveng.ImportInterface;
import org.argouml.uml.reveng.ImportSettings;
import org.argouml.uml.reveng.ImporterManager;
import org.argouml.uml.reveng.SettingsTypes;
import org.argouml.util.SuffixFilter;

public class ModelCheckerModule implements ModuleInterface, CodeGenerator
{
    private static Language lang = GeneratorHelper.makeLanguage("SMV");

    private static final Logger LOG = Logger.getLogger(ModelCheckerModule.class);

    static final SuffixFilter SUFFIX_FILTERS[] =
    { new SuffixFilter("smv", "SMV source files"),
        new SuffixFilter("SMV", "SMV source files") };

    public boolean enable()
    {
        GeneratorManager.getInstance().addGenerator(ModelCheckerModule.lang, this);

        // TODO:
        // enable Notation

        return true;
    }
    
    public boolean disable()
    {
        GeneratorManager.getInstance().removeGenerator(ModelCheckerModule.lang);
        return true;
    }

    public String getName()
    {
        return "ModelChecker";
    }
    
    public String getInfo(int type)
    {
        switch (type)
        {
            case DESCRIPTION:
                return "This is the model checker module for generating smv-code from UML";
            case AUTHOR:
                return "Amir Saeidi";
            case VERSION:
                return "0.01";
        }
        
        return null;
    }
    
    // code generator
    public Collection generate(Collection objs, boolean deps)
    {
        Model2SourceUnits t = new Model2SourceUnits("", deps);

        try
        {
            return t.toSourceUnits(objs);
        }
        catch (Exception e)
        {
            LOG.error("while transforming: " + e);
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
            LOG.error("while transforming: " + e);
            return null;
        }

        for (SourceUnit su : sus)
        {
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
                LOG.error("can't create file '" + su.getFullName() + "': " + e);
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
            LOG.error("while transforming: " + e);
            return null;
        }

        for (SourceUnit su : sus)
        {
            res.add(su.getFullName());
        }

        return res;
    }
   

}
