package com.projekat.bezbednostWeb.zip;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
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

import com.projekat.bezbednostWeb.certificate.KeyStoreReader;

public class ZipChecker {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
        org.apache.xml.security.Init.init();
	}
	
	public static boolean check(MultipartFile multipartFile) {
		
		// unzip, find xml, get email from xml, check signature, find user with that email
		
		ZipFile zipFile = null;
		try {			
			zipFile = new ZipFile( new SeekableInMemoryByteChannel(multipartFile.getBytes()));
			Enumeration<ZipArchiveEntry> zipEnum = zipFile.getEntries();			
			
			while(zipEnum.hasMoreElements()) {
				ZipArchiveEntry entry = zipEnum.nextElement();				
				try {
					String extension = FilenameUtils.getExtension(entry.getName());
					switch(extension) {
						case "xml":
							
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setNamespaceAware(true);
							DocumentBuilder db = dbf.newDocumentBuilder();
							Document document = db.parse(zipFile.getInputStream(entry));
							
							// get email from xml
							NodeList emailNode = document.getElementsByTagName("email");
							Element emailElement = (Element) emailNode.item(0);
							String email = emailElement.getTextContent();
							System.out.println("EMAIL: " + emailElement.getTextContent());
							
							// get public key from local certificate
							KeyStore keyStore = KeyStoreReader.read("data/" + email + ".jks", new char[0]);
							Certificate certificate = keyStore.getCertificate(email);
							PublicKey publicKey = certificate.getPublicKey();
							
							
							if(verifySignature(document, publicKey)) {
								System.out.println("Dokument verifikovan");
							}
							else {
								System.out.println("Dokument NIJE verifikovan");
							}
							
							// proveriti HASH vrednosti slike (uporediti sa onim iz xml-a)
							
							break;
						case "jpg":
						case "png":
							break;
					}
					
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			ZipFile.closeQuietly(zipFile);
		}
		
		return false;
	}
	
	
	private static boolean verifySignature(Document doc, PublicKey realPublicKey) {
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
}
