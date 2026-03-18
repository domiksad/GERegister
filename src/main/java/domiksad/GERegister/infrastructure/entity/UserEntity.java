package domiksad.GERegister.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;
  private String email;
  private byte[] salt;
  private byte[] passwordHash;

  public UserEntity(String name, String email, byte[] salt, byte[] passwordHash) {
    this.name = name;
    this.email = email;
    this.salt = salt;
    this.passwordHash = passwordHash;
  }

  public boolean matchesPassword(String password) {
    if (password == null || salt == null || passwordHash == null) return false;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(salt);
      byte[] computedHash = md.digest(password.getBytes());
      return Arrays.equals(computedHash, passwordHash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Hashing algorithm not found", e);
    }
  }

  public String getPasswordHashBase64() {
    return Base64.getEncoder().encodeToString(passwordHash);
  }
}
