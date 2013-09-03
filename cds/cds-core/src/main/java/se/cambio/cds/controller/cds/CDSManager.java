package se.cambio.cds.controller.cds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import se.cambio.cds.controller.CDSSessionManager;
import se.cambio.cds.controller.guide.GuideManager;
import se.cambio.cds.gdl.model.Guide;
import se.cambio.cds.model.facade.execution.vo.GeneratedArchetypeReference;
import se.cambio.cds.model.facade.kb.delegate.KBFacadeDelegate;
import se.cambio.cds.model.facade.kb.delegate.KBFacadeDelegateFactory;
import se.cambio.cds.model.instance.ArchetypeReference;
import se.cambio.cds.model.instance.ElementInstance;
import se.cambio.cds.util.Domains;
import se.cambio.cds.util.ElementInstanceCollection;
import se.cambio.cds.util.GeneratedElementInstanceCollection;
import se.cambio.cds.util.PredicateGeneratedElementInstance;
import se.cambio.openehr.util.exceptions.InternalErrorException;
import se.cambio.openehr.util.exceptions.PatientNotFoundException;

public class CDSManager {

    public static Collection<ElementInstance> getElementInstances(
	    String ehrId, 
	    Collection<String> guideIds,
	    Collection<ArchetypeReference> ehrData, 
	    GuideManager guideManager) 
		    throws PatientNotFoundException, InternalErrorException{

	ElementInstanceCollection eic = new ElementInstanceCollection();
	if (ehrData!=null){
	    eic.addAll(ehrData, guideManager);
	}

	GeneratedElementInstanceCollection completeEIC = guideManager.getElementInstanceCollection(guideIds);

	//Search for EHR elements
	//Query EHR for elements
	if (ehrId!=null){
	    Collection<ArchetypeReference> ars = getEHRArchetypeReferences(completeEIC);
	    eic.addAll(CDSSessionManager.getEHRFacadeDelegate().queryEHRElements(ehrId, ars));
	}

	return getElementInstances(eic, completeEIC, guideManager);
    }

    public static Map<String,Collection<ElementInstance>> getElementInstancesForPopulation(
	    Collection<String> ehrIds, 
	    Collection<String> guideIds,
	    Collection<ArchetypeReference> ehrData, 
	    GuideManager guideManager) throws PatientNotFoundException, InternalErrorException{
	GeneratedElementInstanceCollection completeEIC = guideManager.getElementInstanceCollection(guideIds);
	Collection<ArchetypeReference> ars = getEHRArchetypeReferences(completeEIC);
	Map<String,Collection<ElementInstance>> ehrMap =
		CDSSessionManager.getEHRFacadeDelegate().queryEHRElements(ehrIds, ars);
	Map<String,Collection<ElementInstance>> cdsEIMap = new HashMap<String, Collection<ElementInstance>>();
	for (String ehrId : ehrIds) {
	    ElementInstanceCollection eic = new ElementInstanceCollection();
	    //TODO If the data existed in ehrData, it should not query for it again to EHR
	    if (!ars.isEmpty()){
		Collection<ElementInstance> eis = ehrMap.get(ehrId);
		if (eis!=null){
		    eic.addAll(eis);
		}
	    }
	    cdsEIMap.put(ehrId, getElementInstances(eic, completeEIC, guideManager));
	}
	return cdsEIMap;
    }


    public static Collection<ArchetypeReference> getEHRArchetypeReferences(GeneratedElementInstanceCollection eic){
	Collection<ArchetypeReference> ars = new ArrayList<ArchetypeReference>();
	ars.addAll(eic.getAllArchetypeReferencesByDomain(Domains.EHR_ID));
	ars.addAll(eic.getAllArchetypeReferencesByDomain(ElementInstanceCollection.EMPTY_CODE));
	Map<String, ArchetypeReference> resultARsMap = new HashMap<String, ArchetypeReference>();
	//TODO Predicates, element selection
	for (ArchetypeReference ar : ars) {
	    ArchetypeReference preAR = resultARsMap.get(ar.getIdArchetype());
	    if (preAR==null || preAR.getAggregationFunction()!=null){
		resultARsMap.put(ar.getIdArchetype(), ar);
	    }
	}
	return resultARsMap.values();
    }

    private static Collection<ElementInstance> getElementInstances(ElementInstanceCollection eic, GeneratedElementInstanceCollection completeEIC, GuideManager guideManager) 
	    throws InternalErrorException{
	KBFacadeDelegate kbfd = KBFacadeDelegateFactory.getDelegate();

	//Search for CDS templates (not looking into 'ANY' Domain)
	Collection<ElementInstance> cdsGeneratedElementInstances = guideManager.getCompleteElementInstanceCollection().getAllElementInstancesByDomain(Domains.CDS_ID);
	Set<String> idTemplates = new HashSet<String>();
	for (ElementInstance elementInstance : cdsGeneratedElementInstances) {
	    String idTemplate = elementInstance.getArchetypeReference().getIdTemplate();
	    if (idTemplate!=null){
		idTemplates.add(idTemplate);
	    }
	}

	//Query KB for CDS elements
	if (!idTemplates.isEmpty()){
	    eic.addAll(kbfd.getKBElementsByIdTemplate(idTemplates));
	}

	//Check for missing elements
	checkForMissingElements(eic, completeEIC, guideManager);
	return eic.getAllElementInstances();
    }

    public static void checkForMissingElements(
	    ElementInstanceCollection elementInstanceCollection, 
	    ElementInstanceCollection completeEIC, 
	    GuideManager guideManager){
	//Check for guide elements, if not present, create archetype reference
	for (ArchetypeReference archetypeReference : completeEIC.getAllArchetypeReferences()) {
	    GeneratedArchetypeReference gar = (GeneratedArchetypeReference)archetypeReference;
	    Guide referencedGuide = getReferencedGuideInPredicate(gar, guideManager);
	    boolean matches = elementInstanceCollection.matches(gar, referencedGuide);
	    if (!matches){
		elementInstanceCollection.add(archetypeReference, guideManager);
	    }
	}
    }

    private static Guide getReferencedGuideInPredicate(GeneratedArchetypeReference gar, GuideManager gm){
	Iterator<ElementInstance> i = gar.getElementInstancesMap().values().iterator();
	while(i.hasNext()){
	    ElementInstance ei = i.next();
	    if (ei instanceof PredicateGeneratedElementInstance){
		String idGuide = ((PredicateGeneratedElementInstance)ei).getGuideId();
		return gm.getGuide(idGuide);
	    }
	}
	return null;
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