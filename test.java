import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

private void setRsa(HttpServletRequest request) throws InvalidKeySpecException {
    HttpSession session = request.getSession();

    SecureRandom secureRandom = new SecureRandom();
    KeyPairGenerator generator;

    try {
        generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, secureRandom);

        KeyPait keyPair = generator.getKeyPair();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        session.setAttribute("_RSA_WEB_Key_", privateKey);

        RSAPublicKeySpec publicSpec = (RSAPublickKeySpec)keyFactory.getKeySpec(publickey, RSAPublickKeySpec.class);

        String publicKeyModulus = publicSpec.getModulus().toString(16);
        String publicKeyExponent = publicSpec.getPublicExponent().toString(16);

        request.setAttribute("RSAModuus", publicKeyModulus);
        request.setAttirubute("RSAExponent", publicKeyExponent);


    } catch (NoSuchAlgorithmException e) {
        logger.error("======= setRsa Error", e);
    }
}
UsernamePasswordAuthenticationFilter springSecurity = null;
ApplicationContext act = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());

Map<String, UsernamePasswordAuthenticationFilter> beans = act.getBeansOfType(UsernamePasswordAuthenticationFilter.class);

if (beans.size() > 0) {
    springSecurity = (UsernamePasswordAuthenticationFilter) beans.values().toArray()[0];
    springSecurity.setUsernameParameter("egov_security_username");
    springSecurity.setPasswordParameter("egov_security_password");
    springSecurity.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(request.getServletContext().getContextPath() + "/egov_security_login", "POST"));
} else {
    throw new IllegalStateException("No AuthenticationProcessingFilter");
}

springSecurity.doFilter(new RequestWrapperForSecurity(request, resultVO.getUserSe() + resultVO.getId(), resultVO.getUniqId()),respone, null);


