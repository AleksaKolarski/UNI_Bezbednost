package com.projekat.bezbednostDesktop.xml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlGenerator {
	
	private String email;
	private List<XmlImage> images;
	
	
	public XmlGenerator() {
		this.images = new ArrayList<XmlImage>();
	}
	
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void addImage(String name, Integer size, String hash) {
		XmlImage xmlImage = new XmlImage(name, size, hash);
		images.add(xmlImage);
	}
	
	public Document generate() throws XmlGeneratorException {
		
		if(this.email == null || this.email.isEmpty()) {
			throw new XmlGeneratorException("email not set");
		}
		
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			 DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			 Document document = documentBuilder.newDocument();
			 
			 // Root element
			 Element rootElement = document.createElement("RootElement");
			 document.appendChild(rootElement);
			 
			 // Email element
			 Element emailElement = document.createElement("email");
			 rootElement.appendChild(emailElement);
			 emailElement.appendChild(document.createTextNode(this.email));
			 
			 // Date element
			 Element dateElement = document.createElement("date");
			 rootElement.appendChild(dateElement);
			 dateElement.appendChild(document.createTextNode(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
			 
			 // Images element
			 Element imagesElement = document.createElement("images");
			 rootElement.appendChild(imagesElement);
			 
			 for(XmlImage image: images) {
				 
				 // Image element
				 Element imageElement = document.createElement("image");
				 imagesElement.appendChild(imageElement);
				 
				 // Image name attribute
				 Attr imageNameAttr = document.createAttribute("name");
				 imageNameAttr.setValue(image.name);
				 imageElement.setAttributeNode(imageNameAttr);
				 
				 // Image size attribute
				 Attr imageSizeAttr = document.createAttribute("size");
				 imageSizeAttr.setValue((image.size).toString());
				 imageElement.setAttributeNode(imageSizeAttr);
				 
				 // Image hash attribute
				 Attr imageHashAttr = document.createAttribute("hash");
				 imageHashAttr.setValue(image.hash);
				 imageElement.setAttributeNode(imageHashAttr);
			 }
			 
			 
			 return document;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new XmlGeneratorException("Could not create DocumentBuilder");
		}
	}
	
	
	private class XmlImage{
		public String name;
		public Integer size;
		public String hash;
		
		public XmlImage(String name, Integer size, String hash) {
			this.name = name;
			this.size = size;
			this.hash = hash;
		}
	}
}
