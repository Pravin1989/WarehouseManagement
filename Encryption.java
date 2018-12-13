package com.pravin.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;

public class Test {
	public static void main(String[] args) throws Exception {
		//Security.setProperty("crypto.policy", "unlimited");
		//int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
		Test test = new Test();
		test.execute();
	}

	public void execute() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");

		X509Certificate certificate = (X509Certificate) certFactory
				.generateCertificate(new FileInputStream("C:/Technical/Baeldung.cer"));

		char[] keystorePassword = "password".toCharArray();
		char[] keyPassword = "password".toCharArray();

		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream("C:/Technical/Baeldung.p12"), keystorePassword);
		PrivateKey key = (PrivateKey) keystore.getKey("baeldung", keyPassword);

		String secretMessage = "My password is 123456Seven";
		System.out.println("Original Message : " + secretMessage);
		byte[] stringToEncrypt = secretMessage.getBytes();
		System.out.println("Converted to Bytes : " + stringToEncrypt);
		byte[] encryptedData = encryptData(stringToEncrypt, certificate);
		System.out.println("Encryption Done : " + stringToEncrypt);
		System.out.println("Encrypted Message : " + new String(encryptedData));
		byte[] rawData = decryptData(encryptedData, key);
		String decryptedMessage = new String(rawData);
		System.out.println("Decrypted Message : " + decryptedMessage);
	}

	public static byte[] encryptData(byte[] data, X509Certificate encryptionCertificate) {
		byte[] encryptedData = null;
		try {

			if (null != data && null != encryptionCertificate) {
				CMSEnvelopedDataGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataGenerator();

				JceKeyTransRecipientInfoGenerator jceKey = new JceKeyTransRecipientInfoGenerator(encryptionCertificate);
				cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey);
				CMSTypedData msg = new CMSProcessableByteArray(data);
				OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider("BC")
						.build();
				CMSEnvelopedData cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(msg, encryptor);
				encryptedData = cmsEnvelopedData.getEncoded();
			}
			return encryptedData;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;
	}

	public static byte[] decryptData(byte[] encryptedData, PrivateKey decryptionKey)  {

		byte[] decryptedData = null;
		try {
			if (null != encryptedData && null != decryptionKey) {
				CMSEnvelopedData envelopedData = new CMSEnvelopedData(encryptedData);

				Collection<RecipientInformation> recipients = envelopedData.getRecipientInfos().getRecipients();
				KeyTransRecipientInformation recipientInfo = (KeyTransRecipientInformation) recipients.iterator().next();
				JceKeyTransRecipient recipient = new JceKeyTransEnvelopedRecipient(decryptionKey);

				return recipientInfo.getContent(recipient);
			}
			return decryptedData;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedData;
	}
}
