package com.projekat.bezbednostWeb.certificate;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Component;

@Component
public class JKS {

	private KeyStore keyStore;
	private Certificate certificate;
	private PrivateKey privateKey;
	//private PublicKey publicKey;
	private X500Name subjectName;
	
	
	public JKS(){
		try {
			keyStore = KeyStoreReader.read("data/glavni.jks", "sifra123");
			certificate = keyStore.getCertificate("glavni sertifikat");
			privateKey = (PrivateKey) keyStore.getKey("glavni sertifikat", "sifra123".toCharArray());
			//publicKey = certificate.getPublicKey();
			subjectName = new JcaX509CertificateHolder((X509Certificate)certificate).getSubject();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void generateSignedJKS(Integer id, String email, String password) {
		
		KeyPair keyPair = generateKeyPair();
		
		Calendar cal = Calendar.getInstance();
		Date startDate = cal.getTime();
		cal.add(Calendar.YEAR, 1);
		Date endDate = cal.getTime();
		
		// generate newCertificate signed by mainCertificate
		X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
		nameBuilder.addRDN(BCStyle.E, email);
		nameBuilder.addRDN(BCStyle.UID, id.toString());
		X500Name newName = nameBuilder.build();
		Certificate newCertificate = CertificateGenerator.generate(subjectName, privateKey, newName, keyPair.getPublic(), BigInteger.valueOf(id.intValue()), startDate, endDate);
		
		KeyStoreWriter.write(email, password, newCertificate, keyPair.getPrivate());
	}
	
	private KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			KeyPair pair = keyPairGenerator.generateKeyPair();
			return pair;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
