package com.projekat.bezbednostDesktop.xml;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlSigner {
	
	public static Document SignDocument(Document document, PrivateKey privateKey, Certificate certificate) throws XmlSignerException {
		try {
			Element rootElement = document.getDocumentElement();
			
			XMLSignature xmlSignature;
			xmlSignature = new XMLSignature(document, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			Transforms transforms = new Transforms(document);
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
				
			xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			xmlSignature.addKeyInfo(certificate.getPublicKey());
			xmlSignature.addKeyInfo((X509Certificate) certificate);
				
			rootElement.appendChild(xmlSignature.getElement());
				
			xmlSignature.sign(privateKey);
			
			return document;
		}catch (XMLSecurityException e) {
			e.printStackTrace();
			throw new XmlSignerException("Could not sign document");
		}
	}
	
	public static boolean VerifyDocument(Document document) {
		try {
			//Pronalazi se prvi Signature element 
			NodeList signatures = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
			Element signatureEl = (Element) signatures.item(0);
			
			//kreira se signature objekat od elementa
			XMLSignature signature = new XMLSignature(signatureEl, null);
			
			//preuzima se key info
			KeyInfo keyInfo = signature.getKeyInfo();
			
			//ako postoji
			if(keyInfo != null) {
				//registruju se resolver-i za javni kljuc i sertifikat
				keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
			    keyInfo.registerInternalKeyResolver(new X509CertificateResolver());
			    
			    //ako sadrzi sertifikat
			    if(keyInfo.containsX509Data() && keyInfo.itemX509Data(0).containsCertificate()) { 
			        Certificate cert = keyInfo.itemX509Data(0).itemCertificate(0).getX509Certificate();
			        
			        //ako postoji sertifikat, provera potpisa
			        if(cert != null) 
			        	return signature.checkSignatureValue((X509Certificate) cert);
			    }
			}
		} catch (XMLSecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
}
