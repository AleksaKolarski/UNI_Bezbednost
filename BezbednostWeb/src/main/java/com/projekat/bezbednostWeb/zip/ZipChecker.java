package com.projekat.bezbednostWeb.zip;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.projekat.bezbednostWeb.certificate.KeyStoreUtil;

public class ZipChecker {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
        org.apache.xml.security.Init.init();
	}
	
	
	private MultipartFile multipartFile;
	
	private String email;
	
	public ZipChecker(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}
	
	public boolean check() {
		
		boolean good = true;
		
		ZipFile zipFile;
		Document xmlDocument;
		Map<String, InputStream> images;
		
		zipFile = null;
		xmlDocument = null;
		images = new HashMap<String, InputStream>();		
		
		// MultipartFile to ZipFile
		try {
			zipFile = new ZipFile(new SeekableInMemoryByteChannel(multipartFile.getBytes()));
		}catch (IOException e) {
			return false;
		}
		
		// Ucitavamo fajlove iz zipa
		Enumeration<ZipArchiveEntry> zipEnum = zipFile.getEntries();
		while(zipEnum.hasMoreElements()) {
			ZipArchiveEntry entry = zipEnum.nextElement();
			String extension = FilenameUtils.getExtension(entry.getName());
			try {
				switch (extension) {
				case "xml":
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setNamespaceAware(true);
					DocumentBuilder db = dbf.newDocumentBuilder();
					xmlDocument = db.parse(zipFile.getInputStream(entry));
					break;
				case "jpg":
				case "png":
					images.put(entry.getName(), zipFile.getInputStream(entry));
					break;
				default:
					break;
				}
			}
			catch (IOException | ParserConfigurationException | SAXException e) {
				e.printStackTrace();
				good = false;
			}
		}
		
		// Proveri xml potpis
		// radi ovo samo ako je sve do sad dobro
		if(good == true) {
			if( ! checkXmlSignature(xmlDocument)) {
				good = false;
			}
		}
		
		// Proveri pravi hash slike sa hash-om iz xml-a
		// radi ovo samo ako je do sad sve dobro
		if( good == true) {
			if( ! checkImages(xmlDocument, images)) {
				good = false;
			}
		}
		
		ZipFile.closeQuietly(zipFile);
		return good;
	}
	
	private boolean checkImages(Document document, Map<String, InputStream> imagesMap) {
		
		// get hash class instance
		MessageDigest sha;
		try {
			sha = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Could not get MessageDigest(SHA-256) class instance. Could not verify hashes.");
			e.printStackTrace();
			return false;
		}
		
		// for every image from xml, check hash of real file
		NodeList imageNodes = document.getElementsByTagName("image");
		for(int i = 0; i < imageNodes.getLength(); i++) {
			Element imageElement = (Element) imageNodes.item(i);
			String imageName = imageElement.getAttribute("name");
			String xmlImageHash = imageElement.getAttribute("hash");
			System.out.println(imageName + " === " + xmlImageHash);
			
			// calculate real hash from zip
			byte[] bytes;
			try {
				InputStream tmpImageInputStream = imagesMap.get(imageName);	
				if(tmpImageInputStream == null) {
					throw new NullPointerException();
				}
				bytes = IOUtils.toByteArray(tmpImageInputStream);
			} catch (IOException | NullPointerException e) {
				System.out.println("Could not read file from Zip archive: " + imageName);
				e.printStackTrace();
				return false;
			}
			byte[] hashBytes = sha.digest(bytes);
			String realHash = Base64.getEncoder().encodeToString(hashBytes);
			
			// compare xml hash and real hash
			if( ! realHash.equals(xmlImageHash)) {
				System.out.println("Hash not good. Real one is " + realHash);
				return false;
			}
		}
		return true;
	}
	
	private boolean checkXmlSignature(Document document) {
		// get email from xml
		NodeList emailNode = document.getElementsByTagName("email");
		Element emailElement = (Element) emailNode.item(0);
		String email = emailElement.getTextContent();
		System.out.println("EMAIL: " + emailElement.getTextContent());
		this.email = emailElement.getTextContent();
		
		// get public key from local certificate
		KeyStore keyStore = KeyStoreUtil.read("data/" + email + ".jks", new char[0]);
		Certificate certificate;
		try {
			certificate = keyStore.getCertificate(email);
		} catch (KeyStoreException e) {
			System.out.println("Could not read certificate from keystore for user: " + email);
			e.printStackTrace();
			return false;
		}
		PublicKey publicKey = certificate.getPublicKey();
		
		return verifySignature(document, publicKey);
	}	
	
	private boolean verifySignature(Document doc, PublicKey realPublicKey) {
		try {
			//Pronalazi se prvi Signature element 
			NodeList signatures = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
			Element signatureEl = (Element) signatures.item(0);
			
			//kreira se signature objekat od elementa
			XMLSignature signature = new XMLSignature(signatureEl, null);
			
			//preuzima se key info
			KeyInfo keyInfo = signature.getKeyInfo();
			
			if( ! Arrays.equals(keyInfo.getPublicKey().getEncoded(), realPublicKey.getEncoded())) {
				System.out.println("public key is fake");
				return false;
			}
			
			//registruju se resolver-i za javni kljuc i sertifikat
			keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
			keyInfo.registerInternalKeyResolver(new X509CertificateResolver());
			
			//ako sadrzi sertifikat
			if(keyInfo.containsX509Data() && keyInfo.itemX509Data(0).containsCertificate()) { 
			    Certificate cert = keyInfo.itemX509Data(0).itemCertificate(0).getX509Certificate();
			    
			    //ako postoji sertifikat, provera potpisa
			    if(cert != null) 
			    	return signature.checkSignatureValue((X509Certificate) cert);
			    else {
			    	return false;
			    }
			}
			else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getEmail() {
		return this.email;
	}
}
