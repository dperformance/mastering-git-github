import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;

public class UserTest {
    
    public static void main(String[] args) {
        System.out.println("진입");
        User user = new User();
        if (user == null || "".equals(user.toString().isEmpty())) {
            System.out.println("dzdz");
        }
    }
}

public void login(HttpServletRequest request, LoginVO loginVO) throws Exception {
    Private privateKey = (PrivateKey)request.getSession().getAttribute("_RSA_WEB_Key_");
    if (privateKey == null) {
        throw new EgovBizException("세션이 종료되었습니다.");
    }

    try {
        if(loginVO.getId() != null && !loginVO.getId().equals("")) {
            String id = decryptRSA(privateKey, loginVO.getId());
            loginVO.setId(id);
        }
        if(loginVO.getPassword() != null && !loginVO.getPassword().equals("")) {
            String id = decryptRSA(privateKey, loginVO.getPassword());
            loginVO.setPassword(id);
        }
    } catch (Exception e) {
        logger.info("RSA 복호화 오류 발생");    
    }
}

public String decryptRSA(PrivateKey privateKey, String encryptValue) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    byte[] envryptedBytes = hexToByteArray(encryptValue);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);

    byte[] decryptBytes = cipher.doFinal(envryptedBytes);

    return new String(decryptBytes, StandardCharsets.UTF_8);
}

public static byte[] hexToByteArray(String val) {
    if(val == null || val.length() % 2 != 0) {
        return new byte[] {};
    }
    byte[] bytes = new byte[val.length() / 2];
    for (int idx = 0; idx < val.length(); idx += 2) {
        byte value = (byte) Integer.parseInt(val.substring(idx, idx + 2), 16);
        bytes[(int)Math.floor(idx/2)] = value;
    }
    return bytes;
}

Failed to connect to the controller : the controller is not available at localhost:10190: java.net.ConnectException: WFLYPRT0053: Could not connect to http-remoting://localhost:10190. The connection failed: 연결이 거부됨
SSH : EXEC: completed after 1,000 ms
SSH : Disconnecting configuration [jboss-dev-was] ...
ERROR: Exception when puplishing, exception message [Exec exit status not zero. Status [1]]
Build step 'Send files or execute commands over SSH' changed build result to UNSTABLE
Parsing POMs
Discovered a new module kt:econ econModules changed, recalculating dependency graph
Established TCP socket on 39333
[econ-dev2] $ /usr/lib/jvm/java-1.8.0/bin/java -cp /var/lib/jenkins/workspace/econ-dev2/pom.xml clean package
[INFO] Scanning for projects...
[WARNING] Some problems were encountered while building the effective model for kt:econ:war:1.0.0
[WARNING] 'dependencies.dependency.(groupId.artifactId:type:classifier)' must be unique: project:cxf-rt-wa-policy:jar -> duplicate declaration of version 3.3.0 @ line 443, column 15
[WARNUNG] 'build.plugins.plugin.version' for org.apache.maven.plugins:maven-surefire-plugin is missing. @ line 842, column 21
[WARNING] It is highly recommended to fix these problems because they threaten the stability of your build.
[WARNING] For this reason, future Maven versions might no longer support building such malformed projects.
[INFO] ----------------< kt:econ >--------------------
[INFO] Building econ 1.0.0
[INFO] ------------------- [ war ] ------------------
[INFO] Downloading from mvn2s: https://repo1.maven.org/maven2/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.pom
[WARNING] Failed to write tracking file /home/jboss/.m2/repository_3_10/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.pom.lastUpdated
java.to.FileNotFoundException: /home/jboss/.m2/repository_3_10/commons-beanutils/commons-beanutils/1.9.2/commons-beanutils-1.9.2.pom.lastUpdated (허가 거부)

