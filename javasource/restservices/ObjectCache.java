package restservices;

import java.util.HashMap;
import java.util.Map;

import restservices.proxies.RestObject;
import restservices.proxies.RestReference;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class ObjectCache {
	private Map<String, IMendixObject> restObjects = new HashMap<String, IMendixObject>();
	private Map<String, IMendixObject> restReferences = new HashMap<String, IMendixObject>();
	
	
	public IMendixObject getObject(IContext context, String url,
			String otherSideType) throws Exception {

		//Return as reference?
		if (Core.isSubClassOf(RestReference.getType(), otherSideType)) {
			IMendixObject res = restReferences.get(url);
			if (res != null)
				return res;
			res = Core.instantiate(context, otherSideType);
			res.setValue(context, RestReference.MemberNames._url.toString(), url);
			Core.commit(context, res);
			restReferences.put(url, res);
			return res;
		}
		else if (!Core.isSubClassOf(RestObject.getType(), otherSideType))
			throw new Exception("Failed to load reference " + url + ": Not a subclass of RestObject: " + otherSideType);
			
		
		IMendixObject res = restObjects.get(url);
		if (res != null)
			return res;
		res = Core.instantiate(context, otherSideType);
		restObjects.put(url, res);
		RestServices.getObject(context, url, this);
		return res;
	}

	public void putObject(String url, IMendixObject target) {
		restObjects.put(url, target);
	}

}
