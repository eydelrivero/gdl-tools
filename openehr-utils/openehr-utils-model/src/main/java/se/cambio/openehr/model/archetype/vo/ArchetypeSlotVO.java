package se.cambio.openehr.model.archetype.vo;

import java.util.Collection;



public class ArchetypeSlotVO extends PathableVO{

    private static final long serialVersionUID = 25042012L;

    private Collection<String> includes;
    private Collection<String> exludes;
   
    public ArchetypeSlotVO(String name, String description, String type, String idParentCluster,
	    String idArchetype, String idTemplate, String path, Collection<String> includes,
	    Collection<String> exludes) {
	super(name, description, type, idParentCluster, idArchetype, idTemplate, path);
	this.includes = includes;
	this.exludes = exludes;
    }
    
    public Collection<String> getIncludes() {
        return includes;
    }
    public void setIncludes(Collection<String> includes) {
        this.includes = includes;
    }
    public Collection<String> getExludes() {
        return exludes;
    }
    public void setExludes(Collection<String> exludes) {
        this.exludes = exludes;
    }
}
/*
 *  ***** BEGIN LICENSE BLOCK *****
 *  Version: MPL 2.0/GPL 2.0/LGPL 2.1
 *
 *  The contents of this file are subject to the Mozilla Public License Version
 *  2.0 (the 'License'); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an 'AS IS' basis,
 *  WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 *  for the specific language governing rights and limitations under the
 *  License.
 *
 *
 *  The Initial Developers of the Original Code are Iago Corbal and Rong Chen.
 *  Portions created by the Initial Developer are Copyright (C) 2012-2013
 *  the Initial Developer. All Rights Reserved.
 *
 *  Contributor(s):
 *
 * Software distributed under the License is distributed on an 'AS IS' basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *  ***** END LICENSE BLOCK *****
 */