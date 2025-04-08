package com.atlas32.infrastructure.secret;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.api.key.repository.ApiKeyRepository;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

  private final ApiKeyRepository apiKeyRepository;

  public ApiKeyService(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  public ApiKeyEntity createApiKey(String name, String rawApiKey, String passphrase)
      throws Exception {
    // 1) Generate a random salt
    byte[] salt = generateRandomSalt(); // e.g., 16 bytes

    // 2) Derive the AES key
    SecretKey secretKey = deriveKey(passphrase, salt);

    // 3) Encrypt the rawApiKey
    byte[] encryptedBytes = encryptAes(rawApiKey.getBytes(StandardCharsets.UTF_8), secretKey);

    // 4) Save to DB: name, salt in base64, encryptedValue in base64
    ApiKeyEntity entity = ApiKeyEntity.builder()
        .name(name)
        .encryptedValue(Base64.getEncoder().encodeToString(encryptedBytes))
        .salt(Base64.getEncoder().encodeToString(salt))
        .build();

    return apiKeyRepository.save(entity);
  }

  public String retrieveApiKey(Long apiKeyId, String passphrase) throws Exception {
    ApiKeyEntity entity = apiKeyRepository.findById(apiKeyId)
        .orElseThrow(() -> new RuntimeException("ApiKey not found"));

    byte[] salt = Base64.getDecoder().decode(entity.getSalt());
    byte[] encryptedBytes = Base64.getDecoder().decode(entity.getEncryptedValue());

    SecretKey secretKey = deriveKey(passphrase, salt);

    byte[] decrypted = decryptAes(encryptedBytes, secretKey);
    return new String(decrypted, StandardCharsets.UTF_8);
  }

  // Helper methods

  private byte[] generateRandomSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16]; // 128-bit salt
    random.nextBytes(salt);
    return salt;
  }

  private SecretKey deriveKey(String passphrase, byte[] salt) throws Exception {
    /* Use PBKDF2 with SHA-256 to derive the AES key */
    int iterations = 65536;
    int keyLength = 256; // AES-256

    PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLength);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] keyBytes = factory.generateSecret(spec).getEncoded();

    return new SecretKeySpec(keyBytes, "AES");
  }

  private byte[] encryptAes(byte[] plaintext, SecretKey secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] iv = cipher.getIV(); // Retrieve the generated IV

    byte[] cipherText = cipher.doFinal(plaintext);

    ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
    byteBuffer.putInt(iv.length); // Store IV length (useful if it could vary)
    byteBuffer.put(iv);           // Store IV
    byteBuffer.put(cipherText);   // Store ciphertext
    return byteBuffer.array();
  }

  private byte[] decryptAes(byte[] cipherMessage, SecretKey secretKey) throws Exception {
    ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);

    int ivLength = byteBuffer.getInt();
    // Basic check for typical GCM IV lengths (e.g., 12 or 16 bytes)
    if (ivLength < 12 || ivLength > 16) {
      throw new IllegalArgumentException("Invalid IV length encountered: " + ivLength);
    }
    byte[] iv = new byte[ivLength];
    byteBuffer.get(iv);

    byte[] cipherText = new byte[byteBuffer.remaining()];
    byteBuffer.get(cipherText);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    // Specify GCM parameters using the retrieved IV
    GCMParameterSpec spec = new GCMParameterSpec(128, iv); // 128 is the tag length (bits)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

    return cipher.doFinal(cipherText);
  }
}