package com.projekat.bezbednostWeb.certificate;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertificateGenerator {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static X509Certificate generate(X500Name issuerName, PrivateKey issuerPrivateKey, X500Name subjectName, PublicKey subjectPublicKey, BigInteger id, Date startDate, Date endDate) {		
		try {
			JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			signerBuilder = signerBuilder.setProvider("BC");
			
			ContentSigner contentSigner = signerBuilder.build(issuerPrivateKey);
			X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuerName, id, startDate, endDate, subjectName, subjectPublicKey);
			
			X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
			
			JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
			certificateConverter = certificateConverter.setProvider("BC");
			
			return certificateConverter.getCertificate(certificateHolder);
			
		} catch (OperatorCreationException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
