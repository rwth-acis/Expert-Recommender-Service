package com.sathvik.textprocessing;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sathvik.models.TagMePOJO;
import com.sathvik.utils.Utils;

public class TagCreator {
	private Element root;
	private DocumentBuilderFactory documentBuilderFactory;
	private Document document;

	TagCreator() {

		try {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			document = documentBuilder.parse(TagCreator.class.getClassLoader()
					.getResourceAsStream("res/Posts_missing_tags.xml"));
			root = document.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void saveTagsToFile(String postid, String tags,
			ArrayList<TagMePOJO> tagmeObjs) {
		try {

			// Root Element
			Element rootElement = document.getDocumentElement();
			Element row = document.createElement("row");
			row.setAttribute("postId", postid);

			if (tags != null) {
				row.setAttribute("tagCollection", tags);
			} else {
				row.setAttribute("tagCollection", "");
			}

			rootElement.appendChild(row);

			for (TagMePOJO obj : tagmeObjs) {
				Element sTag = document.createElement("semanticTag");
				sTag.setAttribute("title", obj.mTitle);

				String categories = StringUtils.join(obj.mCategories, ",");

				sTag.setAttribute("categories", categories);
				sTag.setAttribute("rho", "" + obj.confidence);
				row.appendChild(sTag);
			}

			root.appendChild(row);

			DOMSource source = new DOMSource(document);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//URL path = TagCreator.class.getClassLoader().getResource(
			//		"res/Posts_xad_tags.xml");
			StreamResult result = new StreamResult(new File(
					"src/res/Posts_tags.xml"));
			//Utils.println("SAVING..... ::" + path);
			transformer.transform(source, result);

		} catch (Exception e) {
			Utils.println("ERROR ::" + e);
			e.printStackTrace();
		}
	}
}
