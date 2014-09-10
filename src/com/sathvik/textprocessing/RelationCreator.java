package com.sathvik.textprocessing;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sathvik.models.Resource;
import com.sathvik.utils.Utils;

public class RelationCreator extends DefaultHandler {
	ArrayList<Integer> parentIds;
	GraphMapper mapper = new GraphMapper();
	public RelationCreator(ArrayList<Integer> parentids) {
		parentIds = parentids;
		
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("row")) {
			String id = attributes.getValue("Id");
			String postTypeId = attributes.getValue("PostTypeId");
			
			//Utils.println("PARENT IDS SIZE:: "+parentIds.size());
			if(postTypeId.equals("2")) {
				String parentId = attributes.getValue("ParentId");
				if(parentIds.contains(new Integer(parentId))) {
					Resource res = new Resource(new Integer(id), "");
					mapper.putNode(new Integer(parentId), res);
				}
			}
			
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("row")) {
			// Not required now
		}
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		String text = new String(ch, start, length);
		// Not required now.
	}

	public void endDocument() throws SAXException {
		mapper.showViewer();
	}
}
