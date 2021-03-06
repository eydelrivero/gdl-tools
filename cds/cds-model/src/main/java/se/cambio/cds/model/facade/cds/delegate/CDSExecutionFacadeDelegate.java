package se.cambio.cds.model.facade.cds.delegate;

import se.cambio.cds.gdl.model.Guide;
import se.cambio.cds.model.facade.execution.vo.ExecutionMode;
import se.cambio.cds.model.facade.execution.vo.RuleExecutionResult;
import se.cambio.cds.model.facade.execution.vo.StoringMode;
import se.cambio.cds.model.instance.ArchetypeReference;
import se.cambio.cds.util.exceptions.GuideNotFoundException;
import se.cambio.openehr.util.exceptions.InternalErrorException;
import se.cambio.openehr.util.exceptions.PatientNotFoundException;

import java.util.Calendar;
import java.util.Collection;

/**
 * @author iago.corbal
 *
 */
public interface CDSExecutionFacadeDelegate {

    public RuleExecutionResult execute(
	    String ehrId,
	    Collection<ArchetypeReference> ehrData,
	    Calendar date,
	    ExecutionMode executionMode) 
		    throws InternalErrorException, PatientNotFoundException;

    public RuleExecutionResult execute(
            String ehrId,
            Collection<String> guideIds,
            Collection<ArchetypeReference> ehrData,
            Calendar date)
            throws InternalErrorException, PatientNotFoundException;

    public Collection<RuleExecutionResult> execute(
	    Collection<String> ehrIds,
	    Collection<String> guideIds,
	    Calendar date)
		    throws InternalErrorException, PatientNotFoundException, GuideNotFoundException;

    public Collection<RuleExecutionResult> executeAndStore(
	    Collection<String> ehrIds,
	    Collection<String> guideIds,
	    Calendar date,
	    StoringMode storingMode) throws InternalErrorException, PatientNotFoundException;
    
    public Collection<Guide> searchAllGuides() 
	    throws InternalErrorException;

    public Collection<Guide> searchByGuideIds(Collection<String> guideIds)
	    throws InternalErrorException, GuideNotFoundException;

    public Collection<ArchetypeReference> searchEHRData(String ehrId, Calendar date, Collection<String> guideIds)
            throws InternalErrorException, PatientNotFoundException, GuideNotFoundException;

}/*
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