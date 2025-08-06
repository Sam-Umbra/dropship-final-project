package br.dev.kajosama.dropship;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class DropshipApplicationTests {

    @Test
    void contextLoads() {

        criptogaSenha();
        GenerateJwtKey();

    }

    public void criptogaSenha() {
        /**
         * MÃ©todo para criptografar senha formato RAW para BCrypt Neste exemplo
         * utiliza-se senha123 para inicializar os usuÃ¡rios na base H2 para
         * testes
         *
         * VocÃª deve manualmente pegar essa senha criptografada e adicionar na
         * base.s
         */

        System.out.println("------------------------------------------------");
        System.out.println("\n\n\n*** Teste carregado! ***\n\n\n");

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String password = bcrypt.encode("senha123");
        System.out.printf("\n\nPassword Raw   : %s\n", "senha123");
        System.out.printf("    Password Cripto: %s\n\n", password);
        System.out.println("------------------------------------------------\n\n");
    }

    public void GenerateJwtKey() {
        byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("CHAVE BASE64:");
        System.out.println(base64Key);
        System.out.println("TAMANHO EM BYTES: " + key.length); // deve ser 64
    }

}
