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

package org.argouml.modelchecker.transform;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.argouml.modelchecker.smv.SMVCompilationUnit;
import org.argouml.uml.generator.SourceUnit;

public class Model2SourceUnits {
    private boolean solveDeps;
    private String path;

    private Logger log;

    public Model2SourceUnits(String path, boolean solveDeps)
    {
        this.path = path;
        this.solveDeps = solveDeps;
        this.log = Logger.getLogger(this.getClass());
    }

    public Collection<SourceUnit> toSourceUnits(Collection objs)
    {
        ArrayList<SourceUnit> res = new ArrayList<SourceUnit>();

        Model2CompilationUnit t = new Model2CompilationUnit(this.path,
                this.solveDeps);

        Collection<PyCompilationUnit> cunits;

        try
        {
            cunits = t.toPyCompilationUnits(objs);
        }
        catch (Exception e)
        {
            this.log.error("can't transform", e);
            return res;
        }

        for (PyCompilationUnit cu : cunits)
        {
            try
            {
                CompilationUnit2Code t2 = new CompilationUnit2Code();
                String code = t2.toCode(cu);
                File cuFile = cu.getFile();
                String filename = cuFile.getName();
                String path = cuFile.getAbsolutePath();
                SourceUnit su = new SourceUnit(filename, path, code);
                res.add(su);
            }
            catch (Exception e)
            {
                this.log.error("can't transform: " + e);
            }
        }

        return res;
    }
}
