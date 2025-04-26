package com.atlas32.infrastructure.secret;

import com.atlas32.infrastructure.db.api.key.entity.ApiKeyEntity;
import com.atlas32.infrastructure.db.api.key.repository.ApiKeyRepository;
import com.atlas32.infrastructure.db.user.entity.UserEntity;
import com.atlas32.infrastructure.db.user.repository.UserRepository;
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

  private final UserRepository userRepository;

  public ApiKeyService(ApiKeyRepository apiKeyRepository,
      UserRepository userRepository) {
    this.apiKeyRepository = apiKeyRepository;
    this.userRepository = userRepository;
  }

  public ApiKeyEntity createApiKeyForUser(Long userId, String name, String rawApiKey,
      String passphrase)
      throws Exception {
    // 1) Generate salt and derive the key (AES)
    byte[] salt = generateRandomSalt();
    SecretKey secretKey = deriveKey(passphrase, salt);

    // 2) We encrypt the rawApiKey
    byte[] encryptedBytes = encryptAes(rawApiKey.getBytes(StandardCharsets.UTF_8), secretKey);

    // 3) Create the entity
    ApiKeyEntity entity = ApiKeyEntity.builder()
        .name(name)
        .encryptedValue(Base64.getEncoder().encodeToString(encryptedBytes))
        .salt(Base64.getEncoder().encodeToString(salt))
        .build();

    // Saving the key
    entity = apiKeyRepository.save(entity);

    // 4) Assign it to the user
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    user = user.withApiKey(entity);
    userRepository.save(user);

    return entity;
  }

  public String retrieveApiKey(ApiKeyEntity apiKeyEntity, String passphrase) throws Exception {
    byte[] salt = Base64.getDecoder().decode(apiKeyEntity.getSalt());
    byte[] encryptedBytes = Base64.getDecoder().decode(apiKeyEntity.getEncryptedValue());

    SecretKey secretKey = deriveKey(passphrase, salt);
    byte[] decrypted = decryptAes(encryptedBytes, secretKey);

    return new String(decrypted, StandardCharsets.UTF_8);
  }

  private byte[] generateRandomSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return salt;
  }

  private SecretKey deriveKey(String passphrase, byte[] salt) throws Exception {
    int iterations = 65536;
    int keyLength = 256;

    PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLength);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] keyBytes = factory.generateSecret(spec).getEncoded();

    return new SecretKeySpec(keyBytes, "AES");
  }

  private byte[] encryptAes(byte[] plaintext, SecretKey secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] iv = cipher.getIV();

    byte[] cipherText = cipher.doFinal(plaintext);

    ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
    byteBuffer.putInt(iv.length);
    byteBuffer.put(iv);
    byteBuffer.put(cipherText);
    return byteBuffer.array();
  }

  private byte[] decryptAes(byte[] cipherMessage, SecretKey secretKey) throws Exception {
    ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);

    int ivLength = byteBuffer.getInt();
    if (ivLength < 12 || ivLength > 16) {
      throw new IllegalArgumentException("Invalid IV length encountered: " + ivLength);
    }
    byte[] iv = new byte[ivLength];
    byteBuffer.get(iv);

    byte[] cipherText = new byte[byteBuffer.remaining()];
    byteBuffer.get(cipherText);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec spec = new GCMParameterSpec(128, iv);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

    return cipher.doFinal(cipherText);
  }
}